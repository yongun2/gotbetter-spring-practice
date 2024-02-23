package hongik.pcrc.gotbetterserver.application.service.user;

import hongik.pcrc.gotbetterserver.application.domain.auth.JWTToken;
import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.application.service.user.UserReadUseCase.LoginRequest;
import hongik.pcrc.gotbetterserver.exception.GotbetterException;
import hongik.pcrc.gotbetterserver.exception.MessageType;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.UserEntity;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static hongik.pcrc.gotbetterserver.application.service.user.UserOperationUseCase.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    public static final String DUMMY_USERNAME = "logintest";
    public static final String DUMMY_PASSWORD = "qwer1234!";
    public static final String DUMMY_NICKNAME = "loginDummy";

    @BeforeEach
    void initDummy() {
        UserCreateCommand command = UserCreateCommand.builder()
                .username(DUMMY_USERNAME)
                .password(DUMMY_PASSWORD)
                .nickname(DUMMY_NICKNAME)
                .build();

        userService.createUser(command);
    }

    @AfterEach
    void deleteDummy() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("유저 회원가입 테스트")
    void createTest() {
        // given
        UserCreateCommand success_command = UserCreateCommand.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserA")
                .email("test@gmail.com")
                .build();
        UserCreateCommand fail_duplicate_nickname = UserCreateCommand.builder()
                .username("asd123412")
                .password("qwer1234!")
                .nickname("testUserA")
                .email("test@gmail.com")
                .build();
        UserCreateCommand fail_long_username = UserCreateCommand.builder()
                .username("asd1234asd1234asd1234asd1234")
                .password("qwer1234!")
                .nickname("testUserB")
                .email("test@gmail.com")
                .build();
        UserCreateCommand fail_long_nickname = UserCreateCommand.builder()
                .username("asd1235")
                .password("qwer1234!")
                .nickname("testUserBtestUserBtestUserBtestUserBtestUserBtestUserB")
                .email("test@gmail.com")
                .build();
        // when
        User user = userService.createUser(success_command);

        // then
        assertThat(user.getUsername()).isEqualTo(success_command.getUsername());
        assertThat(user.getPassword()).isNotEqualTo(success_command.getPassword());

        assertThatThrownBy(() -> userService.createUser(success_command))
                .isInstanceOf(GotbetterException.class)
                .hasMessage(MessageType.DUPLICATED_USER_ID.getMessage());

        assertThatThrownBy(() -> userService.createUser(fail_duplicate_nickname))
                .isInstanceOf(GotbetterException.class)
                .hasMessage(MessageType.DUPLICATE_NICKNAME.getMessage());

        assertThatThrownBy(() -> userService.createUser(fail_long_username))
                .isInstanceOf(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> userService.createUser(fail_long_nickname))
                .isInstanceOf(DataIntegrityViolationException.class);

        assertThat(userRepository.findUserEntityByUsername(fail_long_username.getUsername()).isEmpty()).isTrue();
        assertThat(userRepository.findUserEntityByNickname(fail_long_nickname.getNickname()).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("로그인 테스트")
    void login() {
        // given
        LoginRequest success_login_request = LoginRequest.builder()
                .username(DUMMY_USERNAME)
                .password(DUMMY_PASSWORD)
                .build();

        LoginRequest fail_login_request_bad_username = LoginRequest.builder()
                .username("")
                .password(DUMMY_PASSWORD)
                .build();

        LoginRequest fail_login_request_bad_password = LoginRequest.builder()
                .username(DUMMY_USERNAME)
                .password("")
                .build();
        // when

        JWTToken jwtToken = userService.login(success_login_request);
        Optional<UserEntity> userEntity = userRepository.findUserEntityByUsername(DUMMY_USERNAME);
        // then
        assertThat(userEntity.isPresent()).isTrue();
        assertThat(userEntity.get().getRefreshTokenEntity()).isNotNull();
        assertThat(userEntity.get().getRefreshTokenEntity().getToken()).isEqualTo(jwtToken.getRefreshToken());

        assertThatThrownBy(() -> userService.login(fail_login_request_bad_username))
                .isInstanceOf(GotbetterException.class)
                .hasMessage(MessageType.USER_NOT_FOUND.getMessage());

        assertThatThrownBy(() -> userService.login(fail_login_request_bad_password))
                .isInstanceOf(GotbetterException.class)
                .hasMessage(MessageType.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logout() {
        // given
        LoginRequest success_login_request = LoginRequest.builder()
                .username(DUMMY_USERNAME)
                .password(DUMMY_PASSWORD)
                .build();
        // when
        userService.login(success_login_request);
        userService.logout(DUMMY_NICKNAME);
        // then
        Optional<UserEntity> userEntity = userRepository.findUserEntityByUsername(DUMMY_USERNAME);
        assertThat(userEntity.isPresent()).isTrue();
        assertThat(userEntity.get().getRefreshTokenEntity()).isNull();
        assertThatThrownBy(() -> userService.logout("fd"))
                .isInstanceOf(GotbetterException.class)
                .hasMessage(MessageType.USER_NOT_FOUND.getMessage());
    }

}