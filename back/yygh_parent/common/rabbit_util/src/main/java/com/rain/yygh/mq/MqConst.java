package com.rain.yygh.mq;

public class MqConst {
    /**
     * 订单
     */
    // 交换机
    public static final String EXCHANGE_DIRECT_ORDER = "exchange.direct.order";
    // 路由key
    public static final String ROUTING_ORDER = "order";
    // 队列
    public static final String QUEUE_ORDER  = "queue.order";
    
    /**
     * 短信
     */
    public static final String EXCHANGE_DIRECT_SMS = "exchange.direct.sms";
    public static final String ROUTING_SMS_ITEM = "sms.item";
    public static final String QUEUE_SMS_ITEM  = "queue.sms.item";

    //定时任务
    public static final String EXCHANGE_DIRECT_TASK = "exchange.direct.task";
    public static final String ROUTING_TASK_8 = "task.8";
    //队列
    public static final String QUEUE_TASK_8 = "queue.task.8";
}