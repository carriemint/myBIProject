package com.carrie.springbootinit.bimq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class BiMessageProducerTest {

    @Resource
    private BiMessageProducer biMessageProducer;


    @Test
    void sendMessage() {
        biMessageProducer.sendMessage("信息来咯");
    }
}