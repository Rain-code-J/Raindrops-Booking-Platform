package com.rain.yygh.mq;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class RabbitMQConfig {


    /**
     * 将生产者发送的pojo类 --> json
     * 将消费者接受的json --> pojo类
     *
     * @return {@link MessageConverter}
     */
    @Bean
    public MessageConverter getMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
