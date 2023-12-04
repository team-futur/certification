package kr.or.futur.futurcertification.service.impl;

import kr.or.futur.futurcertification.service.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("prod")
class RedisServiceImplTest {

    @Autowired
    RedisService redisService;

    Logger log = LoggerFactory.getLogger(RedisServiceImplTest.class);

    @Test
    @DisplayName("연결 확인 - isConnected")
    void isConnected() {
        boolean connected = redisService.isConnected();

        // 연결 확인
        assertTrue(connected, "Redis와 연결하지 못했습니다.");
    }

    @Test
    @DisplayName("만료기간 있는 데이터를 저장 - setDataExpire")
    void setDataExpire() throws InterruptedException {
        redisService.setDataExpire("TEST", "1234", 10);

        String redisData = redisService.getData("TEST");

        // 데이터 검증
        assertNotNull(redisData, "Redis에서 데이터를 검색하지 못했습니다.");
        assertEquals("1234", redisData, "검색된 데이터가 예상과 다릅니다.");

        // 만료 시간 후 데이터 검증
        Thread.sleep(10000); // 10초 대기
        redisData = redisService.getData("TEST");
        assertNull(redisData, "데이터가 만료 시간 후에도 여전히 존재합니다.");
    }

    @Test
    @DisplayName("특정 데이터를 삭제 - deleteData")
    void deleteData() {
        redisService.setDataExpire("TEST", "1234", 10);

        redisService.deleteData("TEST");

        // 데이터 검증
        String redisData = redisService.getData("TEST");
        assertNull(redisData, "Redis에서 데이터를 삭제하지 못했습니다.");
    }

    @Test
    @DisplayName("특정 데이터 조회 - getData")
    void getData() {
        redisService.setDataExpire("TEST", "1234", 10);
        String redisData = redisService.getData("TEST");

        assertNotNull(redisData, "Redis에서 데이터를 검색하지 못했습니다.");
    }

    @Test
    @DisplayName("특정 데이터가 존재하는지 여부 - existData")
    void existData() {
        redisService.setDataExpire("TEST", "1234", 10);
        boolean existData = redisService.existData("TEST");

        assertTrue(existData, "Redis에서 데이터를 검색하지 못했습니다.");
    }
}