package com.rain.yygh.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rain.yygh.model.order.OrderInfo;
import com.rain.yygh.vo.order.OrderCountQueryVo;
import com.rain.yygh.vo.order.OrderQueryVo;

import java.util.Map;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author rain
 * @since 2023-05-07
 */
public interface OrderInfoService extends IService<OrderInfo> {

    Long saveOrder(String scheduleId, Long patientId);

    Page<OrderInfo> getOrderPage(Integer pageNum, Integer pageSize, OrderQueryVo orderQueryVo);

    OrderInfo getOrderInfo(Long orderId);

    void cancelOrder(Long orderId);

    void patientRemind();


    Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo);

}
