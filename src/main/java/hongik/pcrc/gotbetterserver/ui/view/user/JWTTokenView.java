package hongik.pcrc.gotbetterserver.ui.view.user;

import hongik.pcrc.gotbetterserver.application.domain.auth.JWTToken;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class JWTTokenView {
    private final String accessToken;
    private final String refreshToken;

    public JWTTokenView(JWTToken jwtToken) {
        this.accessToken = jwtToken.getAccessToken();
        this.refreshToken = jwtToken.getRefreshToken();
    }
}
