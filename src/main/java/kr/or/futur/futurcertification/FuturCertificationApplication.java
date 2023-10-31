package kr.or.futur.futurcertification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FuturCertificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(FuturCertificationApplication.class, args);
    }

}
