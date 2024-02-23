package hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository;

import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    @Query("select u from UserEntity u left join fetch u.refreshTokenEntity where u.username = :username")
    Optional<UserEntity> findUserEntityByUsername(String username);
    @Query("select u from UserEntity u left join fetch u.refreshTokenEntity where u.nickname = :nickname")
    Optional<UserEntity> findUserEntityByNickname(String nickname);
}
