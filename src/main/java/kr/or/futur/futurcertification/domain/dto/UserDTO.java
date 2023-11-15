package kr.or.futur.futurcertification.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDTO {

    private long idx;

    private String userId;

    private String name;

    private String email;

    private String phoneNumber;

    private String address;

    private List<String> roles;

}
