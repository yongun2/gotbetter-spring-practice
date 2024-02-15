package hongik.pcrc.gotbetterserver.ui.requestBody.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserLoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
