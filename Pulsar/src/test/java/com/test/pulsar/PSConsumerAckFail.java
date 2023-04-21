package com.test.pulsar;

import lombok.extern.java.Log;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.pulsar.client.api.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * Pulsar 四种不同模式的下的ACK
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PulsarApplication.class)
@Log
public class PSConsumerAckFail {


    @Autowired
    private PulsarClient client;


    @Test
    public void testProduce() throws Exception {
        Producer<String> producer = client.newProducer(Schema.STRING)
                .topic("my-topic")
                .enableBatching(false)
                .create();
        producer.send("my-topic发送消息" + System.currentTimeMillis());
        System.in.read();
    }

    /**
     * 单机模式下的失败重试
     */
    @Test
    public void testConsumerDanJI() throws IOException {
        MessageListener myMessageListener = (consumer, msg) -> {
            try {
                System.out.println("Message received my-subscription1 : " + new String(msg.getData()));
                //模拟消费失败的情况
                int i = 1 / 0;
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };
        Consumer consumer = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription1")
                .messageListener(myMessageListener)
                .subscribe();
        System.in.read();
    }


    /**
     * Exclusive下的失败重试
     */
    @Test
    public void testConsumerExclusive() throws IOException {
        MessageListener myMessageListener1 = (consumer, msg) -> {
            try {
                System.out.println("Message received my-subscription1 : " + new String(msg.getData()));
                //模拟消费失败的情况
                int i = 1 / 0;
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };
        Consumer consumer = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription1")
                .subscriptionType(SubscriptionType.Exclusive)
                .messageListener(myMessageListener1)
                .subscribe();

        MessageListener myMessageListener2 = (consumer2, msg) -> {
            try {
                System.out.println("Message received my-subscription2 : " + new String(msg.getData()));
                consumer2.acknowledge(msg);
            } catch (Exception e) {
                consumer2.negativeAcknowledge(msg);
            }
        };
        Consumer consumer2 = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription2")
                .subscriptionType(SubscriptionType.Exclusive)
                .messageListener(myMessageListener2)
                .subscribe();

        System.in.read();
    }

    /**
     * Failover 下的失败重试
     */
    @Test
    public void testConsumerFailover() throws IOException {
        MessageListener myMessageListener1 = (consumer, msg) -> {
            try {
                System.out.println("Message received my-subscription1 : " + new String(msg.getData()));
                //模拟消费失败的情况
                int i = 1 / 0;
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };
        Consumer consumer = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription1")
                .subscriptionType(SubscriptionType.Failover)
                .messageListener(myMessageListener1)
                .subscribe();

        MessageListener myMessageListener2 = (consumer2, msg) -> {
            try {
                System.out.println("Message received my-subscription2 : " + new String(msg.getData()));
                consumer2.acknowledge(msg);
            } catch (Exception e) {
                consumer2.negativeAcknowledge(msg);
            }
        };
        Consumer consumer2 = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription1")
                .subscriptionType(SubscriptionType.Failover)
                .messageListener(myMessageListener2)
                .subscribe();

        System.in.read();
    }


    /**
     * Shard 下的失败重试
     */
    @Test
    public void testConsumerShard() throws IOException {
        MessageListener myMessageListener1 = (consumer, msg) -> {
            try {
                System.out.println("Message received my-subscription1 : " + new String(msg.getData()));
                //模拟消费失败的情况
                int i = 1 / 0;
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };
        Consumer consumer = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription1")
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(myMessageListener1)
                .subscribe();

        MessageListener myMessageListener2 = (consumer2, msg) -> {
            try {
                System.out.println("Message received my-subscription2 : " + new String(msg.getData()));
                consumer2.acknowledge(msg);
            } catch (Exception e) {
                consumer2.negativeAcknowledge(msg);
            }
        };
        Consumer consumer2 = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription1")
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(myMessageListener2)
                .subscribe();

        System.in.read();
    }

    @Test
    public void testProduce2() throws PulsarClientException {
        Producer<String> producer = client.newProducer(Schema.STRING)
                .topic("my-topic")
                .enableBatching(false)
                .create();
        producer.newMessage().key("key-1").value("message-1-1").send();
        producer.newMessage().key("key-1").value("message-1-2").send();
        producer.newMessage().key("key-1").value("message-1-3").send();
        producer.newMessage().key("key-2").value("message-2-1").send();
        producer.newMessage().key("key-2").value("message-2-2").send();
        producer.newMessage().key("key-2").value("message-2-3").send();
        producer.newMessage().key("key-3").value("message-3-1").send();
        producer.newMessage().key("key-3").value("message-3-2").send();
        producer.newMessage().key("key-4").value("message-4-1").send();
        producer.newMessage().key("key-4").value("message-4-2").send();
    }

    /**
     * Key_Shard 下的失败重试
     */
    @Test
    public void testConsumerKeyShard() throws IOException {
        MessageListener myMessageListener1 = (consumer, msg) -> {
            try {
                System.out.println("Message received my-subscription1 : " + new String(msg.getData()));
                //模拟消费失败的情况
                 int i=1/0;
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };
        Consumer consumer = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription1")
                .subscriptionType(SubscriptionType.Key_Shared)
                .messageListener(myMessageListener1)
                .subscribe();

        MessageListener myMessageListener2 = (consumer2, msg) -> {
            try {
                System.out.println("Message received my-subscription2 : " + new String(msg.getData()));
                consumer2.acknowledge(msg);
            } catch (Exception e) {
                consumer2.negativeAcknowledge(msg);
            }
        };
        Consumer consumer2 = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription1")
                .subscriptionType(SubscriptionType.Key_Shared)
                .messageListener(myMessageListener2)
                .subscribe();

        System.in.read();
    }

}
