package kr.or.futur.futurcertification.domain.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 로그인
 */
@Data
public class SignInRequestDTO {
    @NotBlank(message = "아이디는 필수 값입니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 값입니다.")
    private String password;
}
