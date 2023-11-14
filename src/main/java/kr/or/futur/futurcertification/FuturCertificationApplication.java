package kr.or.futur.futurcertification;

import kr.or.futur.futurcertification.config.CoolSMSConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableDiscoveryClient
@SpringBootApplication
@EnableConfigurationProperties(CoolSMSConfiguration.class)
public class FuturCertificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(FuturCertificationApplication.class, args);
    }

}
