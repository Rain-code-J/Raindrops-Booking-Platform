package com.rain.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.wxpay.sdk.WXPayUtil;
import com.rain.yygh.enums.PaymentTypeEnum;
import com.rain.yygh.enums.RefundStatusEnum;
import com.rain.yygh.model.order.OrderInfo;
import com.rain.yygh.model.order.PaymentInfo;
import com.rain.yygh.model.order.RefundInfo;
import com.rain.yygh.order.mapper.OrderInfoMapper;
import com.rain.yygh.order.prop.WeixinProperties;
import com.rain.yygh.order.service.PaymentService;
import com.rain.yygh.order.service.RefundInfoService;
import com.rain.yygh.order.service.WeixinService;
import com.rain.yygh.order.utils.HttpClient;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class WeixinServiceImpl implements WeixinService {
    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private WeixinProperties weixinProperties;

    @Autowired
    private RefundInfoService refundInfoService;

    @Override
    public Map<String, Object> createNative(Long orderId) {
        // 1.通过orderId获取订单信息
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        // 2.调用payment保存支付记录信息
        paymentService.savePaymentInfo(orderInfo, PaymentTypeEnum.WEIXIN.getStatus());
        // 3.请求微信服务器，获取微信支付二维码的url
        //      3.1.构建map
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid",weixinProperties.getAppid());
        paramMap.put("mch_id",weixinProperties.getPartner());
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        Date reserveDate = orderInfo.getReserveDate();
        String reserveDateString = new DateTime(reserveDate).toString("yyyy/MM/dd");
        String body = reserveDateString + "就诊"+ orderInfo.getDepname();
        paramMap.put("body",body);
        paramMap.put("out_trade_no",orderInfo.getOutTradeNo());
        // 测试  订单总金额，单位为分
        paramMap.put("total_fee","1");
        paramMap.put("spbill_create_ip", "127.0.0.1");
        paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
        paramMap.put("trade_type", "NATIVE");

        try {
            // 发送数据
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, weixinProperties.getPartnerkey()));
            client.setHttps(true);
            client.post();
            // 接收数据
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //4、封装返回结果集
            Map map = new HashMap<>();
            map.put("orderId", orderId);
            map.put("totalFee", orderInfo.getAmount());
            map.put("resultCode", resultMap.get("result_code"));
            map.put("codeUrl", resultMap.get("code_url"));
            if(null != resultMap.get("result_code")) {
                //微信支付二维码2小时过期，可采取2小时未支付取消订单
                redisTemplate.opsForValue().set(orderId.toString(), map, 1000, TimeUnit.MINUTES);
            }
            // 4.返回给Controller
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, String> queryPayStatus(Long orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);

        // 设置请求参数
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid",weixinProperties.getAppid());
        paramMap.put("mch_id",weixinProperties.getPartner());
        paramMap.put("out_trade_no",orderInfo.getOutTradeNo());
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr());

        try {
            // 发送请求
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, weixinProperties.getPartnerkey()));
            client.setHttps(true);
            client.post();
            // 返回数据
            String content = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Boolean refund(Long orderId) {
        // 通过订单id查询支付信息
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderId);
        PaymentInfo paymentInfo = paymentService.getOne(queryWrapper);
        // 通过支付信息，保存退款信息到退款表中
        RefundInfo refundInfo = refundInfoService.saveRefundInfo(paymentInfo);
        // 从退款信息表中查询退款状态，如果已经退款，那么直接返回true
        if (refundInfo.getRefundStatus() == RefundStatusEnum.REFUND.getStatus()){
            return true;
        }

        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid",weixinProperties.getAppid());
        paramMap.put("mch_id",weixinProperties.getPartner());
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
        paramMap.put("out_trade_no", paymentInfo.getOutTradeNo());
        paramMap.put("out_refund_no","tk"+paymentInfo.getOutTradeNo());
        //       paramMap.put("total_fee",paymentInfo.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
        //       paramMap.put("refund_fee",paymentInfo.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
        paramMap.put("total_fee","1");
        paramMap.put("refund_fee","1");

        try {
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/secapi/pay/refund");
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, weixinProperties.getPartnerkey()));
            client.setHttps(true);
            client.setCert(true);
            client.setCertPassword(weixinProperties.getPartner());
            client.post();

            // 返回数据
            String content = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            if (resultMap != null && "SUCCESS".equals(resultMap.get("result_code"))){
                refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
                refundInfo.setCallbackTime(new Date());
                refundInfo.setCallbackContent(JSONObject.toJSONString(resultMap));
                refundInfo.setTradeNo(resultMap.get("refund_id"));
                refundInfoService.updateById(refundInfo);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}