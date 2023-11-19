package kr.or.futur.futurcertification.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInResultDTO {
    private String token;
    private String refreshToken;
    private boolean isSuccess;
    private String msg;
    private int code;
}
