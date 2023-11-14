package kr.or.futur.futurcertification.domain.dto.request;

import kr.or.futur.futurcertification.domain.common.CertificationCodeType;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class SendCertificationRequestDTO {

    @NotBlank(message = "휴대전화 번호는 필수 값입니다.")
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3,4})-\\d{4}$", message = "휴대전화 번호의 형식이 올바르지 않습니다.")
    private String phoneNumber;

    @NotNull(message = "타입은 필수 값입니다.")
    @Enumerated(EnumType.STRING)
    private CertificationCodeType type;

}
