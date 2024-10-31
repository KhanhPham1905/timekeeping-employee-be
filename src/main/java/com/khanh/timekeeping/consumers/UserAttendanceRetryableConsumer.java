package com.khanh.timekeeping.consumers;


import com.khanh.timekeeping.configs.kafka.KafkaConsumerConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
@KafkaListener(
        containerFactory = KafkaConsumerConfig.CONTAINER_FACTORY_CONFIG_BATCH,
        topics = "${spring.kafka.topics.user-attendance}",
        groupId = "erp-demo-retry")
public class UserAttendanceRetryableConsumer {

    @KafkaHandler
    @RetryableTopic(
            backoff = @Backoff(value = 3000L),
            attempts = "1",
            autoCreateTopics = "false",
            include = SocketTimeoutException.class,
            exclude = NullPointerException.class)
    public void nonBlockingRetry(List<String> messages) throws SocketTimeoutException {
        throw new SocketTimeoutException();
    }
}
