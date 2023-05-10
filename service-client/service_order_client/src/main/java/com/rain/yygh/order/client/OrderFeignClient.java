package com.rain.yygh.order.client;

import com.rain.yygh.vo.order.OrderCountQueryVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Component
@FeignClient("service-order")
public interface OrderFeignClient {
    @PostMapping("/api/order/orderInfo/inner/getCountMap")
    Map<String,Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo);
}
