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

    @NotBlank(message = "이름은 필수 값입니다.")
    private String name;

    private String email;

    private String phoneNumber;

    private String address;

    @NotBlank(message = "생년월일 필수 값입니다.(yyyy-MM-dd)")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "생년월일은 yyy-MM-dd 형식만 가능합니다.")
    private String birthDay;
}
