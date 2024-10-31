package com.khanh.timekeeping.configs.kafka;


import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    public static final String SIMPLE_CONTAINER_FACTORY = "simpleContainerFactory";
    public static final String CONTAINER_FACTORY_CONFIG_BATCH = "containerFactoryConfigBatch";

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${spring.kafka.group-id}")
    private String groupId;

    private Map<String, Object> consumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "45000");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "18000");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "30");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }

    @Bean(name = SIMPLE_CONTAINER_FACTORY)
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerSimpleContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        // Other configurations
        factory.setCommonErrorHandler(errorHandler());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);

        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactoryConfigBatch() {
        Map<String, Object> config = consumerConfig();
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "180000");
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "300");
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean(name = CONTAINER_FACTORY_CONFIG_BATCH)
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactoryConfigBatch() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryConfigBatch());
        factory.setConcurrency(3);
        factory.setBatchListener(Boolean.TRUE);

        // Other configurations
        factory.setCommonErrorHandler(errorHandler());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);

        return factory;
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        BackOff fixedBackOff = new FixedBackOff(3000L, 1L);
        DefaultErrorHandler errorHandler =
                new DefaultErrorHandler(
                        (consumerRecord, exception) -> {
                            // Logic to execute when all the retry attemps are exhausted
                            System.out.printf(
                                    "Cannot retry record at par %d, offset %d - Detail %s%n",
                                    consumerRecord.partition(), consumerRecord.offset(), exception.getMessage());
                        },
                        fixedBackOff);
        errorHandler.addRetryableExceptions(SocketTimeoutException.class);
        errorHandler.addNotRetryableExceptions(NullPointerException.class);
        return errorHandler;
    }
}

