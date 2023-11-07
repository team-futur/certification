package kr.or.futur.futurcertification.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SignUpResultDTO {
    private boolean isSuccess;
    private int code;
    private String msg;
}
