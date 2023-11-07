package kr.or.futur.futurcertification.domain.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SignInRequestDTO {
    @NotEmpty(message = "아이디는 필수 값입니다.")
    private String id;

    @NotEmpty(message = "패스워드는 필수 값입니다.")
    private String password;
}
