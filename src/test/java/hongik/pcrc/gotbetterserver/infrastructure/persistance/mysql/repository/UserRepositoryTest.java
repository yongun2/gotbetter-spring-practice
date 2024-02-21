package hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository;

import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.UserEntity;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.assertj.core.api.Assertions;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    /**
     * UNIQUE Constraint 테스트 위해 @Transactional 제외
     */
    @Test
    @DisplayName("유저 생성 테스트")
    void createUser() {
        // given
        User testUserB = User.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserB")
                .build();
        User testUserDuplicateUsername = User.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserC")
                .build();
        User testUserDuplicateNickname = User.builder()
                .username("asd1235")
                .password("qwer1234!")
                .nickname("testUserB")
                .build();
        // when
        UserEntity testUserA_result = userRepository.saveAndFlush(new UserEntity(testUserB));
        // then

        // success
        assertThat(testUserA_result.getUsername()).isEqualTo(testUserB.getUsername());
        assertThat(testUserA_result.getRefreshTokenEntity()).isNull();

        // Error test
        assertThatThrownBy(() -> userRepository.save(new UserEntity(testUserDuplicateUsername)))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> userRepository.save(new UserEntity(testUserDuplicateNickname)))
                .isInstanceOf(DataIntegrityViolationException.class);

        userRepository.delete(testUserA_result);
    }

    @Test
    @Transactional
    @DisplayName("유저 조회 테스트")
    void findUserBy_() {
        // given
        User testUserB = User.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserB")
                .build();
        UserEntity testUserB_result = userRepository.save(new UserEntity(testUserB));

        // when
        Optional<UserEntity> resultByUsername = userRepository.findUserEntityByUsername(testUserB_result.getUsername());
        Optional<UserEntity> resultByNickname = userRepository.findUserEntityByNickname(testUserB_result.getNickname());
        Optional<UserEntity> failed_nickname = userRepository.findUserEntityByNickname("");
        Optional<UserEntity> failed_username = userRepository.findUserEntityByUsername("");
        // then

        resultByUsername.ifPresent(
                (userEntity) -> {
                    User user = userEntity.toUser();
                    assertThat(user.getId()).isEqualTo(testUserB_result.getId());
                    assertThat(user.getUsername()).isEqualTo(testUserB_result.getUsername());
                    assertThat(user.getRefreshToken()).isNull();
                }
        );

        resultByNickname.ifPresent(
                (userEntity) -> {
                    User user = userEntity.toUser();
                    assertThat(user.getId()).isEqualTo(testUserB_result.getId());
                    assertThat(user.getNickname()).isEqualTo(testUserB_result.getNickname());
                    assertThat(user.getRefreshToken()).isNull();
                }
        );

        assertThat(failed_nickname.isEmpty()).isTrue();
        assertThat(failed_username.isEmpty()).isTrue();
    }

    @Test
    @Transactional
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

        UserEntity updateResult = userRepository.save(new UserEntity(updateUser));
        //then
        log.info(defaultUserSaveResult.toString());
        log.info(updateResult.toUser().toString());
        assertThat(updateResult.getUsername()).isNotEqualTo(defaultUserSaveResult.getUsername());
        assertThat(updateResult.getEmail()).isNotEqualTo(defaultUserSaveResult.getEmail());
        assertThat(updateResult.toUser().toString()).isNotSameAs(defaultUserSaveResult.toString());
    }
}