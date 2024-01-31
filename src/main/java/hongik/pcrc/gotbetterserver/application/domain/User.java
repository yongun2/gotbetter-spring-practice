package hongik.pcrc.gotbetterserver.application.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class User {
    private final int id;
    private final String userId;
    private final String password;
    private final String nickname;
}
