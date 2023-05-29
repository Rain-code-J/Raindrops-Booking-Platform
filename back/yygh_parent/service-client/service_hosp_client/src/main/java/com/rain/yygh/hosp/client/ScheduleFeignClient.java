package com.rain.yygh.hosp.client;

import com.rain.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-hosp")
public interface ScheduleFeignClient {
    @GetMapping("/user/hosp/schedule/{scheduleId}")
    ScheduleOrderVo getScheduleById(@PathVariable("scheduleId") String scheduleId);
}
