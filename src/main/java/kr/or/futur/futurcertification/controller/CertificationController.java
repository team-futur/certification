package kr.or.futur.futurcertification.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/certification")
public class CertificationController {
    private final Logger log = LoggerFactory.getLogger(CertificationController.class);

    /**
     * 연결됐는지 여부 판별하는 Handler
     * @return {isConnected: true}
     */
    @GetMapping("/connected")
    public ResponseEntity<Map<String, Object>> connected() {
        Map<String, Object> connectedMap = new HashMap<>();

        connectedMap.put("isConnected", true);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(connectedMap);
    }
}
