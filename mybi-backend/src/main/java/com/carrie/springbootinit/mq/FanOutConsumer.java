package com.carrie.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class FanOutConsumer {
  private static final String EXCHANGE_NAME = "fanout-exchange";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
    //创建队列，随机分配一个队列名称
    //指定队列名称
    //channel.queueDeclare(queueName,true,false,false,null);
    String queueName1 = channel.queueDeclare().getQueue();
    channel.queueBind(queueName1, EXCHANGE_NAME, "");

    String queueName2 = channel.queueDeclare().getQueue();
    channel.queueBind(queueName2, EXCHANGE_NAME, "");

    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        System.out.println(" 1111[x] Received '" + message + "'");
    };
    DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), "UTF-8");
      System.out.println(" 2222[x] Received '" + message + "'");
    };
    channel.basicConsume(queueName1, true, deliverCallback1, consumerTag -> { });
    channel.basicConsume(queueName2, true, deliverCallback2, consumerTag -> { });
  }
}