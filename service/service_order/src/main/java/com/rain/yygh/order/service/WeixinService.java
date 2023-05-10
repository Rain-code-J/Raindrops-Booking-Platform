package com.rain.yygh.order.service;

import java.util.Map;

public interface WeixinService {

    Map<String, Object> createNative(Long orderId);

    Map<String, String> queryPayStatus(Long orderId);

    Boolean refund(Long orderId);
}