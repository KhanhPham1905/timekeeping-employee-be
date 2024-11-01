package com.khanh.timekeeping.services.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khanh.timekeeping.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.khanh.timekeeping.utils.ConvertUtil.objectMapper;

@Service
@RequiredArgsConstructor
public class QueryRedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;

    private String getKeyFrom (String keyword) {
        String key = String.format("list_user:%s", keyword);
        return key;
    }

    public void clear() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    public void saveUserList(String keyword, List<User> userList, long timeout, TimeUnit unit) throws JsonProcessingException {
        String json = redisObjectMapper.writeValueAsString(userList);
        String key = this.getKeyFrom(keyword);
        redisTemplate.opsForValue().set(key, json, timeout, unit);
    }

    public List<User> getUserList(String keyword) throws JsonProcessingException {
        String key = this.getKeyFrom(keyword);
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json != null) {
            // Chuyển đổi chuỗi JSON thành danh sách User
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
        } else {
            return null; // Hoặc xử lý trường hợp không tìm thấy
        }
    }
}
