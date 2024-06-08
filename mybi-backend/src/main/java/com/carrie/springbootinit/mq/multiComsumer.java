package com.carrie.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class multiComsumer {

    private static final String TASK_QUEUE_NAME = "multi_queue";

    public static void main(String[] argv) throws Exception {
        //建立连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        final Connection connection = factory.newConnection();
        for(int i=0;i<2;i++) {
            final Channel channel = connection.createChannel();

            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            //控制单个消费者处理任务积压数量：每个channel最多同时处理一个任务
            channel.basicQos(1);
            //如何处理消息
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                try {
                    System.out.println(" [x] Received '" + message + "'");
                    //doWork(message);处理工作
                    //停20s 模拟工人处理能力有限
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    System.out.println(" [x] Done");
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            //开启消费监听
            channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
            });
        }
    }

    private static void doWork(String task) {
        for (char ch : task.toCharArray()) {
            if (ch == '.') {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}