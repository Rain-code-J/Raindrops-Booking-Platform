package com.rain.yygh.order.listener;

import com.rabbitmq.client.Channel;
import com.rain.yygh.mq.MqConst;
import com.rain.yygh.order.service.OrderInfoService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskListener {

    @Autowired
    private OrderInfoService orderInfoService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MqConst.QUEUE_TASK_8, durable = "true"),
            exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_TASK),
            key = MqConst.ROUTING_TASK_8
    ))
    public void patientRemind(Message message, Channel channel) {
        orderInfoService.patientRemind();
    }
}
