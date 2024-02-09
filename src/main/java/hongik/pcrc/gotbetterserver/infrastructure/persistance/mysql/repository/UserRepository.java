package hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository;

import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    Optional<UserEntity> findUserEntityByUserId(String userId);

    Optional<UserEntity> findUserEntityByNickname(String userId);
}
