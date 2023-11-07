package kr.or.futur.futurcertification.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ErrorResponseDTO {
    private int status = HttpStatus.BAD_REQUEST.value();
    private String msg;
}
