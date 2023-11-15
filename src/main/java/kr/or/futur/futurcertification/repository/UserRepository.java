package kr.or.futur.futurcertification.repository;

import io.lettuce.core.dynamic.annotation.Param;
import kr.or.futur.futurcertification.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Spring Security에서 사용하는 함수
     * @param userId
     * @return
     */
    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.userId = :userId " +
            "AND u.delYn = false ")
    User getByUserId(@Param("userId") String userId);

    /**
     * 휴대전화 번호 일치한 사용자 찾는
     * @param phoneNumber
     * @return
     */
    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.phoneNumber = :phoneNumber " +
            "AND u.delYn = false ")
    Optional<User> findByPhoneNumber(String phoneNumber);
}
