package hongik.pcrc.gotbetterserver.application.service.user;

import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void createUser() {
        // given
        User userA = User.builder()
                .id(1)
                .userId("userA")
                .password("1234")
                .nickname("hello")
                .build();
        // when
        User user = userService.createUser(userA);
        // then
        assertThat(user.getUserId()).isEqualTo("userA");

    }

    @Test
    void checkUserIdDuplicate() {
        // given
        User userA = User.builder()
                .id(1)
                .userId("userA")
                .password("1234")
                .nickname("hello")
                .build();
        User user = userService.createUser(userA);

        // when
        boolean check1 = userService.checkUserIdDuplicate("userA");
        boolean check2 = userService.checkUserIdDuplicate("hello");
        // then
        assertThat(check1).isTrue();
        assertThat(check2).isFalse();
    }
}