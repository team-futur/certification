package kr.or.futur.futurcertification.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.or.futur.futurcertification.domain.dto.UserDTO;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity implements UserDetails{

    /**
     * 사용자 IDX
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_idx", nullable = false)
    private long idx;

    /**
     * 사용자 아이디
     */
    @Column(name = "user_id", nullable = false)
    private String userId;

    /**
     * 사용자 비밀번호
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "user_pw", nullable = false)
    private String password;

    /**
     * 사용자 이름
     */
    @Column(name = "user_name", nullable = false, length = 10)
    private String name;

    /**
     * 사용자 이메일
     */
    @Column(name = "user_email", length = 100)
    private String email;

    /**
     * 사용자 휴대전화 번호
     */
    @Column(name = "user_phone_number", length = 100)
    private String phoneNumber;

    /**
     * 사용자 주소
     */
    @Column(name = "user_address")
    private String address;

    /**
     * 사용자 권한
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    @Column(name = "user_roles")
    private List<String> roles = new ArrayList<>();

    /**
     * 사용자 생년월일
     */
    @Column(name = "user_birth_day", nullable = false)
    private LocalDate birthDay;

    /**
     * 삭제 여부
     */
    @Setter
    @Builder.Default
    @Column(name = "user_del_yn", nullable = false)
    private boolean delYn = false;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public String getUsername() {
        return this.userId;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isEnabled() {
        return true;
    }

    public UserDTO toDTO() {
        return UserDTO.builder()
                .idx(this.idx)
                .userId(this.userId)
                .name(this.name)
                .email(this.email)
                .phoneNumber(this.phoneNumber)
                .address(this.address)
                .roles(this.roles)
                .build();
    }
}
