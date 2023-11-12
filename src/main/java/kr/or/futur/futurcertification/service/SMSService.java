package kr.or.futur.futurcertification.service;

import java.util.Map;

public interface SMSService {
    /**
     * 메세지를 보내는 메서드
     * @param phoneNumber 휴대폰 번호
     * @param content 문자 내용
     * @return Map<String, Object> {res: true / false, msg: String}
     */
    Map<String, Object> sendSMS(String phoneNumber, String content);
}
