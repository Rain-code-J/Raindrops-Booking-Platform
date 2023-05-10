package com.rain.yygh.order.controller;

import com.rain.yygh.common.result.R;
import com.rain.yygh.order.service.PaymentService;
import com.rain.yygh.order.service.WeixinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user/order/weixin")
public class WeixinController {

    @Autowired
    private WeixinService weixinService;

    @Autowired
    private PaymentService paymentService;

    /**
     * 创建微信二维码
     *
     * @param orderId 订单id
     * @return {@link R}
     */
    @GetMapping("/createNative/{orderId}")
    public R createNative(@PathVariable Long orderId){
        Map<String,Object> map = weixinService.createNative(orderId);
        return R.ok().data(map);
    }

    /**
     * 查询支付状态
     *
     * @param orderId 订单id
     * @return {@link R}
     */
    @GetMapping("/queryPayStatus/{orderId}")
    public R queryPayStatus(@PathVariable Long orderId){
        Map<String,String> resultMap = weixinService.queryPayStatus(orderId);
        if (resultMap == null){
            return R.error().message("查询出错");
        }
        if ("SUCCESS".equals(resultMap.get("trade_state"))){
            // 更新订单状态，处理支付结果
            // 获取商户订单号
            String out_trade_no = resultMap.get("out_trade_no");
            paymentService.updatePaymentInfo(out_trade_no,resultMap);
            return R.ok().message("支付成功");
        }
        return R.ok().message("支付中");
    }
}