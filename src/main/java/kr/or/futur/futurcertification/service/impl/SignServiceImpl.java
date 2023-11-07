package kr.or.futur.futurcertification.service.impl;

import kr.or.futur.futurcertification.config.provider.JwtTokenProvider;
import kr.or.futur.futurcertification.domain.common.Status;
import kr.or.futur.futurcertification.domain.dto.response.SignInResultDTO;
import kr.or.futur.futurcertification.domain.dto.response.SignUpResultDTO;
import kr.or.futur.futurcertification.domain.entity.User;
import kr.or.futur.futurcertification.exception.DuplicateUserIdException;
import kr.or.futur.futurcertification.exception.LoginFailedException;
import kr.or.futur.futurcertification.repository.UserRepository;
import kr.or.futur.futurcertification.service.SignService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class SignServiceImpl implements SignService {

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final Logger log = LoggerFactory.getLogger(SignServiceImpl.class);

    @Override
    public SignUpResultDTO signUp(String id, String password, String name, String role) {
        log.info("[SignServiceImpl/signUp] 회원 가입 로직 시작");
        User user = null;

        if (userRepository.getByUserId(id) != null) {
            throw new DuplicateUserIdException("동일한 아이디의 사용자가 있습니다.");
        }

        /* Step 1. 권한별 엔티티 객체 생성 */
        if ("admin".equalsIgnoreCase(role)) {
            user = User.builder()
                    .userId(id)
                    .name(name)
                    .password(passwordEncoder.encode(password))
                    .roles(Collections.singletonList("ROLE_ADMIN"))
                    .build();
        } else {
            user = User.builder()
                    .userId(id)
                    .name(name)
                    .password(passwordEncoder.encode(password))
                    .roles(Collections.singletonList("ROLE_USER"))
                    .build();
        }

        /* Step 2. DB에 저장 */
        User savedUser = userRepository.save(user);
        SignUpResultDTO signUpResultDTO = null;

        /* Step 3. 저장이 맞게 되었는지 검증 */
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
}
