package com.khanh.timekeeping.configs.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topics.user-attendance}")
    private String userAttendanceTopic;

    public String getUserAttendanceTopic() {
        return userAttendanceTopic;
    }
}

