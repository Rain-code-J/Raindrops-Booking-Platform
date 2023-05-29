package com.rain.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rain.yygh.enums.OrderStatusEnum;
import com.rain.yygh.enums.PaymentStatusEnum;
import com.rain.yygh.model.order.OrderInfo;
import com.rain.yygh.model.order.PaymentInfo;
import com.rain.yygh.order.mapper.OrderInfoMapper;
import com.rain.yygh.order.mapper.PaymentMapper;
import com.rain.yygh.order.service.PaymentService;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

@Service
public class PaymentServiceImpl extends
        ServiceImpl<PaymentMapper, PaymentInfo> implements PaymentService {

    @Resource
    private OrderInfoMapper orderInfoMapper;

    /**
     * 保存交易记录
     * @param orderInfo
     * @param paymentType 支付类型（1：微信 2：支付宝）
     */
    @Override
    public void savePaymentInfo(OrderInfo orderInfo, Integer paymentType) {
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderInfo.getId());
        queryWrapper.eq("payment_type", paymentType);
        Integer count = baseMapper.selectCount(queryWrapper);
        if(count >0) {
            return;
        }
        // 保存交易记录
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
        String subject = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")+"|"+orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle();
        paymentInfo.setSubject(subject);
        paymentInfo.setTotalAmount(orderInfo.getAmount());
        baseMapper.insert(paymentInfo);
    }

    @Transactional
    @Override
    public void updatePaymentInfo(String out_trade_no, Map<String, String> paramMap) {
        // 首先：更新订单状态
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_trade_no",out_trade_no);
        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);

        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderInfoMapper.updateById(orderInfo);

        // 更新支付记录
        QueryWrapper<PaymentInfo> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("out_trade_no",out_trade_no);
        PaymentInfo paymentInfo = baseMapper.selectOne(queryWrapper1);

        paymentInfo.setTradeNo(paramMap.get("transaction_id"));
        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(paramMap.toString());
        baseMapper.updateById(paymentInfo);
    }
}