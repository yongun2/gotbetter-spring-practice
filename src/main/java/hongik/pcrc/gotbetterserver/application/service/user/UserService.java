package hongik.pcrc.gotbetterserver.application.service.user;

import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.exception.GotbetterException;
import hongik.pcrc.gotbetterserver.exception.MessageType;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.UserEntity;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserOperationUseCase, UserReadUseCase {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {

        // check isDuplicate userId
        Optional<UserEntity> userEntityByUserId = userRepository.findUserEntityByUserId(user.getUserId());

        if (userEntityByUserId.isPresent()) {
            throw new GotbetterException(MessageType.DUPLICATED_USER_ID);
        }

        // check isDuplicate nickname
        Optional<UserEntity> userEntityByNickname = userRepository.findUserEntityByNickname(user.getNickname());
        if (userEntityByNickname.isPresent()) {
            throw new GotbetterException(MessageType.DUPLICATE_NICKNAME);
        }

        return userRepository.save(new UserEntity(user)).toUser();
    }

    @Override
    public boolean checkUserIdDuplicate(String userId) {
        return userRepository.findUserEntityByUserId(userId).isPresent();
    }

    @Override
    public boolean checkUserNicknameDuplicate(String nickname) {
        return userRepository.findUserEntityByNickname(nickname).isPresent();
    }


}
