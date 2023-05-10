package com.rain.yygh.hosp.repository;

import com.rain.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScheduleRepository extends MongoRepository<Schedule,String> {

    Schedule findScheduleByHoscodeAndDepcodeAndHosScheduleId(String hoscode, String depcode, String hosScheduleId);
    Schedule findScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    Schedule findByHosScheduleId(String scheduleId);
}
