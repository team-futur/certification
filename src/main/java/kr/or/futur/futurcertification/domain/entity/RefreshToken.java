package kr.or.futur.futurcertification.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * 리프레쉬 토큰 발급
 */

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_token")
@EntityListeners(AuditingEntityListener.class)
public class RefreshToken extends BaseEntity {

    /**
     * 리프레시 토큰 IDX
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_idx", nullable = false)
    private long idx;

    /**
     * 리프레시 토큰
     */
    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    /**
     * 사용자 IDX
     */
    @Column(name = "user_idx", nullable = false)
    private long userIdx;

    /**
     * 만료일자
     */
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

}
