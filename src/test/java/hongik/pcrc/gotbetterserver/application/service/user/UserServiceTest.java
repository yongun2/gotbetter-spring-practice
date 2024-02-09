package hongik.pcrc.gotbetterserver.application.service.user;

import hongik.pcrc.gotbetterserver.application.domain.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

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
                .userId("testUserA")
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
                .userId("철수")
                .password("1234")
                .nickname("hello")
                .build();
        User user = userService.createUser(userA);

        // when
        boolean check1 = userService.checkUserIdDuplicate("userA");
        boolean check2 = userService.checkUserIdDuplicate("hello");
        boolean check3 = userService.checkUserIdDuplicate("usera");
        boolean check4 = userService.checkUserIdDuplicate("철수");
        // then
        assertThat(check1).isFalse();
        assertThat(check2).isFalse();
        assertThat(check3).isFalse();
        assertThat(check4).isTrue();
    }
}