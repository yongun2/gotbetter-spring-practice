package hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository;

import hongik.pcrc.gotbetterserver.application.domain.auth.RefreshToken;
import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.RefreshTokenEntity;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class RefreshTokenRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("리프레시 토큰 저장 테스트")
    void saveRefreshToken() {
        // given
        User testUserA = User.builder()
                .username("qwer1234")
                .password("qwer1234!")
                .nickname("testUserA")
                .build();
        userRepository.save(new UserEntity(testUserA));

        Optional<UserEntity> findUser = userRepository.findUserEntityByUsername("qwer1234");
        findUser.ifPresentOrElse(
                (userEntity -> {
                    assertThat(userEntity.getRefreshTokenEntity()).isNull();

                    RefreshToken refreshToken = RefreshToken.builder()
                            .token("testToken")
                            .build();

                    User user = userEntity.toUser();

                    User.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .email(user.getEmail())
                            .nickname(user.getNickname());

                    UserEntity userEntity1 = new UserEntity(user, new RefreshTokenEntity(refreshToken));
                    userRepository.save(userEntity1);
                })
                ,() -> {}
        );

        // when

        // then

    }
}