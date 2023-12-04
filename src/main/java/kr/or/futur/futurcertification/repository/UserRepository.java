package kr.or.futur.futurcertification.repository;

import io.lettuce.core.dynamic.annotation.Param;
import kr.or.futur.futurcertification.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * 특정한 사용자 아이디로 조회
     * @param userId
     * @return
     */
    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.userId = :userId " +
            "AND u.delYn = false ")
    Optional<User> findByUserId(String userId);


    /**
     * 삭제여부에 따른 사용자 조회
     * SELECT * FROM USER WHERE USER_ID = ? AND DEL_YN = ?
     * @param userId
     * @param delYn
     * @return
     */
    Optional<User> findByUserIdAndDelYn(String userId, boolean delYn);

    /**
     * 삭제여부에 따른 사용자를 Page별로 반환
     * @param delYn Y or N
     * @return
     */
    Page<User> findAllByDelYn(Pageable pageable, boolean delYn);
}
