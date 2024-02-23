package hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository;

import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.application.domain.auth.JWTAuthenticationToken;
import hongik.pcrc.gotbetterserver.application.domain.auth.JWTToken;
import hongik.pcrc.gotbetterserver.application.domain.auth.RefreshToken;
import hongik.pcrc.gotbetterserver.application.service.auth.JWTTokenProvider;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @AfterEach
    void deleteDummy() {
        userRepository.deleteAll();
    }



    /**
     * UNIQUE Constraint, Max Length 테스트 위해 @Transactional 제외
     */
    @Test
    @DisplayName("유저 생성 테스트")
    void createUser() {
        // given
        User saveSuccess = User.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserA")
                .build();
        User testUserDuplicateUsername = User.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserB")
                .build();
        User testUserDuplicateNickname = User.builder()
                .username("asd1235")
                .password("qwer1234!")
                .nickname("testUserA")
                .build();
        // when
        userRepository.saveAndFlush(new UserEntity(saveSuccess));
        // then

        // Error test
        assertThatThrownBy(() -> userRepository.save(new UserEntity(testUserDuplicateUsername)))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> userRepository.save(new UserEntity(testUserDuplicateNickname)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @Transactional
    @DisplayName("유저 조회 테스트")
    void findUserBy_() {
        // given
        User testUserA = User.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserA")
                .build();

        UserEntity result = userRepository.save(new UserEntity(testUserA));

        // when
        Optional<UserEntity> resultByUsername = userRepository.findUserEntityByUsername(result.getUsername());
        Optional<UserEntity> resultByNickname = userRepository.findUserEntityByNickname(result.getNickname());
        Optional<UserEntity> failed_nickname = userRepository.findUserEntityByNickname("");
        Optional<UserEntity> failed_username = userRepository.findUserEntityByUsername("");

        // then

        assertThat(resultByUsername.isPresent()).isTrue();
        assertThat(resultByUsername.get().getId()).isEqualTo(result.getId());
        assertThat(resultByUsername.get().getUsername()).isEqualTo(result.getUsername());
        assertThat(resultByUsername.get().getRefreshTokenEntity()).isNull();

        assertThat(resultByNickname.isPresent()).isTrue();
        assertThat(resultByNickname.get().getId()).isEqualTo(result.getId());
        assertThat(resultByNickname.get().getUsername()).isEqualTo(result.getUsername());
        assertThat(resultByNickname.get().getRefreshTokenEntity()).isNull();

        assertThat(failed_nickname.isEmpty()).isTrue();
        assertThat(failed_username.isEmpty()).isTrue();


    }

    @Test
    @DisplayName("유저 수정 테스트")
    void updateUser() {
        // given
        User defaultUser = User.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserB")
                .build();
        UserEntity defaultUserEntity = userRepository.save(new UserEntity(defaultUser));
        User defaultUserSaveResult = defaultUserEntity.toUser();

        // when
        User updateUser = User.builder()
                .id(defaultUserEntity.getId())
                .username("asd1235")
                .nickname("testUserC")
                .email("hello@gmail.com")
                .build();

        User fail_updateUser_long_username = User.builder()
                .id(defaultUserEntity.getId())
                .username("asd1235asd1235asd1235asd1235")
                .nickname("testUserC")
                .email("hello@gmail.com")
                .build();

        User fail_updateUser_long_nickname = User.builder()
                .id(defaultUserEntity.getId())
                .username("asd1235asd1235asd1235asd1235")
                .nickname("testUserCtestUserCtestUserCtestUserCtestUserCtestUserCtestUserCtestUserC")
                .email("hello@gmail.com")
                .build();


        UserEntity updateResult = userRepository.save(new UserEntity(updateUser));
        //then

        assertThat(updateResult.getUsername()).isNotEqualTo(defaultUserSaveResult.getUsername());
        assertThat(updateResult.getEmail()).isNotEqualTo(defaultUserSaveResult.getEmail());
        assertThat(updateResult.toUser().toString()).isNotSameAs(defaultUserSaveResult.toString());

        assertThatThrownBy(() -> userRepository.save(new UserEntity(fail_updateUser_long_username)))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> userRepository.save(new UserEntity(fail_updateUser_long_nickname)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }


    @Test
    @DisplayName("리프레시 토큰 저장")
    void refreshToken() {
        // given
        User defaultUser = User.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserB")
                .build();

        userRepository.save(new UserEntity(defaultUser)); // 회원가입

        // when
        Optional<UserEntity> findResult = userRepository.findUserEntityByNickname(defaultUser.getNickname());
        assertThat(findResult.isPresent()).isTrue();

        UserEntity loginUserEntity = findResult.get();

        JWTAuthenticationToken authenticationToken = new JWTAuthenticationToken(defaultUser.getUsername(), null);
        JWTToken jwtToken = jwtTokenProvider.generateToken(authenticationToken);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(jwtToken.getRefreshToken())
                .build();

        User storeRefreshToken = User.builder()
                .id(loginUserEntity.getId())
                .username(loginUserEntity.getUsername())
                .password(loginUserEntity.getPassword())
                .nickname(loginUserEntity.getNickname())
                .email(loginUserEntity.getEmail())
                .refreshToken(refreshToken)
                .build();

        UserEntity save = userRepository.save(new UserEntity(storeRefreshToken));
        // then
        assertThat(save.toUser().getRefreshToken().getToken()).isEqualTo(jwtToken.getRefreshToken());
    }

    @Test
    @DisplayName("리프레시 토큰 삭제")
    void deleteRefreshToken() {
        // given
        User defaultUser = User.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserB")
                .build();

        userRepository.save(new UserEntity(defaultUser)); // 회원가입

        // when
        Optional<UserEntity> findResult = userRepository.findUserEntityByNickname(defaultUser.getNickname());
        assertThat(findResult.isPresent()).isTrue();

        UserEntity loginUserEntity = findResult.get();

        JWTAuthenticationToken authenticationToken = new JWTAuthenticationToken(defaultUser.getUsername(), null);
        JWTToken jwtToken = jwtTokenProvider.generateToken(authenticationToken);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(jwtToken.getRefreshToken())
                .build();

        User storeRefreshToken = User.builder()
                .id(loginUserEntity.getId())
                .username(loginUserEntity.getUsername())
                .password(loginUserEntity.getPassword())
                .nickname(loginUserEntity.getNickname())
                .email(loginUserEntity.getEmail())
                .refreshToken(refreshToken)
                .build();

        UserEntity save = userRepository.save(new UserEntity(storeRefreshToken));

        User deleteRefreshToken = User.builder()
                .id(save.getId())
                .username(save.getUsername())
                .password(save.getPassword())
                .nickname(save.getNickname())
                .email(save.getEmail())
                .refreshToken(null)
                .build();

        UserEntity deleteResult = userRepository.save(new UserEntity(deleteRefreshToken));
        // then
        assertThat(deleteResult.getRefreshTokenEntity()).isNull();
    }

    @Test
    @DisplayName("리프레시 토큰 갱신")
    void updateRefreshToken() {

    }
}