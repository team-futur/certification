package kr.or.futur.futurcertification.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.futur.futurcertification.config.CoolSMSConfiguration;
import kr.or.futur.futurcertification.exception.SMSSendingFailedException;
import kr.or.futur.futurcertification.service.SMSService;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SMSServiceImpl implements SMSService {

    private final CoolSMSConfiguration coolSMSConfiguration;
    private final Logger log = LoggerFactory.getLogger(SMSServiceImpl.class);

    private final Environment environment;

    @Override
    public Map<String, Object> sendSMS(String phoneNumber, String content) {
        Map<String, Object> sendResult = new HashMap<>();

        /* 테스트일 떄 문자 발송 안되도록 */
        String[] activeProfiles = environment.getActiveProfiles();

        if ("test".equals(activeProfiles[0])) {
            sendResult.put("success_count", 1);
            return sendResult;
        }

        Message message = new Message(coolSMSConfiguration.getApiKey(), coolSMSConfiguration.getSecretKey());
        HashMap<String, String> param = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject sendJSONResult;

        if (content.length() > 70) {
            log.error("SMS는 70자를 초과할 수 없습니다. : {}", content);
            throw new IllegalArgumentException("SMS은 70자를 초과할 수 없습니다.");
        }

        /* put data */
        param.put("to", phoneNumber);
        param.put("from", coolSMSConfiguration.getCallingNumber());
        param.put("type", "SMS");
        param.put("text", content);

        try {
            /* send message */
            sendJSONResult = message.send(param);

            /* convert map */
            sendResult = objectMapper.readValue(sendJSONResult.toJSONString(), Map.class);
        } catch (CoolsmsException e) {
            log.error("SMS를 발송하지 못했습니다. : {}", e.getMessage());
            throw new SMSSendingFailedException();
        } catch (JsonProcessingException e) {
            log.error("SMS 결과를 Map으로 파싱하지 못했습니다. : {}", e.getMessage());
            throw new SMSSendingFailedException();
        }

        return sendResult;
    }

    @Override
    public Map<String, Object> sendCertificationSMS(String phoneNumber, String content) {

        content = ("[작물 관리 전산 시스템 인증번호] : " + content);

        return sendSMS(phoneNumber, content);
    }
}
