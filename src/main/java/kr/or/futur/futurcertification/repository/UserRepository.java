package kr.or.futur.futurcertification.repository;

import kr.or.futur.futurcertification.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Spring Security에서 사용하는 함수
     * @param userId
     * @return
     */
    User getByUserId(String userId);
}
