package kr.or.futur.futurcertification.domain.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignUpRequestDTO {

    @NotBlank(message = "아이디는 필수 값입니다.")
    private String id;

    @NotBlank(message = "비밀번호는 필수 값입니다.")
    private String password;

    @NotBlank(message = "이름은 필수 값입니다.")
    private String name;

    @NotBlank(message = "권한은 필수 값입니다.")
    private String role;

    private String email;

    @NotBlank(message = "휴대전화 번호는 필수 값입니다.")
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3,4})-\\d{4}$", message = "휴대전화 번호의 형식이 올바르지 않습니다.")
    private String phoneNumber;

    private String address;

    @NotBlank(message = "생년월일 필수 값입니다.(yyyy-MM-dd)")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "생년월일은 yyy-MM-dd 형식만 가능합니다.")
    private String birthDay;

}
