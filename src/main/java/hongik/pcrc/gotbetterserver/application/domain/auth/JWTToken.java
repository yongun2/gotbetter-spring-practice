package hongik.pcrc.gotbetterserver.application.domain.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class JWTToken {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
