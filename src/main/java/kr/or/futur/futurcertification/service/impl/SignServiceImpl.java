package kr.or.futur.futurcertification.service.impl;

import kr.or.futur.futurcertification.config.provider.JwtTokenProvider;
import kr.or.futur.futurcertification.domain.common.CertificationCodeType;
import kr.or.futur.futurcertification.domain.dto.UserDTO;
import kr.or.futur.futurcertification.domain.dto.request.ConfirmCertificationRequestDTO;
import kr.or.futur.futurcertification.domain.dto.request.FindLostUserIdRequestDTO;
import kr.or.futur.futurcertification.domain.dto.request.SendCertificationRequestDTO;
import kr.or.futur.futurcertification.domain.dto.request.SignUpRequestDTO;
import kr.or.futur.futurcertification.domain.dto.response.CommonResponseDTO;
import kr.or.futur.futurcertification.domain.entity.RefreshToken;
import kr.or.futur.futurcertification.domain.entity.User;
import kr.or.futur.futurcertification.exception.*;
import kr.or.futur.futurcertification.repository.RefreshTokenRepository;
import kr.or.futur.futurcertification.repository.UserRepository;
import kr.or.futur.futurcertification.service.RedisService;
import kr.or.futur.futurcertification.service.SMSService;
import kr.or.futur.futurcertification.service.SignService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SignServiceImpl implements SignService {

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final RedisService redisService;

    private final SMSService smsService;

    private final RefreshTokenRepository refreshTokenRepository;

    private final Logger log = LoggerFactory.getLogger(SignServiceImpl.class);

    @Override
    public CommonResponseDTO signUp(SignUpRequestDTO signUpRequestDTO) {
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

        /* 레디스에 인증번호가 확인된 여부 있는지 확인 */
        if(!redisService.existData(CertificationCodeType.REGISTER.name() + "_result_" + signUpRequestDTO.getPhoneNumber())) {
            throw new CertificationCodeExpiredException();
        }

        /* 2. 레디스에 인증 번호 확인 됐는지 확인 */
        if (!redisService.getData(CertificationCodeType.REGISTER.name() + "_result_" + signUpRequestDTO.getPhoneNumber()).equals("true")) {
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

        /* 4. DB에 저장 */
        User savedUser = userRepository.save(user);
        CommonResponseDTO commonResponseDTO = null;

        /* 5. 저장이 맞게 되었는지 검증 */
        if (savedUser.getName().isEmpty()) {
            log.error("[SignServiceImpl/signUp] 회원가입 실패");

            commonResponseDTO = CommonResponseDTO.builder()
                    .isSuccess(false)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .msg("회원가입을 실패하였습니다.")
                    .build();
        } else {
            log.info("[SignServiceImpl/signUp] 회원가입 성공");

            commonResponseDTO = CommonResponseDTO.builder()
                    .isSuccess(true)
                    .code(HttpStatus.OK.value())
                    .msg("회원가입을 성공했습니다.")
                    .build();

        }

        return commonResponseDTO;
    }

    @Override
    public CommonResponseDTO signIn(String id, String password) throws RuntimeException {
        log.info("[SignServiceImpl/signIn] 로그인 시도");

        log.info("userId : {}", id);
        log.info("password : {}", password);

        String loginFailMsg = "입력한 정보가 일치하지 않습니다.";
        User user = userRepository.getByUserId(id);

        log.info("user : {}", user);
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

        /* 리프레시 토큰 생성 */
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .expiryDate(LocalDate.from(LocalDateTime.now().plusDays(30)))
                .userIdx(user.getIdx())
                .refreshToken(refreshToken)
                .build();

        /* 리프레시 토큰 저장 */
        refreshTokenRepository.save(refreshTokenEntity);

        return CommonResponseDTO.builder()
                .isSuccess(true)
                .code(HttpStatus.OK.value())
                .msg("로그인을 성공했습니다.")
                .data(Map.of("token", token, "refreshToken", refreshToken))
                .build();
    }

    @Override
    public void sendCertificationNumber(SendCertificationRequestDTO sendCertificationRequestDTO) {
        /* 0. redis에 저장할 key 값, 인증번호 생성*/
        String redisKey = sendCertificationRequestDTO.getType() + "_" + sendCertificationRequestDTO.getPhoneNumber();
        String redisCntKey = sendCertificationRequestDTO.getType() + "_cnt_" + sendCertificationRequestDTO.getPhoneNumber();
        String expirationRedisKey = sendCertificationRequestDTO.getType() + "_expiration_" + sendCertificationRequestDTO.getPhoneNumber();
        String certificationNumber = String.valueOf((int) (Math.random() * 90000) + 10000);

       try {
           /* 1. 이미 요청한 기록이 있을 경우 해당 데이터를 지움*/
           if (redisService.existData(redisKey)) {
               redisService.deleteData(redisKey);
           }

           /* TODO 요청 횟수 제한 */

           long nowMilliseconds = System.currentTimeMillis();
           long threeMinutesLater = nowMilliseconds + (3 * 60 * 1000);

           /* 2. redis에 저장 및 인증번호 발송 */
           redisService.setDataExpire(redisKey, certificationNumber, 180);
           redisService.setDataExpire(expirationRedisKey, threeMinutesLater, 180);
           smsService.sendCertificationSMS(sendCertificationRequestDTO.getPhoneNumber(), certificationNumber);
       } catch (Exception e) {
           throw new CertificationCodeSendingFailedException(e);
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
        String certificationNumber = (String) redisService.getData(redisKey);

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

        /* 1. 사용자가 존재 유무 체크 */
        if (byUserId == null) {
            throw new UserNotFoundException();
        }

        /* 2. Redis에 인증번호를 요청한 기록이 있는지 확인 */
        if (!redisService.existData(CertificationCodeType.DELETE.name() + "_" + byUserId.getPhoneNumber())) {
            throw new CertificationCodeNotRequestedException();
        }

        /* 3. 인증번호가 일치하지 않은 경우 */
        if(!redisService.getData(CertificationCodeType.DELETE.name() + "_result_" + byUserId.getPhoneNumber()).equals("true")) {
            throw new CertificationCodeMismatchException();
        }

        /* 4. 사용자 삭제 */
        byUserId.setDelYn(true);
        userRepository.save(byUserId);
    }

    @Override
    public void restoreDeletedUser(String userId) {
        /* 사용자 조회 */
        User user = userRepository.findByUserIdAndDelYn(userId, true)
                .orElseThrow(UserNotFoundException::new);

        /* 인증 번호 요청 확인 */
        if (!redisService.existData(CertificationCodeType.RESTORE.name() + "_" + user.getPhoneNumber())) {
            throw new CertificationCodeNotRequestedException();
        }

        /* 인증번호가 일치하지 않은 경우 */
        if(!redisService.getData(CertificationCodeType.RESTORE.name() + "_result_" + user.getPhoneNumber()).equals("true")) {
            throw new CertificationCodeMismatchException();
        }

        /* 사용자 복구 */
        user.setDelYn(false);

        /* DB에 저장 */
        userRepository.save(user);
    }

    @Override
    public UserDTO findPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(UserNotFoundException::new)
                .toDTO();
    }

    @Override
    public UserDTO findUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(UserNotFoundException::new)
                .toDTO();
    }

    @Override
    public List<UserDTO> findAllByDelYn(Pageable pageable, boolean delYn) {
        return userRepository.findAllByDelYn(pageable, delYn).getContent().stream()
                .map(User::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserIdAndDelYn(String userId, boolean delYn) {
        return userRepository.findByUserIdAndDelYn(userId, delYn)
                .orElseThrow(UserNotFoundException::new)
                .toDTO();
    }

    @Override
    public CommonResponseDTO isDuplicate(String userId) {
        boolean isPresent = userRepository.findByUserId(userId).isPresent();

        return CommonResponseDTO.builder()
                .isSuccess(!isPresent)
                .msg(!isPresent ? "중복된 아이디가 존재하지 않습니다." : "중복된 아이디가 존재합니다.")
                .code(!isPresent ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @Override
    public CommonResponseDTO findLostUserId(FindLostUserIdRequestDTO findLostUserIdRequestDTO) {
        Map<String, Object> userIdInfo = new HashMap<>();

        /* 인증 번호 요청 확인 */
        if (!redisService.existData(CertificationCodeType.FIND_ID.name() + "_" + findLostUserIdRequestDTO.getPhoneNumber())) {
            throw new CertificationCodeNotRequestedException();
        }

        /* 인증번호가 일치하지 않은 경우 */
        if(!redisService.getData(CertificationCodeType.FIND_ID.name() + "_result_" + findLostUserIdRequestDTO.getPhoneNumber()).equals("true")) {
            throw new CertificationCodeMismatchException();
        }

        /* 사용자 조회 */
        UserDTO userDTO = userRepository.findByUserId(findLostUserIdRequestDTO.getUserId())
                .orElseThrow(UserNotFoundException::new)
                .toDTO();

        /* ID 일부 가리기 */
        userIdInfo.put("userId", userDTO.getUserId().substring(0, 5) + "***");

        return CommonResponseDTO.builder()
                .code(HttpStatus.OK.value())
                .isSuccess(true)
                .data(userIdInfo)
                .build();
    }
}
