package kr.or.futur.futurcertification.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 시간을 다루는 클래스
 */
public class TimeUtil {

    /**
     * 현재 시간을 문자열로 보여주는 메서드
     * @return
     */
    public static String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
