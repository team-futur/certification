package kr.or.futur.futurcertification.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class CoolSMSConfigurationTest {

    private final Logger log = LoggerFactory.getLogger(CoolSMSConfiguration.class);

    @Autowired
    private CoolSMSConfiguration coolSMSConfiguration;

    @Test
    @DisplayName("CoolSMS 라이브러리 세팅 값 확인")
    void isSetCoolSMSConfiguration() {
        assertNotNull(coolSMSConfiguration, "CoolSMS 라이브러리가 세팅되지 않았습니다.");
        assertNotNull(coolSMSConfiguration.getApiKey(), "CoolSMS API Key가 세팅되지 않았습니다.");
        assertNotNull(coolSMSConfiguration.getCallingNumber(), "CoolSMS Calling Number가 세팅되지 않았습니다.");
        assertNotNull(coolSMSConfiguration.getSecretKey(), "CoolSMS Secret Key가 세팅되지 않았습니다.");
    }
}