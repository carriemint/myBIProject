package com.carrie.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

public class singleProducer {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        //创建链接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        //如果修改了用户名，密码和端口
//        factory.setUsername();
//        factory.setPassword();
//        factory.setHost();
        //新建一个链接
        try (Connection connection = factory.newConnection();
             //频道，操作消息队列的client，提供了和消息队列sever建立通信的传输方法。（复用链接，提高传输效率）
             Channel channel = connection.createChannel()) {
            //创建消息队列
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World!";
            //发送消息
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}