package com.rain.yygh.hosp.service;

import com.rain.yygh.model.hosp.Schedule;
import com.rain.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    void saveSchedule(Map<String, Object> paramMap);

    Page<Schedule> findPageSchedule(Integer page, Integer limit, Schedule schedule);

    void remove(String hoscode, String hosScheduleId);

    Map<String, Object> getSchedulePage(Long pageNum, Long pageSize, String hoscode, String depcode);

    List<Schedule> detail(String hoscode, String depcode, String workdate);

    Map<String, Object> getBookingScheduleRule(Integer pageNum, Integer pageSize, String hoscode, String depcode);

    Schedule getById(String id);

    ScheduleOrderVo getScheduleById(String scheduleId);

    Boolean updateAvailableNumber(String scheduleId, Integer availableNumber);

    void cancelSchedule(String scheduleId);

}
