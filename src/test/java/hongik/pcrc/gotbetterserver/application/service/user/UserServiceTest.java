package hongik.pcrc.gotbetterserver.application.service.user;

import hongik.pcrc.gotbetterserver.application.domain.auth.JWTToken;
import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.application.service.user.UserReadUseCase.LoginRequest;
import hongik.pcrc.gotbetterserver.exception.GotbetterException;
import hongik.pcrc.gotbetterserver.exception.MessageType;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.RefreshTokenEntity;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.UserEntity;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser() {
        // given
        User userA = User.builder()
                .id(1)
                .username("testUserA")
                .password("Qwer1234!")
                .nickname("hello")
                .build();
        // when
        User user = userService.createUser(userA);
        // then
        assertThat(user.getUsername()).isEqualTo("testUserA");
    }

    @Test
    void checkUserIdDuplicate() {
        // given
        User userA = User.builder()
                .id(1)
                .username("testUserA")
                .password("Qwer1234!")
                .nickname("hello")
                .build();
        User user = userService.createUser(userA);

        // when
        boolean check1 = userService.checkUsernameDuplicate("userA");
        boolean check2 = userService.checkUsernameDuplicate("df");
        boolean check3 = userService.checkUsernameDuplicate("usera");
        boolean check4 = userService.checkUsernameDuplicate("testUserA");
        // then
        assertThat(check1).isFalse();
        assertThat(check2).isFalse();
        assertThat(check3).isFalse();
        assertThat(check4).isTrue();
    }

    @Test
    @DisplayName("로그인 테스트")
    @Transactional
    void loginTest() {
        // given
        User testUserA = User.builder()
                .username("qwer1235")
                .password("qwer1234!")
                .nickname("testUserC")
                .build();

        userService.createUser(testUserA);
        // when
        LoginRequest request = LoginRequest.builder()
                .username("qwer1235")
                .password("qwer1234!")
                .build();
        JWTToken jwt = userService.login(request);

        Optional<UserEntity> userEntityByNickname = userRepository.findUserEntityByNickname("testUserC");
        // then
        userEntityByNickname.ifPresentOrElse(userEntity -> {
            RefreshTokenEntity refreshTokenEntity = userEntity.getRefreshTokenEntity();
            assertThat(jwt.getRefreshToken()).isEqualTo(refreshTokenEntity.getToken());
        }, () -> {});

    }

    @Test
    @DisplayName("로그아웃 테스트")
    @Transactional
    void logout() {
        // given

        // when
        userService.logout("testUserB");
        // then
        userRepository.findUserEntityByUsername("test1")
                .ifPresent(userEntity -> {
                    log.info(String.valueOf(userEntity.getRefreshTokenEntity()));
                    assertThat(userEntity.getRefreshTokenEntity()).isNull();
                });

        Assertions.assertThatThrownBy(() -> userService.logout("helloworld"))
                .isInstanceOf(GotbetterException.class)
                .hasMessage(MessageType.USER_NOT_FOUND.getMessage());


    }
}