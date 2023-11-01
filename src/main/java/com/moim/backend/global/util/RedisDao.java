package com.moim.backend.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisDao implements InitializingBean {
    private final RedisTemplate<String, String> redisTemplate;
    private ValueOperations<String, String> valueOperations;

    @Override
    public void afterPropertiesSet() throws Exception {
        valueOperations = redisTemplate.opsForValue();
    }

    public void setValues(String key, String data) {
        valueOperations.set(key, data);
    }

    public void setValuesList(String key, String data) {
        redisTemplate.opsForList().rightPushAll(key, data);
    }

    public List<String> getValuesList(String key) {
        Long len = redisTemplate.opsForList().size(key);
        return len == 0 ? new ArrayList<>() : redisTemplate.opsForList().range(key, 0, len - 1);
    }

    public void setValues(String key, String data, Duration duration) {
        valueOperations.set(key, data, duration);
    }

    public String getValues(String key) {
        return valueOperations.get(key);
    }

    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    public void deleteSpringCache(String name, String key) {
        redisTemplate.delete(name + "::" + key);
    }
}
