package com.carrie.springbootinit.bimq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class BiMessageProducer {

    @Resource
    public RabbitTemplate rabbitTemplate;

    public void sendMessage(String message ){
        rabbitTemplate.convertAndSend(BiMqConstant.BI_EXCHANGE_NAME,BiMqConstant.BI_ROUTING_KEY,message);
    }

}
