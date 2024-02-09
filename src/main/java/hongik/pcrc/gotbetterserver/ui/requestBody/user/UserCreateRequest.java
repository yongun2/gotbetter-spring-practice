package hongik.pcrc.gotbetterserver.ui.requestBody.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserCreateRequest {
    @NotNull
    private String userId;
    @NotBlank
    private String password;
    @NotBlank
    private String nickname;
}
