package com.rain.yygh.hosp.controller.api;

import com.rain.yygh.hosp.bean.Result;
import com.rain.yygh.hosp.service.ScheduleService;
import com.rain.yygh.hosp.utils.HttpRequestHelper;
import com.rain.yygh.model.hosp.Schedule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@Api(tags = "医院排班管理API接口")
@RequestMapping("/api/hosp")
public class ApiScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation("上传排班信息")
    @PostMapping("/saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        // 验证sign
        scheduleService.saveSchedule(paramMap);
        return Result.ok();
    }

    @ApiOperation("分页查询排班信息")
    @PostMapping("/schedule/list")
    public Result findPageSchedule(HttpServletRequest request){
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        Integer page = Integer.parseInt((String)paramMap.get("page"));
        Integer limit = Integer.parseInt((String)paramMap.get("limit"));
        Long timestamp = Long.parseLong((String)paramMap.get("timestamp"));
        String sign = (String) paramMap.get("sign");

        Schedule schedule = new Schedule();
        schedule.setHoscode((String) paramMap.get("hoscode"));

        // 参数验证
        // 使用StringUtils
        // sign验证
        Page<Schedule> pageList = scheduleService.findPageSchedule(page,limit,schedule);

        return Result.ok(pageList);
    }

    @ApiOperation("删除排班信息")
    @PostMapping("/schedule/remove")
    public Result removeSchedule(HttpServletRequest request){
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String hoscode = (String)paramMap.get("hoscode");
        String hosScheduleId = (String)paramMap.get("hosScheduleId");
        // 参数验证
        Long timestamp = Long.parseLong((String)paramMap.get("timestamp"));
        String sign = (String) paramMap.get("sign");

        scheduleService.remove(hoscode,hosScheduleId);

        return Result.ok();

    }
}
