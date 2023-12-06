package kr.or.futur.futurcertification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.futur.futurcertification.domain.dto.UserDTO;
import kr.or.futur.futurcertification.domain.dto.request.*;
import kr.or.futur.futurcertification.domain.dto.response.CommonResponseDTO;
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

    private final ObjectMapper objectMapper;

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
     * @return CommonResponseDTO
     */
    @PostMapping("/sign-in")
    public CommonResponseDTO signIn(@Valid @RequestBody SignInRequestDTO signInRequestDTO) {
        return signService.signIn(signInRequestDTO.getUserId(), signInRequestDTO.getPassword());
    }

    /**
     * 회원가입
     *
     * @param signUpRequestDTO {}
     * @return CommonResponseDTO
     */
    @PostMapping("/sign-up")
    public CommonResponseDTO signUp(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO) {
        return signService.signUp(signUpRequestDTO);
    }

    /**
     * 인증번호 요청
     *
     * @param sendCertificationRequestDTO {}
     * @return CertificationResponseDTO
     */
    @PostMapping("/request-certification-number")
    public CommonResponseDTO requestCertificationNumber(@Valid @RequestBody SendCertificationRequestDTO sendCertificationRequestDTO) {

        signService.sendCertificationNumber(sendCertificationRequestDTO);

        return CommonResponseDTO.builder()
                .code(HttpStatus.OK.value())
                .msg("인증번호를 전송하였습니다.")
                .isSuccess(true)
                .build();
    }

    /**
     * 인증번호 확인
     *
     * @param certificationRequestDTO {}
     * @return
     */
    @PutMapping("/confirm-certification-number")
    public CommonResponseDTO confirmCertificationNumber(@Valid @RequestBody ConfirmCertificationRequestDTO certificationRequestDTO) {

        boolean isEqual = signService.confirmCertificationNumber(certificationRequestDTO);

        return CommonResponseDTO
                .builder()
                .isSuccess(isEqual)
                .msg(isEqual ? "인증번호 확인에 성공" : "인증번호 확인에 실패")
                .code(isEqual ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value())
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
     * @param userId
     * @return CommonResponseDTO
     */
    @DeleteMapping("/{userId}")
    public CommonResponseDTO delete(@PathVariable String userId) {

        signService.deleteUser(userId);

        return CommonResponseDTO.builder()
                .msg(userId + " 사용자를 삭제하였습니다.")
                .code(HttpStatus.OK.value())
                .isSuccess(true)
                .build();
    }

    /**
     * 아이디 복구
     * @param userId 사용자 아이디
     * @return
     */
    @PutMapping("/restore/{userId}")
    public CommonResponseDTO restoreUser(@PathVariable String userId) {

        signService.restoreDeletedUser(userId);

        return CommonResponseDTO.builder()
                .isSuccess(true)
                .code(HttpStatus.OK.value())
                .msg(userId + " 사용자를 복구하였습니다.")
                .build();
    }

    /**
     * 사용자 이름으로 단건 조회
     * @param userIdAndPhoneNumber 사용자 아이디 또는 휴대전화 번호
     * @return CommonResponseDTO
     */
    @GetMapping("/{userIdAndPhoneNumber}")
    public CommonResponseDTO findUserIdAndPhoneNumber(@PathVariable String userIdAndPhoneNumber) {
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

        return CommonResponseDTO.builder()
                .code(HttpStatus.OK.value())
                .isSuccess(true)
                .data(objectMapper.convertValue(userDTO, Map.class))
                .build();
    }

    /**
     * 사용자 아이디 찾기
     *
     * @param findLostUserIdRequestDTO
     * @return
     */
    @GetMapping("/find-lost-user-id")
    public CommonResponseDTO findUserId(@Valid FindLostUserIdRequestDTO findLostUserIdRequestDTO) {
        return signService.findLostUserId(findLostUserIdRequestDTO);
    }

    /**
     * 비밀번호 변경
     * @param userId
     */
    @PostMapping("/reset-password/{userId}")
    public void resetPassword(@PathVariable String userId) {
        /*  TODO 개발 필요 */
    }

    /**
     * 리프레쉬 토큰 발급
     */
    @PostMapping("/refresh")
    public void refreshToken() {
        /* TODO 개발 필요 */
    }

    /**
     * 아이디 중복 체크
     * @param userId
     * @return
     */
    @PostMapping("/duplicate/{userId}")
    public CommonResponseDTO isDuplicateUserId(@PathVariable String userId) {
        return signService.isDuplicate(userId);
    }
}
