package com.test.pulsar;

import lombok.extern.java.Log;
import org.apache.pulsar.client.api.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * 测试消费者 命名空间规则
 * {persistent|non-persistent}://tenant/namespace/topic
 * 注意namespace需手动先创建好,否则会报错 olicies not found for sample/namespace_test3 namespace
 * <p>
 * <p>
 * 默认 persistent://public/default/topic
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PulsarApplication.class)
@Log
public class PSConsumerNameSpace {


    @Autowired
    private PulsarClient client;

    /**
     * 向租户public 命名空间 namespace_test1 topic topic-demo1 发送消息
     */
    @Test
    public void testProduce1() throws Exception {
        Producer<String> producer = client.newProducer(Schema.STRING)
                .topic("persistent://public/namespace_test1/topic-demo1")
                .enableBatching(false)
                .create();
        producer.send("111");
        System.in.read();
    }


    /**
     * 测试消费
     * 只会打印出 my-subscription2 因为指定了 namespace
     */
    @Test
    public void testConsumer1() throws IOException {
        MessageListener myMessageListener = (consumer, msg) -> {
            try {
                System.out.println("Message received my-subscription1 : " + new String(msg.getData()));
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };
        Consumer consumer = client.newConsumer()
                .topic("topic-demo1")
                .subscriptionName("my-subscription1")
                .messageListener(myMessageListener)
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
                .topic("persistent://public/namespace_test1/topic-demo1")
                .subscriptionName("my-subscription2")
                .messageListener(myMessageListener2)
                .subscribe();

        System.in.read();
    }


    /**
     * 向租户sample 命名空间 namespace_test2  topic topic-haha1 发送消息
     */
    @Test
    public void testProduce2() throws Exception {
        Producer<String> producer = client.newProducer(Schema.STRING)
                .topic("persistent://sample/namespace_test2/topic-haha1")
                .enableBatching(false)
                .create();
        producer.send("向租户sample 命名空间 namespace_test2  topic topic-haha1 发送消息");
        System.in.read();
    }

    /**
     * 测试消费
     * 打印 Message received my-subscription2
     */
    @Test
    public void testConsumer2() throws IOException {
        MessageListener myMessageListener = (consumer, msg) -> {
            try {
                System.out.println("Message received my-subscription1 : " + new String(msg.getData()));
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };
        Consumer consumer = client.newConsumer()
                .topic("topic-haha1")
                .subscriptionName("my-subscription1")
                .messageListener(myMessageListener)
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
                .topic("persistent://sample/namespace_test2/topic-haha1")
                .subscriptionName("my-subscription2")
                .messageListener(myMessageListener2)
                .subscribe();

        System.in.read();
    }


    /**
     * 报错
     * 向租户sample 命名空间 namespace_test4  topic topic-haha1 发送消息
     * 注意namespace需手动先创建好,否则会报错 olicies not found for sample/namespace_test4 namespace
     */
    @Test
    public void testProduce322() throws Exception {
        Producer<String> producer = client.newProducer(Schema.STRING)
                .topic("persistent://sample/namespace_test4/topic-haha1")
                .enableBatching(false)
                .create();
        producer.send("向租户sample 命名空间 namespace_test2  topic topic-haha1 发送消息");
        System.in.read();
    }


    /**
     * 默认是persistent://public/default/topic
     */
    @Test
    public void testProduce3() throws Exception {
        Producer<String> producer = client.newProducer(Schema.STRING)
                .topic("hh_xixi1")
                .enableBatching(false)
                .create();
        producer.send("hh-xixi1");
        System.in.read();
    }


    /**
     * 测试消费
     * 也就是默认是 persistent://public/default/topic
     * 只会打印 my-subscription1 my-subscription2  不可打印 my-subscription3 因为设置接受的是非久化接收 non-persistent
     */
    @Test
    public void testConsumer4() throws IOException {
        MessageListener myMessageListener = (consumer, msg) -> {
            try {
                System.out.println("Message received my-subscription1 : " + new String(msg.getData()));
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };
        Consumer consumer = client.newConsumer()
                .topic("hh_xixi1")
                .subscriptionName("my-subscription1")
                .messageListener(myMessageListener)
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
                //验证默认是 persistent://public/default/topic
                .topic("persistent://public/default/hh_xixi1")
                .subscriptionName("my-subscription2")
                .messageListener(myMessageListener2)
                .subscribe();


        MessageListener myMessageListener3 = (consumer3, msg) -> {
            try {
                System.out.println("Message received my-subscription3 : " + new String(msg.getData()));
                consumer3.acknowledge(msg);
            } catch (Exception e) {
                consumer3.negativeAcknowledge(msg);
            }
        };
        Consumer consumer3 = client.newConsumer()
                //非持久化
                .topic("non-persistent://public/default/hh_xixi1")
                .subscriptionName("my-subscription3")
                .messageListener(myMessageListener3)
                .subscribe();

        System.in.read();
    }


}
