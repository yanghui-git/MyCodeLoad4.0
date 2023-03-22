package com.test.pulsar.config;

import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class PulsarConfig {

    /**
     * pulsar 连接bean
     */
    @Bean
    public PulsarClient getPulsarClient() throws PulsarClientException {
        return PulsarClient.builder()
                .serviceUrl("pulsar://1.116.157.159:6650")
                .build();
    }

}
