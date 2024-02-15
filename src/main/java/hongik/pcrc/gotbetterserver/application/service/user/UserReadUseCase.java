package hongik.pcrc.gotbetterserver.application.service.user;

import hongik.pcrc.gotbetterserver.application.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public interface UserReadUseCase {

    User findUserByUsername(String userId);

    boolean checkUsernameDuplicate(String userId);

    boolean checkUserNicknameDuplicate(String nickname);

    @Getter
    @Builder
    @ToString
    class LoginRequest {
        private final String username;
        private final String password;
    }
}
