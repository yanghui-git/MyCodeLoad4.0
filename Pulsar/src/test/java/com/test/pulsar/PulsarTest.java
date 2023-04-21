package com.test.pulsar;

import com.alibaba.fastjson.JSON;
import lombok.extern.java.Log;
import org.apache.pulsar.client.api.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
                .subscriptionName("my-subscription2")
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

                //int i =1/0;
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
                int i =1/0;
                consumer.acknowledge(msg);
            } catch (Exception e) {
                log.info("消费失败"+e);
                consumer.negativeAcknowledge(msg);
            }
        };
        Consumer consumer = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription")
                .messageListener(myMessageListener)
                .subscribe();
      //  System.out.println("监听器方式,不阻塞线程");
        System.in.read();
    }


    /**
     * loadConf创建消费者
     */
    @Test
    public void testConsumer222() throws IOException {
        MessageListener myMessageListener = (consumer, msg) -> {
            try {
                System.out.println("Message received: " + new String(msg.getData()));
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };
        Map<String, Object> config1 = new HashMap<>();
        config1.put("subscriptionName", "consumer-demo1");
        config1.put("topicNames", Arrays.asList(new String[]{"my-topic"}));

        Consumer consumer = client
                .newConsumer()
                .loadConf(config1)
                .messageListener(myMessageListener)
                .subscribe();
        System.out.println("loadConf方式");
        System.in.read();
    }

    /**
     * 创建消费者
     */
    @Test
    public void testConsumer22() throws Exception{
        Consumer consumer = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription")
                .subscribe();
        while (true) {
            // Wait for a message
            Message msg = consumer.receive();
            try {
                // Do something with the message
                System.out.println("Message received: " + new String(msg.getData()));
                // Acknowledge the message so that it can be deleted by the message broker
                consumer.acknowledge(msg);
            } catch (Exception e) {
                // Message failed to process, redeliver later
                consumer.negativeAcknowledge(msg);
            }
        }
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
     * 测试同步发送
     */
    @Test
    public void testProducer22() throws Exception {
        Producer<String> stringProducer = client
                .newProducer(Schema.STRING)
                .topic("my-topic")
                .producerName("produce-demo1")
                .create();
        MessageId messageId = stringProducer.send("My message" + "发送消息时间" + new Date());
        System.out.println("消息同步发送---");
        System.in.read();
    }


    /**
     * 测试异步发送
     */
    @Test
    public void testProducer222() throws Exception {
        Producer<String> stringProducer = client
                .newProducer(Schema.STRING)
                .topic("my-topic")
                .producerName("produce-demo1")
                .create();
        CompletableFuture<MessageId> messageIdCompletableFuture = stringProducer.sendAsync(
                "异步发送的消息");
        System.in.read();
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
