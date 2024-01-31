package hongik.pcrc.gotbetterserver.ui.view.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import hongik.pcrc.gotbetterserver.application.domain.User;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserView {

    private final int id;
    private final String userId;
    private final String nickname;

    public UserView(User user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.nickname = user.getNickname();
    }
}
