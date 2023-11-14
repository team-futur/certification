package kr.or.futur.futurcertification.domain.dto.request;

import kr.or.futur.futurcertification.domain.common.CertificationCodeType;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 인증번호 확인 요청 DTO
 */
@Data
public class ConfirmCertificationRequestDTO {

    @NotBlank(message = "인증번호는 필수 값입니다.")
    private String certificationNumber;

    @NotNull(message = "타입은 필수 값입니다.")
    @Enumerated(EnumType.STRING)
    private CertificationCodeType type;

    @NotBlank(message = "휴대전화 번호는 필수 값입니다.")
    private String phoneNumber;
}
