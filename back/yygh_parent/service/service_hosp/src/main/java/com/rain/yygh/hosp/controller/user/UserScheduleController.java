package com.rain.yygh.hosp.controller.user;

import com.rain.yygh.common.result.R;
import com.rain.yygh.hosp.service.ScheduleService;
import com.rain.yygh.model.hosp.Schedule;
import com.rain.yygh.vo.hosp.ScheduleOrderVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/hosp/schedule")
public class UserScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation(value = "获取可预约排班数据")
    @GetMapping("/auth/getBookingScheduleRule/{pageNum}/{pageSize}/{hoscode}/{depcode}")
    public R getBookingSchedule(
            @PathVariable Integer pageNum,
            @PathVariable Integer pageSize,
            @PathVariable String hoscode,
            @PathVariable String depcode) {
        Map<String, Object> map = scheduleService.getBookingScheduleRule(pageNum, pageSize, hoscode, depcode);
        return R.ok().data(map);
    }

    @ApiOperation(value = "获取排班数据")
    @GetMapping("/auth/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public R findScheduleList(
            @PathVariable String hoscode,
            @PathVariable String depcode,
            @PathVariable String workDate) {
        List<Schedule> scheduleList = scheduleService.detail(hoscode, depcode, workDate);
        return R.ok().data("scheduleList",scheduleList);
    }

    @ApiOperation(value = "获取排班详情")
    @GetMapping("/getSchedule/{id}")
    public R getSchedule(@PathVariable String id ){
        Schedule schedule = scheduleService.getById(id);
        return R.ok().data("schedule",schedule);
    }

    @GetMapping("/{scheduleId}")
    public ScheduleOrderVo getScheduleById(@PathVariable("scheduleId") String scheduleId){
        ScheduleOrderVo scheduleOrderVo = scheduleService.getScheduleById(scheduleId);
        return scheduleOrderVo;
    }


}
