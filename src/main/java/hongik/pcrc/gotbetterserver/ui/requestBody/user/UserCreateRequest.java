package hongik.pcrc.gotbetterserver.ui.requestBody.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Getter
@ToString
@NoArgsConstructor
public class UserCreateRequest {
    @NotNull
    @Length(max = 12)
    private String userId;
    @NotBlank
    @Length(max = 20)
    private String password;
    @NotBlank
    @Length(max = 10)
    private String nickname;
}
