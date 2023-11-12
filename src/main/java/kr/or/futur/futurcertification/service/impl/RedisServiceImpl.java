package kr.or.futur.futurcertification.service.impl;

import kr.or.futur.futurcertification.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final Logger log = LoggerFactory.getLogger(RedisService.class);

    @Override
    public boolean isConnected() {
        try {
            String pingResponse = redisTemplate.getConnectionFactory().getConnection().ping();

            return "PONG".equals(pingResponse);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void setDataExpire(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    @Override
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public String getData(String key) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    @Override
    public boolean existData(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
