package com.rain.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rain.yygh.model.order.OrderInfo;
import com.rain.yygh.model.order.PaymentInfo;

import java.util.Map;

public interface PaymentService extends IService<PaymentInfo> {

    void savePaymentInfo(OrderInfo orderInfo, Integer paymentType);

    void updatePaymentInfo(String out_trade_no,  Map<String, String> paramMap);

}