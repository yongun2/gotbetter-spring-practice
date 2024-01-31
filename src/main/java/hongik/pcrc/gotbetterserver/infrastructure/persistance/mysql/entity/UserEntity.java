package hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity;

import hongik.pcrc.gotbetterserver.application.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "USER")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String userId;

    private String password;

    private String nickname;

    public User toUser() {
        return User.builder()
                .id(this.id)
                .userId(this.userId)
                .password(this.password)
                .nickname(this.nickname)
                .build();
    }

    public UserEntity(User user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
    }
}


