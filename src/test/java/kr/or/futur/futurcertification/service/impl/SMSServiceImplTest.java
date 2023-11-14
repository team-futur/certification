package kr.or.futur.futurcertification.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ActiveProfiles("prod")
@SpringBootTest
class SMSServiceImplTest {

    @Autowired
    private SMSServiceImpl smsService;

    private final Logger log = LoggerFactory.getLogger(SMSServiceImpl.class);

    @Test
    @DisplayName("SMS 메세지 발송")
    void sendSMS() {
        Map<String, Object> sendResult = smsService.sendSMS("01065263863", "SMS 발송 테스트");

        assertNotEquals(sendResult.get("success_count"), 0, "SMS를 발송할 수 없습니다.");
    }

    @Test
    @DisplayName("인증번호 메세지 발송")
    void sendCertificationSMS() {
        Map<String, Object> sendResult = smsService.sendCertificationSMS("01065263863", "인증번호 발송 테스트");

        assertNotEquals(sendResult.get("success_count"), 0, "SMS를 발송할 수 없습니다.");
    }
}