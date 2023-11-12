package kr.or.futur.futurcertification.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.futur.futurcertification.config.CoolSMSConfiguration;
import kr.or.futur.futurcertification.service.SMSService;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class SMSServiceImpl implements SMSService {

    private final CoolSMSConfiguration coolSMSConfiguration;
    private final Logger log = LoggerFactory.getLogger(SMSServiceImpl.class);

    @Override
    public Map<String, Object> sendSMS(String phoneNumber, String content) {
        Message message = new Message(coolSMSConfiguration.getApiKey(), coolSMSConfiguration.getSecretKey());
        HashMap<String, String> param = new HashMap<>();
        Map<String, Object> sendResult = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject sendJSONResult;

        content = ("[작물 관리 전산 시스템 인증번호] : " + content);

        /* put data */
        param.put("to", coolSMSConfiguration.getCallingNumber());
        param.put("from", phoneNumber);
        param.put("type", "SMS");
        param.put("text", content);

        try {
            /* send message */
            sendJSONResult = message.send(param);

            /* convert map */
            sendResult = objectMapper.readValue(sendJSONResult.toJSONString(), Map.class);
        } catch (CoolsmsException e) {
            log.error("SMS를 발송하지 못했습니다. : {}", e.getMessage());
        } catch (JsonProcessingException e) {
            log.error("SMS 결과를 Map으로 파싱하지 못했습니다. : {}", e.getMessage());
        }

        return sendResult;
    }
}
