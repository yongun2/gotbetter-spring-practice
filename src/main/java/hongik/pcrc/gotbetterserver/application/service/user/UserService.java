package hongik.pcrc.gotbetterserver.application.service.user;

import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.exception.GotbetterException;
import hongik.pcrc.gotbetterserver.exception.MessageType;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.UserEntity;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserOperationUseCase{

    private final UserRepository userRepository;
    @Override
    public User createUser(User user) {
        Optional<UserEntity> userEntityByUserId = userRepository.findUserEntityByUserId(user.getUserId());

        if (userEntityByUserId.isPresent()) {
            throw new GotbetterException(MessageType.DUPLICATED_ID);
        }
        return userRepository.save(new UserEntity(user)).toUser();
    }

    @Override
    public boolean checkUserIdDuplicate(String userId) {
        return userRepository.findUserEntityByUserId(userId).isPresent();
    }
}
