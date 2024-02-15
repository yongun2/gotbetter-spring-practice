package hongik.pcrc.gotbetterserver.application.service.user;

import hongik.pcrc.gotbetterserver.application.domain.auth.JWTToken;
import hongik.pcrc.gotbetterserver.application.domain.User;

public interface UserOperationUseCase {
    User createUser(User user);

    /**
     * 일반 아이디 패스워드 로그인
     * @param request: String username, String password
     * @return 생성한 JWT 토큰
     */
    JWTToken login(UserReadUseCase.LoginRequest request);

    /**
     * 로그아웃 String nickname
     */
    void logout(String nickname);
}
