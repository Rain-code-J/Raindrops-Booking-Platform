package com.rain.yygh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rain.yygh.model.order.OrderInfo;
import com.rain.yygh.vo.order.OrderCountQueryVo;
import com.rain.yygh.vo.order.OrderCountVo;

import java.util.List;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author rain
 * @since 2023-05-07
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    List<OrderCountVo> selectOrderCount(OrderCountQueryVo orderCountQueryVo);
}
