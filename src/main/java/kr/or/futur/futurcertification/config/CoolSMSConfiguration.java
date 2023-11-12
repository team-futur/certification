package kr.or.futur.futurcertification.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.stereotype.Component;

/**
 * CoolSMS 라이브러리를 사용할 수 있는 설정 파일 및 Component
 */
@Data
@Component
@ConstructorBinding
@ConfigurationProperties("springboot.coolsms")
public class CoolSMSConfiguration {

    private String apiKey;

    private String secretKey;

    private String callingNumber;
}
