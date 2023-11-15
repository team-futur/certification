package kr.or.futur.futurcertification.service.impl;

import kr.or.futur.futurcertification.config.provider.JwtTokenProvider;
import kr.or.futur.futurcertification.domain.common.Status;
import kr.or.futur.futurcertification.domain.dto.UserDTO;
import kr.or.futur.futurcertification.domain.dto.request.ConfirmCertificationRequestDTO;
import kr.or.futur.futurcertification.domain.dto.request.SendCertificationRequestDTO;
import kr.or.futur.futurcertification.domain.dto.request.SignUpRequestDTO;
import kr.or.futur.futurcertification.domain.dto.response.SignInResultDTO;
import kr.or.futur.futurcertification.domain.dto.response.SignUpResultDTO;
import kr.or.futur.futurcertification.domain.entity.User;
import kr.or.futur.futurcertification.exception.*;
import kr.or.futur.futurcertification.repository.UserRepository;
import kr.or.futur.futurcertification.service.RedisService;
import kr.or.futur.futurcertification.service.SMSService;
import kr.or.futur.futurcertification.service.SignService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SignServiceImpl implements SignService {

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final RedisService redisService;

    private final SMSService smsService;

    private final Logger log = LoggerFactory.getLogger(SignServiceImpl.class);

    @Override
    public SignUpResultDTO signUp(SignUpRequestDTO signUpRequestDTO) {
        String id = signUpRequestDTO.getId();
        String name = signUpRequestDTO.getName();
        String password = signUpRequestDTO.getPassword();
        String role = signUpRequestDTO.getRole();

        User user = null;

        /* 0. 이미 존재하는 아이디의 경우 제외 */
        if (userRepository.getByUserId(id) != null) {
            throw new DuplicateUserIdException("동일한 아이디의 사용자가 있습니다.");
        }

        /* 1. 이미 존재하는 휴대폰 번호 제외 */
        if(userRepository.findByPhoneNumber(signUpRequestDTO.getPhoneNumber()).isPresent()) {
            throw new DuplicatePhoneNumberException("동일한 휴대폰 번호가 있습니다.");
        }

        /* 2. 레디스에 인증 번호 확인 됐는지 확인 */
        if (!redisService.existData("REGISTER_result_" + signUpRequestDTO.getPhoneNumber())) {
            throw new CertificationCodeExpiredException();
        }

        /* 3. 권한별 엔티티 객체 생성 */
        List<String> roles = null;

        if ("admin".equalsIgnoreCase(role)) {
            roles = Collections.singletonList("ROLE_ADMIN");
        } else {
            roles = Collections.singletonList("ROLE_USER");
        }

        user = User.builder()
                .userId(id)
                .name(name)
                .password(passwordEncoder.encode(password))
                .roles(roles)
                .address(signUpRequestDTO.getAddress())
                .phoneNumber(signUpRequestDTO.getPhoneNumber())
                .email(signUpRequestDTO.getEmail())
                .birthDay(LocalDate.parse(signUpRequestDTO.getBirthDay()))
                .delYn(false)
                .build();

        log.info("3");
        /* 4. DB에 저장 */
        User savedUser = userRepository.save(user);
        SignUpResultDTO signUpResultDTO = null;

        /* 5. 저장이 맞게 되었는지 검증 */
        if (savedUser.getName().isEmpty()) {
            log.error("[SignServiceImpl/signUp] 회원가입 실패");

            signUpResultDTO = SignUpResultDTO.builder()
                    .isSuccess(Status.FAIL.value())
                    .code(HttpStatus.BAD_REQUEST.value())
                    .msg("회원가입을 성공했습니다.")
                    .build();
        } else {
            log.info("[SignServiceImpl/signUp] 회원가입 성공");

            signUpResultDTO = SignUpResultDTO.builder()
                    .isSuccess(Status.SUCCESS.value())
                    .code(HttpStatus.OK.value())
                    .msg("회원가입을 성공했습니다.")
                    .build();

        }

        return signUpResultDTO;
    }

    @Override
    public SignInResultDTO signIn(String id, String password) throws RuntimeException {
        log.info("[SignServiceImpl/signIn] 로그인 시도");

        String loginFailMsg = "입력한 정보가 일치하지 않습니다.";
        User user = userRepository.getByUserId(id);

        /* id와 맞는 User가 있는지 조회 */
        if (user == null) {
            throw new LoginFailedException(loginFailMsg);
        }

        /* Step 2. 비밀번호 일치여부 */
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new LoginFailedException(loginFailMsg);
        }

        /* Step 3. 토큰 생성 및 전달 */
        String token = jwtTokenProvider.createToken(user.getUserId(), user.getRoles());

        return SignInResultDTO.builder()
                .token(token)
                .build();
    }

    @Override
    public void sendCertificationNumber(SendCertificationRequestDTO sendCertificationRequestDTO) {
        /* 0. redis에 저장할 key 값, 인증번호 생성*/
        String redisKey = sendCertificationRequestDTO.getType() + "_" + sendCertificationRequestDTO.getPhoneNumber();
        String redisCntKey = sendCertificationRequestDTO.getType() + "_cnt_" + sendCertificationRequestDTO.getPhoneNumber();
        String certificationNumber = String.valueOf((int) (Math.random() * 90000) + 10000);

       try {
           /* 1. 이미 요청한 기록이 있을 경우 해당 데이터를 지움*/
           if (redisService.existData(redisKey)) {
               redisService.deleteData(redisKey);
           }

           int requestCnt = redisService.getData(redisCntKey) == null ? 0 : Integer.parseInt(redisService.getData(redisCntKey));

           if (requestCnt > 4) {
               throw new CertificationCodeSendingFailedException("일정 시간동안 인증번호를 요청하실 수 없습니다.");
           }

           /* 2. redis에 저장 및 인증번호 발송 */
           redisService.setDataExpire(redisKey, certificationNumber, 180);
           redisService.setDataExpire(redisCntKey, String.valueOf(requestCnt + 1), 600);
           smsService.sendCertificationSMS(sendCertificationRequestDTO.getPhoneNumber(), certificationNumber);
       } catch (Exception e) {
           throw new CertificationCodeSendingFailedException();
       }
    }

    @Override
    public boolean confirmCertificationNumber(ConfirmCertificationRequestDTO certificationRequestDTO) {
        String redisKey = certificationRequestDTO.getType() + "_" + certificationRequestDTO.getPhoneNumber();
        String redisCntKey = certificationRequestDTO.getType() + "_cnt_" + certificationRequestDTO.getPhoneNumber();

        /* 1. 레디스에 저장된 데이터가 있는지 확인 */
        if(!redisService.existData(redisKey)) {
            throw new CertificationCodeSendingFailedException();
        }

        /* 2. 레디스에서 인증번호 추출 */
        String certificationNumber = redisService.getData(redisKey);

        /* 3. 인증번호 불일치하는 경우 제외 */
        if(!certificationRequestDTO.getCertificationNumber().equals(certificationNumber)) {
            throw new CertificationCodeMismatchException();
        }

        /* 4. 성공 시 인증번호 시도 횟수 초기화 */
        redisService.setDataExpire(redisCntKey, "0", 600);

        /* 5. 성공 시 결과 redis에 저장 */
        redisService.setDataExpire(certificationRequestDTO.getType() + "_result_" + certificationRequestDTO.getPhoneNumber(), "true", 600);
        return true;
    }

    @Override
    public void deleteUser(String userId) {
        User byUserId = userRepository.getByUserId(userId);

        /* 0. 사용자 조회 못할 경우 */
        if (byUserId == null) {
            throw new UserNotFoundException();
        }

        /* TODO Redis로 검증 */
        /* 1. 사용자 삭제 */
        byUserId.setDelYn(true);
        userRepository.save(byUserId);
    }

    @Override
    public UserDTO findUserId(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(UserNotFoundException::new);

        return user.toDTO();
    }
}
