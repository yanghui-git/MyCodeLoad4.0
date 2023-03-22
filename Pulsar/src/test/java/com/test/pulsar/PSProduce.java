package com.test.pulsar;

import lombok.SneakyThrows;
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
 * 测试生产者 Topic 访问模式
 * <p>
 * https://juejin.cn/post/7039898138609451022#heading-12
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PulsarApplication.class)
@Log
public class PSProduce {

    @Autowired
    private PulsarClient client;
/*

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
                .topic("访问模式-shared")
                .subscriptionName("my-subscription")
                .messageListener(myMessageListener)
                .subscribe();
        //
        System.in.read();
    }


    */

    /**
     * shard模式 默认情况下多个生产者可以发布消息到同一个 Topic
     *//*

    @Test
    public void testProducer() throws IOException {
        Producer<String> stringProducer = client
                .newProducer(Schema.STRING)
                .topic("访问模式-shared")
                .producerName("produce-demo1")
                .create();
        stringProducer.send("My message 1 " + "发送消息时间" + new Date());

        Producer<String> stringProducer2 = client
                .newProducer(Schema.STRING)
                .topic("访问模式-shared")
                // Producer with name 'produce-demo1' is already connected to topic
                //注意生产者名称不能重复
                .producerName("produce-demo2")
                .create();
        stringProducer2.send("My message 2 " + "发送消息时间" + new Date());

        System.in.read();
    }
*/
   /* @Test
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
                .topic("访问模式-Exclusive")
                .subscriptionName("my-subscription")
                .messageListener(myMessageListener)
                .subscribe();
        //
        System.in.read();
    }


    */

    /**
     * Exclusive 要求生产者以独占模式访问 Topic，在此模式下 如果 Topic 已经有了生产者，那么其他生产者在连接就会失败报错。
     * <p>
     * "Topic has an existing exclusive producer: standalone-0-12
     *//*
    @Test
    public void testProducer() throws IOException {
        Producer<String> stringProducer = client
                .newProducer(Schema.STRING)
                .topic("访问模式-Exclusive")
                //设置访问模式 默认shared
                .accessMode(ProducerAccessMode.Exclusive)
                .producerName("produce-demo1")
                .create();
        stringProducer.send("My message 1 " + "发送消息时间" + new Date());

        Producer<String> stringProducer2 = client
                .newProducer(Schema.STRING)
                .topic("访问模式-Exclusive")
                //设置访问模式 默认shared
                .accessMode(ProducerAccessMode.Exclusive)
                // Producer with name 'produce-demo1' is already connected to topic
                //注意生产者名称不能重复
                .producerName("produce-demo2")
                .create();
        stringProducer2.send("My message 2 " + "发送消息时间" + new Date());

        System.in.read();
    }
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
                .topic("访问模式-WaitForExclusive")
                .subscriptionName("my-subscription")
                .messageListener(myMessageListener)
                .subscribe();
        //
        System.in.read();
    }


    /**
     * WaitForExclusive
     * <p>
     * 如果主题已经连接了生产者，则将当前生产者挂起，直到生产者获得了 Exclusive 访问权限。
     * <p>
     * 也就是存在相同的生产者,不会报错,当然也不会发送消息,     获取到独占后,会将未获取到独占时的消息进行发送！！！
     */
    @Test
    public void testProducer() throws Exception {
        Producer<String> stringProducer = client
                .newProducer(Schema.STRING)
                .topic("访问模式-WaitForExclusive")
                //设置访问模式 默认shared
                .accessMode(ProducerAccessMode.WaitForExclusive)
                .producerName("produce-demo1")
                .create();


        new Thread() {
            @SneakyThrows
            @Override
            public void run() {
                stringProducer.send("My message 1 " + "发送消息时间" + new Date());
                //5秒过后释放,看看另一个生产者会不会发送消息成功  会
                Thread.sleep(5000);
                stringProducer.close();
                System.out.println("生产者1 已被释放");
            }
        }.start();

        Producer<String> stringProducer2 = client
                .newProducer(Schema.STRING)
                .topic("访问模式-WaitForExclusive")
                //设置访问模式 默认shared
                .accessMode(ProducerAccessMode.WaitForExclusive)
                // Producer with name 'produce-demo1' is already connected to topic
                //注意生产者名称不能重复
                .producerName("produce-demo2")
                .create();

        //模拟生产者1 被释放后,重新拿到独占,发送消息
        //Thread.sleep(10000);

//        stringProducer2.send("My message 2 " + "发送消息时间" + new Date());

        //假设有10条消息在未获取 独占前,均未被发送,模拟来看一下,获取独占后, 这10条消息会进行发送吗 ？ 会
        for (int i = 0; i < 10; i++) {
            stringProducer2.send("My message 2 " + "发送消息时间" + new Date());
        }
        System.in.read();
    }


}
