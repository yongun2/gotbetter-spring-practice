package hongik.pcrc.gotbetterserver.application.service.user;

import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.application.domain.auth.JWTAuthenticationToken;
import hongik.pcrc.gotbetterserver.application.domain.auth.JWTToken;
import hongik.pcrc.gotbetterserver.application.domain.auth.RefreshToken;
import hongik.pcrc.gotbetterserver.application.service.auth.JWTTokenProvider;
import hongik.pcrc.gotbetterserver.exception.GotbetterException;
import hongik.pcrc.gotbetterserver.exception.MessageType;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.UserEntity;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserService implements UserOperationUseCase, UserReadUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public User createUser(UserCreateCommand command) {

        // check isDuplicate userId
        Optional<UserEntity> userEntityByUserId = userRepository.findUserEntityByUsername(command.getUsername());

        if (userEntityByUserId.isPresent()) {
            throw new GotbetterException(MessageType.DUPLICATED_USER_ID);
        }

        // check isDuplicate nickname
        Optional<UserEntity> userEntityByNickname = userRepository.findUserEntityByNickname(command.getNickname());
        if (userEntityByNickname.isPresent()) {
            throw new GotbetterException(MessageType.DUPLICATE_NICKNAME);
        }

        String encodedPassword = passwordEncoder.encode(command.getPassword());

        User newUser = User.builder()
                .username(command.getUsername())
                .password(encodedPassword)
                .nickname(command.getNickname())
                .email(command.getEmail())
                .build();

        return userRepository.save(new UserEntity(newUser)).toUser();
    }

    @Override
    @Transactional
    public JWTToken login(LoginRequest request) {
        User user = userRepository.findUserEntityByUsername(request.getUsername())
                .orElseThrow(() -> new GotbetterException(MessageType.USER_NOT_FOUND))
                .toUser();

        validatePassword(request.getPassword(), user.getPassword());

        JWTToken jwtToken = jwtTokenProvider.generateToken(new JWTAuthenticationToken(user.getNickname(), null));
        RefreshToken refreshToken = RefreshToken.builder()
                .token(jwtToken.getRefreshToken())
                .build();

        User loginUser = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .refreshToken(refreshToken)
                .build();

        userRepository.save(new UserEntity(loginUser));

        return jwtToken;
    }

    @Override
    public void logout(String nickname) {
        userRepository.findUserEntityByNickname(nickname)
                .ifPresentOrElse(
                        userEntity -> {
                            User deleteRefreshToken = User.builder()
                                    .id(userEntity.getId())
                                    .username(userEntity.getUsername())
                                    .password(userEntity.getPassword())
                                    .nickname(userEntity.getNickname())
                                    .email(userEntity.getEmail())
                                    .refreshToken(null)
                                    .build();

                            userRepository.save(new UserEntity(deleteRefreshToken));
                        },
                        () -> {
                            throw new GotbetterException(MessageType.USER_NOT_FOUND);
                        }
                );
    }

    @Override
    public User findUserByUsername(String userId) {
        return userRepository.findUserEntityByUsername(userId)
                .orElseThrow(() -> new GotbetterException(MessageType.USER_NOT_FOUND))
                .toUser();
    }

    @Override
    public boolean checkUsernameDuplicate(String userId) {
        return userRepository.findUserEntityByUsername(userId).isPresent();
    }

    @Override
    public boolean checkUserNicknameDuplicate(String nickname) {
        return userRepository.findUserEntityByNickname(nickname).isPresent();
    }

    private void validatePassword(String requestPassword, String encodedPassword) {
        if (!passwordEncoder.matches(requestPassword, encodedPassword)) {
            throw new GotbetterException(MessageType.USER_NOT_FOUND);
        }
    }
}
