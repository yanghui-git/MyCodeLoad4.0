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
 * 测试消费者 不同消费模型
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PulsarApplication.class)
@Log
public class PSConsumerMultiTopic2 {


    @Autowired
    private PulsarClient client;
/*

    @Test
    public void testProduce3() throws PulsarClientException {
        Producer<String> producer = client.newProducer(Schema.STRING)
                .topic("persistent://tenant/namespace/topic-test1")
                .enableBatching(false)
                .create();
        producer.send("111");
    }
*/

    /**
     * 默认是 发送一个消息 订阅了对应topic都可以进行消费
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
                .subscriptionName("my-subscription1")
                .messageListener(myMessageListener)
                .subscribe();

        Consumer consumer2 = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription2")
                .messageListener(myMessageListener)
                .subscribe();

        Consumer consumer3 = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription3")
                .messageListener(myMessageListener)
                .subscribe();

        System.in.read();
    }

    @Test
    public void testProducer() throws IOException {
        Producer<String> stringProducer = client
                .newProducer(Schema.STRING)
                .topic("topic5")
                .producerName("produce-demo1")
                .create();
        stringProducer.send("My message 5 " + "发送消息时间" + new Date(

        ));

        System.in.read();
    }


    @Test
    public void testProduce2() throws PulsarClientException {
        Producer<String> producer = client.newProducer(Schema.STRING)
                .topic("my-topic")
                .enableBatching(false)
                .create();
// 3 messages with "key-1", 3 messages with "key-2", 2 messages with "key-3" and 2 messages with "key-4"
        // 这里的key可以类似于 投递到 不同broker的一个标识???
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
     * Exclusive 模式  也是默认的
     * 同一主题上只能有一个具有相同订阅名称的使用者 默认
     * 否则会启动报错
     */
    @Test
    public void testConsumerExclusive() throws IOException {
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
                .subscriptionName("my-subscription2")
                .subscriptionType(SubscriptionType.Exclusive)
                .messageListener(myMessageListener)
                .subscribe();

        Consumer consumer2 = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription2")
                .subscriptionType(SubscriptionType.Exclusive)
                .messageListener(myMessageListener)
                .subscribe();

        System.in.read();
    }

    /**
     * Failover故障转移 .subscriptionName("my-subscription") 可重复
     * 一个节点挂掉了 剩余消息转移到另一个节点继续消费
     * 注意这些消费模式 都是和subscriptionName("my-subscription") 订阅者名称相关
     */
    int a = 0;

    @Test
    public void testConsumerFailover() throws IOException {
        MessageListener myMessageListener1 = (consumer, msg) -> {
            try {
                a++;
                if (a > 4) {
                    System.out.println("模拟节点1故障");
                    //关闭节点1
                    consumer.close();
                    throw new RuntimeException("模拟某时刻节点1故障,转移至节点2消费");
                }
                System.out.println("Message1 received: " + new String(msg.getData()));
                consumer.acknowledge(msg);
            } catch (Exception e) {
                //consumer.negativeAcknowledge(msg);
            }
        };
        Consumer consumer = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription")
                .subscriptionType(SubscriptionType.Failover)
                .messageListener(myMessageListener1)
                .subscribe();

        MessageListener myMessageListener2 = (consumer2, msg) -> {
            try {
                System.out.println("Message2 received: " + new String(msg.getData()));
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };
        Consumer consumer2 = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription")
                .subscriptionType(SubscriptionType.Failover)
                .messageListener(myMessageListener2)
                .subscribe();

        System.in.read();
    }


    /**
     * Shared模式
     * 多个使用者将能够使用相同的订阅名称，并且消息将根据连接的使用者之间的循环旋转进行分派。 在这种模式下，消费顺序不能保证。
     * 也就是消费者 1 消费者2 总共消费10条
     * 注意都是从 .subscriptionName("my-subscription") 视角
     */
    @Test
    public void testShared() throws IOException {
        MessageListener myMessageListener1 = (consumer, msg) -> {
            try {
                System.out.println("Message1 received: " + new String(msg.getData()));
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };
        Consumer consumer = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription")
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(myMessageListener1)
                .subscribe();

        MessageListener myMessageListener2 = (consumer2, msg) -> {
            try {
                System.out.println("Message2 received: " + new String(msg.getData()));
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };
        Consumer consumer2 = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription")
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(myMessageListener2)
                .subscribe();

        System.in.read();
    }

    /**
     * Key_Shared模式
     * 多个使用者将能够使用相同的订阅名称，并且消息将根据连接的使用者之间的循环旋转进行分派。 在这种模式下，消费顺序不能保证。
     * 也就是消费者 1 消费者2 总共消费10条
     * 注意都是从 .subscriptionName("my-subscription") 视角
     * <p>
     * 具有相同密钥的消息仅按顺序传递给一个消费者。消息在不同消费者之间的可能分布（默认情况下，我们事先不知道哪些密钥将被分配给消费者，但一个密钥只会同时被分配给消费者
     * ("key-1", "message-1-1")
     * ("key-1", "message-1-2")
     * ("key-1", "message-1-3")
     * ("key-3", "message-3-1")
     * ("key-3", "message-3-2")
     * <p>
     * <p>
     * ("key-2", "message-2-1")
     * ("key-2", "message-2-2")
     * ("key-2", "message-2-3")
     * ("key-4", "message-4-1")
     * ("key-4", "message-4-2")
     */
    @Test
    public void testKeyShared() throws IOException {
        MessageListener myMessageListener1 = (consumer, msg) -> {
            try {
                System.out.println("Message1 received: " + new String(msg.getData()));
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };
        Consumer consumer = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription")
                .subscriptionType(SubscriptionType.Key_Shared)
                .messageListener(myMessageListener1)
                .subscribe();

        MessageListener myMessageListener2 = (consumer2, msg) -> {
            try {
                System.out.println("Message2 received: " + new String(msg.getData()));
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };
        Consumer consumer2 = client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription")
                .subscriptionType(SubscriptionType.Key_Shared)
                .messageListener(myMessageListener2)
                .subscribe();

        System.in.read();
    }
}
