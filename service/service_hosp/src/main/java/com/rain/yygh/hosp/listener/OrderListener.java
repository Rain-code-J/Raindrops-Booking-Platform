package com.rain.yygh.hosp.listener;

import com.rabbitmq.client.Channel;
import com.rain.yygh.hosp.service.ScheduleService;
import com.rain.yygh.model.hosp.Schedule;
import com.rain.yygh.mq.MqConst;
import com.rain.yygh.mq.RabbitMQService;
import com.rain.yygh.vo.msm.MsmVo;
import com.rain.yygh.vo.order.OrderMqVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RabbitMQService rabbitMQService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = MqConst.QUEUE_ORDER,durable = "true"),
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_ORDER),// durable默认为true
                    key = {MqConst.ROUTING_ORDER}
            )
    })
    public void consume(OrderMqVo orderMqVo, Message message, Channel channel){
        String scheduleId = orderMqVo.getScheduleId();
        Integer availableNumber = orderMqVo.getAvailableNumber();
        MsmVo msmVo = orderMqVo.getMsmVo();
        if(availableNumber != null){
            boolean flag= scheduleService.updateAvailableNumber(scheduleId,availableNumber);

        }else{
            scheduleService.cancelSchedule(scheduleId);
        }

        if(msmVo != null){
            rabbitMQService.sendMessage(MqConst.EXCHANGE_DIRECT_SMS,MqConst.ROUTING_SMS_ITEM,msmVo);
        }
    }
}
