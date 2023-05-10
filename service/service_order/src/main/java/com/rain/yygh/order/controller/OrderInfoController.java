package com.rain.yygh.order.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.wxpay.sdk.WXPayUtil;
import com.rain.yygh.common.result.R;
import com.rain.yygh.common.utils.JwtHelper;
import com.rain.yygh.enums.OrderStatusEnum;
import com.rain.yygh.model.order.OrderInfo;
import com.rain.yygh.order.service.OrderInfoService;
import com.rain.yygh.order.utils.HttpClient;
import com.rain.yygh.vo.order.OrderCountQueryVo;
import com.rain.yygh.vo.order.OrderQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author rain
 * @since 2023-05-07
 */
//创建controller方法
@Api(tags = "订单接口")
@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    @PostMapping("/inner/getCountMap")
    public Map<String,Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo){
        return orderInfoService.getCountMap(orderCountQueryVo);
    }


    @ApiOperation(value = "创建订单")
    @PostMapping("/submitOrder/{scheduleId}/{patientId}")
    public R submitOrder(@PathVariable String scheduleId,
                         @PathVariable Long patientId){
        Long orderId = orderInfoService.saveOrder(scheduleId,patientId);
        return R.ok().data("orderId",orderId);
    }

    //订单列表（条件查询带分页）
    @GetMapping("/auth/{pageNum}/{pageSize}")
    public R getOrderPage(@PathVariable Integer pageNum,
                  @PathVariable Integer pageSize,
                  OrderQueryVo orderQueryVo,
                  @RequestHeader String token) {
        Long userId = JwtHelper.getUserId(token);
        orderQueryVo.setUserId(userId);
        Page<OrderInfo> page = orderInfoService.getOrderPage(pageNum,pageSize,orderQueryVo);
        return R.ok().data("page",page);
    }

    @ApiOperation(value = "获取订单状态")
    @GetMapping("/auth/getStatusList")
    public R getStatusList() {
        return R.ok().data("statusList", OrderStatusEnum.getStatusList());
    }

    //根据订单id查询订单详情
    @GetMapping("/auth/getOrders/{orderId}")
    public R getOrders(@PathVariable Long orderId) {
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);

        return R.ok().data("orderInfo",orderInfo);
    }

    @ApiOperation(value = "取消预约")
    @GetMapping("/auth/cancelOrder/{orderId}")
    public R cancelOrder(@PathVariable Long orderId){
        orderInfoService.cancelOrder(orderId);
        return R.ok();
    }

    @GetMapping("/zdy/aaa/{id}")
    public R cccc(@PathVariable String id){
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("out_refund_no","tk"+id);
        //       paramMap.put("total_fee",paymentInfo.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
        //       paramMap.put("refund_fee",paymentInfo.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
        paramMap.put("total_fee","1");
        paramMap.put("refund_fee","1");
        paramMap.put("appid","wx74862e0dfcf69954");
        paramMap.put("mch_id","1558950191");
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("transaction_id", id);
        //       paramMap.put("total_fee",paymentInfo.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
        //       paramMap.put("refund_fee",paymentInfo.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
        try {
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/secapi/pay/refund");
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setHttps(true);
            client.setCert(true);
            client.setCertPassword("1558950191");
            client.post();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.ok();
    }

}

