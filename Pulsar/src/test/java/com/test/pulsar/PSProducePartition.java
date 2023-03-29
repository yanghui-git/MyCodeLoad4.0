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
 * 测试生产者 Publish to partitioned topics 发不到不同broker
 *
 * <p>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PulsarApplication.class)
@Log
public class PSProducePartition {

    @Autowired
    private PulsarClient client;


    /**
     * 如果没有提供密钥，生产者将随机挑选一个分区，并将所有消息发布到该分区中。如果在消息上指定了一个键，则分区生成器对该键进行散列，并将消息分配给特定的分区。
     * SinglePartition
     * By default, Pulsar topics are served by a single broker, which limits the maximum throughput of a topic. Partitioned topics can span multiple brokers and thus allow for higher throughput
     */
    @Test
    public void testProduceSinglePartition() throws Exception {
        String topic = "persistent://my-tenant/my-namespace/my-topic";
        Producer<byte[]> producer = client.newProducer()
                .topic(topic)
                .messageRoutingMode(MessageRoutingMode.SinglePartition)
                .create();
        producer.send("Partitioned topic message".getBytes());
        System.in.read();
    }

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
                .topic("mytopic")
                .subscriptionName("my-subscription")
                .messageListener(myMessageListener)
                .subscribe();
        //
        System.in.read();
    }


}
