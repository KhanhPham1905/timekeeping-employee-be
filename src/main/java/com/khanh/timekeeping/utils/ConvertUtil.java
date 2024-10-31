package com.khanh.timekeeping.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConvertUtil {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static String toString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            log.warn("Error when convert object to string", ex);
            return "";
        }
    }

    public static <T> T toObject(String json, Class<T> classz) {
        try {
            return objectMapper.readValue(json, classz);
        } catch (JsonProcessingException ex) {
            log.warn("Error when convert string to object", ex);
            return null;
        }
    }
}

