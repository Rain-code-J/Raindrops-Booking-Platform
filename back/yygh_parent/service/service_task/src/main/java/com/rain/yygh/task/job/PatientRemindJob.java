package com.rain.yygh.task.job;

import com.rain.yygh.mq.MqConst;
import com.rain.yygh.mq.RabbitMQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PatientRemindJob {

    @Autowired
    private RabbitMQService rabbitMQService;

    @Scheduled(cron = "*/20 * * * * ?")
    public void printTime(){
//        System.out.println(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
        rabbitMQService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,MqConst.ROUTING_TASK_8,"");
    }
}
