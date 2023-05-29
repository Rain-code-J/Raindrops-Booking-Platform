package com.rain.yygh.hosp.controller.admin;

import com.rain.yygh.common.result.R;
import com.rain.yygh.hosp.service.ScheduleService;
import com.rain.yygh.model.hosp.Schedule;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/schedule")
@Api(tags = "排班信息")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/{pageNum}/{pageSize}/{hoscode}/{depcode}")
    public R getSchedulePage(@PathVariable("pageNum") Long pageNum,
                             @PathVariable("pageSize") Long pageSize,
                             @PathVariable("hoscode") String hoscode,
                             @PathVariable("depcode") String depcode) {
        Map<String, Object> map = scheduleService.getSchedulePage(pageNum, pageSize, hoscode, depcode);
        return R.ok().data(map);
    }

    @GetMapping("/{hoscode}/{depcode}/{workdate}")
    public R detail(@PathVariable("hoscode") String hoscode,
                    @PathVariable("depcode") String depcode,
                    @PathVariable("workdate") String workdate){
        List<Schedule> scheduleList = scheduleService.detail(hoscode,depcode,workdate);
        return R.ok().data("scheduleList",scheduleList);
    }

}
