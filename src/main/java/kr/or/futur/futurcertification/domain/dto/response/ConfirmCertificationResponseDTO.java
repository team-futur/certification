package kr.or.futur.futurcertification.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmCertificationResponseDTO {

    private int code;

    private String msg;

    private boolean isEqual;
}
