package kr.or.futur.futurcertification.controller;

import kr.or.futur.futurcertification.domain.dto.request.SignInRequestDTO;
import kr.or.futur.futurcertification.domain.dto.request.SignUpRequestDTO;
import kr.or.futur.futurcertification.domain.dto.response.SignInResultDTO;
import kr.or.futur.futurcertification.domain.dto.response.SignUpResultDTO;
import kr.or.futur.futurcertification.service.SignService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/certification")
public class CertificationController {
    private final Logger log = LoggerFactory.getLogger(CertificationController.class);

    private final SignService signService;

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

    /**
     * 로그인
     * @return SignInResultDTO
     */
    @PostMapping("/sign-in")
    public SignInResultDTO signIn(@Valid SignInRequestDTO signInRequestDTO) {
        return signService.signIn(signInRequestDTO.getId(), signInRequestDTO.getPassword());
    }

    /**
     * 회원가입
     * @return SignUpResultDTO
     */
    @PostMapping("/sign-up")
    public SignUpResultDTO signUp(@Valid SignUpRequestDTO signUpRequestDTO) {
        return signService.signUp(signUpRequestDTO.getId(), signUpRequestDTO.getPassword(), signUpRequestDTO.getName(), signUpRequestDTO.getRole());
    }
}
