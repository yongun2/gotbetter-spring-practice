package hongik.pcrc.gotbetterserver.application.domain.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class RefreshToken {
    private int id;
    private String token;
}
