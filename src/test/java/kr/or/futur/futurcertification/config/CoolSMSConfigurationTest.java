package kr.or.futur.futurcertification.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("dev")
@SpringBootTest
class CoolSMSConfigurationTest {

    private final Logger log = LoggerFactory.getLogger(CoolSMSConfiguration.class);

    @Autowired
    private CoolSMSConfiguration coolSMSConfiguration;

    @Test
    @DisplayName("CoolSMS 라이브러리 연동 확인")
    void isConnected() {
        log.info("=========================");
        log.info(coolSMSConfiguration.toString());
        log.info("=========================");
    }
}