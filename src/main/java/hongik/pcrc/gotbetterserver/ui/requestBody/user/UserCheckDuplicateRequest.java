package hongik.pcrc.gotbetterserver.ui.requestBody.user;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserCheckDuplicateRequest {

    private String userId;

    private String nickname;
}
