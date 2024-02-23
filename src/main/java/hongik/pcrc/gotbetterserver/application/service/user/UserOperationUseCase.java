package hongik.pcrc.gotbetterserver.application.service.user;

import hongik.pcrc.gotbetterserver.application.domain.auth.JWTToken;
import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.application.domain.auth.RefreshToken;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static hongik.pcrc.gotbetterserver.application.service.user.UserReadUseCase.*;

public interface UserOperationUseCase {
    User createUser(UserCreateCommand user);

    /**
     * 일반 아이디 패스워드 로그인
     * @param request: String username, String password
     * @return 생성한 JWT 토큰
     */
    JWTToken login(LoginRequest request);

    /**
     * 로그아웃 String nickname
     */
    void logout(String nickname);

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class UserCreateCommand {
        private final String username;
        private final String password;
        private final String nickname;
        private final String email;
    }

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class UserUpdateCommand {
        private final int id;
        private final String username;
        private final String password;
        private final String nickname;
        private final String email;
        private final RefreshToken refreshToken;
    }

}
