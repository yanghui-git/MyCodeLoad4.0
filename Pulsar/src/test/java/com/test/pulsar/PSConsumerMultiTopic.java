package com.test.pulsar;

import lombok.extern.java.Log;
import org.apache.pulsar.client.api.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 测试消费者
 * <p>
 * https://juejin.cn/post/7039898138609451022#heading-12
 *
 * https://pulsar.apache.org/docs/2.11.x/client-libraries-java/#multi-topic-subscriptions
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PulsarApplication.class)
@Log
public class PSConsumerMultiTopic {

    /**
     * 在 Pulsar 中不需要显示的创建 Topic，如果尝试向不存在的 Topic 发送或接收消息，会在默认租户和命名空间中创建 Topic。
     * topic 命名
     * <p>
     * {persistent|non-persistent}://tenant/namespace/topic
     * 持久化/非持久化/  租户 / 命名空间  /topic
     * <p>
     * 默认消息为persistent 持久化类型,  进行订阅消费 默认也是 .subscriptionTopicsMode(RegexSubscriptionMode.PersistentOnly) 持久化类型
     */
    //String topic = "persistent://" + "tenant" + "/" + "nameSpace" + "/" + "";

    @Autowired
    private PulsarClient client;

    /**
     * Multi-topic subscriptions
     * 多主题订阅
     * 正则表达式,订阅所有
     */
    @Test
    public void testConsumer1() throws IOException {
        MessageListener myMessageListener = (consumer, msg) -> {
            try {
                System.out.println("Message received: " + new String(msg.getData()));
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };
        ConsumerBuilder consumerBuilder = client.newConsumer()
                .subscriptionName("consumer-1");
        // Subscribe to all topics in a namespace
        Pattern allTopicsInNamespace = Pattern.compile("public/default/.*");
        Consumer allTopicsConsumer = consumerBuilder
                .topicsPattern(allTopicsInNamespace)
                .messageListener(myMessageListener)
                .subscribe();

        System.in.read();
    }


    /**
     * Multi-topic subscriptions
     * 多主题订阅
     * 正则表达式,订阅 以 1结束的topic
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
        ConsumerBuilder consumerBuilder = client.newConsumer()
                .subscriptionName("consumer-1");
        // Subscribe to all topics in a namespace
        Pattern allTopicsInNamespace = Pattern.compile("public/default/.*1");
        Consumer allTopicsConsumer = consumerBuilder
                .topicsPattern(allTopicsInNamespace)
                .messageListener(myMessageListener)


                //订阅消息的
                //订阅消息为非持久化,默认订阅持久化的
                //.subscriptionTopicsMode(RegexSubscriptionMode.NonPersistentOnly)

                //当然消息默认是持久化
                .subscriptionTopicsMode(RegexSubscriptionMode.PersistentOnly)
                .subscribe();

        System.in.read();
    }


    /**
     * Multi-topic subscriptions
     * 多主题订阅
     * 多topic 订阅list设置的topic1 topic2
     */
    @Test
    public void testConsumer3() throws IOException {
        MessageListener myMessageListener = (consumer, msg) -> {
            try {
                System.out.println("Message received: " + new String(msg.getData()));
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };

        ConsumerBuilder consumerBuilder = client.newConsumer()
                .subscriptionName("consumer-3");

        List<String> topics = Arrays.asList(
                "topic1",
                "topic2"
        );
        Consumer multiTopicConsumer = consumerBuilder
                .topics(topics)
                .messageListener(myMessageListener)
                .subscribe();
        //
        System.in.read();
    }

    /**
     * Multi-topic subscriptions
     * 多主题订阅
     * 多topic 订阅list设置的topic1 topic2
     */
    @Test
    public void testConsumer4() throws IOException {
        MessageListener myMessageListener = (consumer, msg) -> {
            try {
                System.out.println("Message received: " + new String(msg.getData()));
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };

        ConsumerBuilder consumerBuilder = client.newConsumer()
                .subscriptionName("consumer-3");

        // Alternatively:
        Consumer multiTopicConsumer = consumerBuilder
                .topic(
                        "topic1",
                        "topic2",
                        "topic3"
                )
                .messageListener(myMessageListener)
                .subscribe();
        //
        System.in.read();
    }


    /**
     * Multi-topic subscriptions
     * 多主题订阅
     * 多topic 订阅list设置的topic1 topic2
     * <p>
     * 异步订阅多主题
     *//*
    @Test
    public void testConsumer5() throws IOException {
        MessageListener myMessageListener = (consumer, msg) -> {
            try {
                System.out.println("Message received: " + new String(msg.getData()));
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };

        ConsumerBuilder consumerBuilder = client.newConsumer()
                .subscriptionName("consumer-3");

        consumerBuilder
                .topic(
                        "topic1",
                        "topic2",
                        "topic3"
                )
                .messageListener(myMessageListener)
                .subscribeAsync();
               // .thenAccept(this::receiveMessageFromConsumer);

        //
        System.in.read();
    }
*/
/*    private void receiveMessageFromConsumer(Object consumer) {
        ((Consumer) consumer).receiveAsync().thenAccept(message -> {
            // Do something with the received message
            receiveMessageFromConsumer(consumer);
        });
    }*/

    /**
     * 使用loadConf创建Produce
     */
    @Test
    public void testProducer() throws Exception {
        Map<String, Object> config1 = new HashMap<>();
        config1.put("producerName", "produce-demo1");
        config1.put("topicName", "topic1");

        Producer producer1 = client
                .newProducer()
                .loadConf(config1)
                .create();

        producer1.send(("test1 --- " + new Date()).getBytes());

        Map<String, Object> config2 = new HashMap<>();
        config2.put("producerName", "produce-demo2");
        config2.put("topicName", "topic2");

        Producer producer2 = client
                .newProducer()
                .loadConf(config2)
                .create();

        producer2.send(("test2 --- " + new Date()).getBytes());

        System.in.read();
    }


}
