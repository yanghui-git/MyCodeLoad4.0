package com.test.pulsar;

import lombok.extern.java.Log;
import org.apache.pulsar.client.api.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Date;

/**
 * 简单发送-接收模式
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PulsarApplication.class)
@Log
public class PulsarTest {

    @Autowired
    private PulsarClient client;


    /**
     * 接收消息:同步 阻塞
     */
    @Test
    public void testConsumer() throws PulsarClientException {
        Consumer consumer = client
                .newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription")
                .consumerName("consumer-demo1")
                .subscribe();
       /* int j = 1;*/

        while (true) {
            // Wait for a message
            Message msg = consumer.receive();
            try {
                // Do something with the message

                //测试消费失败处理
            /*    int i = 1 / j;
                j--;*/

                System.out.println("Message received: " + new String(msg.getData()));

                // Acknowledge the message so that it can be deleted by the message broker
                consumer.acknowledge(msg);
            } catch (Exception e) {
                // Message failed to process, redeliver later
                log.info("测试消费失败处理" + e);
                consumer.negativeAcknowledge(msg);
            }
        }
    }

    /**
     * 接收消息:异步 不阻塞主线程
     */
    @Test
    public void testConsumer2() throws IOException {
        MessageListener myMessageListener = (consumer, msg) -> {
            try {
                System.out.println("Message received: " + new String(msg.getData()));
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };
        Consumer consumer = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription")
                .messageListener(myMessageListener)
                .subscribe();
        //
        System.in.read();
    }

    /**
     * 测试简单发送消息
     */
    @Test
    public void testProducer() throws PulsarClientException {
        Producer<String> stringProducer = client
                .newProducer(Schema.STRING)
                .topic("my-topic")
                .producerName("produce-demo1")
                .create();
        stringProducer.send("My message" + "发送消息时间" + new Date());

    }

    /**
     * 测试简单发送消息
     */
    @Test
    public void testProducer2() throws PulsarClientException {
        Producer<byte[]> producer = client.newProducer()
                .topic("my-topic")
                .create();

// You can then send messages to the broker and topic you specified:

        for (int i = 0; i < 100; i++) {
            producer.send(("My message 2 " + new Date()).getBytes());
        }
    }

}
