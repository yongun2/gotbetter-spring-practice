package hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository;

import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.aspectj.lang.annotation.After;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    void userCreate() {
        User userA = User.builder()
                .id(1)
                .userId("hello")
                .password("world")
                .nickname("userA")
                .build();

        userRepository.save(new UserEntity(userA));

        Optional<UserEntity> findUser = userRepository.findById(1);
        if (findUser.isPresent()) {
            UserEntity findUserEntity = findUser.get();
            User user = findUserEntity.toUser();
            assertThat(user.getNickname()).isEqualTo("userA");
            assertThat(user.getId()).isEqualTo(1);
        }

    }

    @Test
    @Transactional
    void findUserByUserId() {
        //given
        User userA = User.builder()
                .id(1)
                .userId("hello")
                .password("world")
                .nickname("userA")
                .build();
        //when
        userRepository.save(new UserEntity(userA));

        Optional<UserEntity> hello = userRepository.findUserEntityByUserId("hello");
        Optional<UserEntity> abs = userRepository.findUserEntityByUserId("abs");

        //then
        assertThat(hello.isPresent()).isTrue();
        assertThat(abs.isPresent()).isFalse();

    }



}