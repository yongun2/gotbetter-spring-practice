package hongik.pcrc.gotbetterserver.application.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class User {
    private final int id;
    private final String username;
    private final String password;
    private final String nickname;
    private final String email;
}

