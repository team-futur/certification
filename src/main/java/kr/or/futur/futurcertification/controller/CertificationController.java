package kr.or.futur.futurcertification.controller;

import kr.or.futur.futurcertification.domain.dto.UserDTO;
import kr.or.futur.futurcertification.domain.dto.request.ConfirmCertificationRequestDTO;
import kr.or.futur.futurcertification.domain.dto.request.SendCertificationRequestDTO;
import kr.or.futur.futurcertification.domain.dto.request.SignInRequestDTO;
import kr.or.futur.futurcertification.domain.dto.request.SignUpRequestDTO;
import kr.or.futur.futurcertification.domain.dto.response.ConfirmCertificationResponseDTO;
import kr.or.futur.futurcertification.domain.dto.response.SignInResultDTO;
import kr.or.futur.futurcertification.domain.dto.response.SignUpResultDTO;
import kr.or.futur.futurcertification.service.SignService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@RequestMapping("/certification")
public class CertificationController {
    private final Logger log = LoggerFactory.getLogger(CertificationController.class);

    private final SignService signService;

    /**
     * 연결됐는지 여부 판별하는 Handler
     *
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
     *
     * @return SignInResultDTO
     */
    @PostMapping("/sign-in")
    public SignInResultDTO signIn(@Valid @RequestBody SignInRequestDTO signInRequestDTO) {
        return signService.signIn(signInRequestDTO.getUserId(), signInRequestDTO.getPassword());
    }

    /**
     * 회원가입
     *
     * @param signUpRequestDTO {}
     * @return SignUpResultDTO
     */
    @PostMapping("/sign-up")
    public SignUpResultDTO signUp(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO) {
        return signService.signUp(signUpRequestDTO);
    }

    /**
     * 인증번호 요청
     *
     * @param sendCertificationRequestDTO {}
     * @return CertificationResponseDTO
     */
    @PostMapping("/request-certification-number")
    public void requestCertificationNumber(@Valid @RequestBody SendCertificationRequestDTO sendCertificationRequestDTO) {
        signService.sendCertificationNumber(sendCertificationRequestDTO);
    }

    /**
     * 인증번호 확인
     *
     * @param certificationRequestDTO {}
     * @return
     */
    @PutMapping("/confirm-certification-number")
    public ConfirmCertificationResponseDTO confirmCertificationNumber(@Valid @RequestBody ConfirmCertificationRequestDTO certificationRequestDTO) {
        boolean isEqual = signService.confirmCertificationNumber(certificationRequestDTO);

        return ConfirmCertificationResponseDTO.builder()
                .code(HttpStatus.OK.value())
                .isEqual(isEqual)
                .build();
    }

    /**
     * 로그아웃
     */
    @PutMapping("/logout")
    public void logout() {
        /* TODO 로그아웃 구현 */
    }

    /**
     * 사용자 삭제
     *
     * @param userId
     */
    @DeleteMapping("/{userId}")
    public void delete(@PathVariable String userId) {
        signService.deleteUser(userId);
    }

    @PutMapping("/restore/{userId}")
    public void restoreUser(@PathVariable String userId) {
        signService.restoreDeletedUser(userId);
    }

    /**
     * 사용자 이름으로 단건 조회
     *
     * @param userIdAndPhoneNumber 사용자 아이디 또는 휴대전화 번호
     * @return
     */
    @GetMapping("/{userIdAndPhoneNumber}")
    public UserDTO findUserIdAndPhoneNumber(@PathVariable String userIdAndPhoneNumber) {
        String regex = "^010-\\d{4}-\\d{4}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userIdAndPhoneNumber);

        UserDTO userDTO = null;

        /* 전화번호 형식인 경우 */
        if (matcher.matches()) {
            userDTO = signService.findPhoneNumber(userIdAndPhoneNumber);
        } else { /* 아이디 형식인 경우 */
            userDTO = signService.findUserId(userIdAndPhoneNumber);
        }

        return userDTO;
    }

    /**
     * 사용자 아이디 찾기
     *
     * @param userId String
     * @return
     */
    @PostMapping("/find-user-id/{userId}")
    public void findUserId(@PathVariable String userId) {
        /* TODO 개발 필요 */
    }

    /**
     * 비밀번호 찾기
     *
     * @param userId
     */
    @PostMapping("/reset-password/{userId}")
    public void resetPassword(@PathVariable String userId) {
        /*  TODO 개발 필요 */
    }

    @PostMapping("/refresh")
    public void refreshToken() {
        /* TODO 개발 필요 */
    }
}
