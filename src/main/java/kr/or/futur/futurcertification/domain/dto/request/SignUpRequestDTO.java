package kr.or.futur.futurcertification.domain.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SignUpRequestDTO {
    @NotEmpty(message = "아이디는 필수 값입니다.")
    private String id;
    @NotEmpty(message = "비밀번호는 필수 값입니다.")
    private String password;
    @NotEmpty(message = "이름은 필수 값입니다.")
    private String name;
    @NotEmpty(message = "권한은 필수 값입니다.")
    private String role;
}
