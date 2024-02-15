package hongik.pcrc.gotbetterserver.application.service.user;

import hongik.pcrc.gotbetterserver.application.domain.auth.JWTAuthenticationToken;
import hongik.pcrc.gotbetterserver.application.domain.auth.JWTToken;
import hongik.pcrc.gotbetterserver.application.domain.auth.RefreshToken;
import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.application.service.auth.JWTTokenProvider;
import hongik.pcrc.gotbetterserver.exception.GotbetterException;
import hongik.pcrc.gotbetterserver.exception.MessageType;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.RefreshTokenEntity;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.UserEntity;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserService implements UserOperationUseCase, UserReadUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTTokenProvider jwtTokenProvider;

    @Override
    public User createUser(User request) {

        // check isDuplicate userId
        Optional<UserEntity> userEntityByUserId = userRepository.findUserEntityByUsername(request.getUsername());

        if (userEntityByUserId.isPresent()) {
            throw new GotbetterException(MessageType.DUPLICATED_USER_ID);
        }

        // check isDuplicate nickname
        Optional<UserEntity> userEntityByNickname = userRepository.findUserEntityByNickname(request.getNickname());
        if (userEntityByNickname.isPresent()) {
            throw new GotbetterException(MessageType.DUPLICATE_NICKNAME);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .email(request.getEmail())
                .build();

        userRepository.save(new UserEntity(newUser));

        return newUser;
    }

    @Override
    public JWTToken login(LoginRequest request) {
        User user = userRepository.findUserEntityByUsername(request.getUsername())
                .orElseThrow(() -> new GotbetterException(MessageType.BAD_REQUEST))
                .toUser();


        log.info("user = {} request = {}", user.toString(), request.toString());
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new GotbetterException(MessageType.BAD_REQUEST);
        }

        JWTToken jwtToken = jwtTokenProvider.generateToken(new JWTAuthenticationToken(user.getNickname(), null));
        RefreshToken refreshToken = RefreshToken.builder()
                .token(jwtToken.getRefreshToken())
                .build();

        userRepository.save(new UserEntity(user, new RefreshTokenEntity(refreshToken)));

        return jwtToken;
    }

    @Override
    public void logout(String nickname) {
        userRepository.findUserEntityByNickname(nickname)
                .ifPresentOrElse(
                        userEntity -> {
                            User user = userEntity.toUser();
                            UserEntity refreshTokenDeletedUserEntity = new UserEntity(user, null);
                            userRepository.save(refreshTokenDeletedUserEntity);
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
}
