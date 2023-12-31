package kr.or.futur.futurcertification.domain.dto.response;

import lombok.*;

/**
 * 아이디 중복 체크 DTO
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsDuplicateUserIdResponseDTO {
    int code;

    String msg;

    boolean isSuccess;
}
