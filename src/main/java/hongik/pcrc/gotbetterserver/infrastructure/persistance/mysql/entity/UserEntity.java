package hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity;

import hongik.pcrc.gotbetterserver.application.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity(name = "USER")
public class UserEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    private String username;

    private String password;

    private String nickname;

    private String email;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "refresh_token_id")
    private RefreshTokenEntity refreshTokenEntity;

    public User toUser() {
        return User.builder()
                .id(this.id)
                .username(this.username)
                .password(this.password)
                .nickname(this.nickname)
                .email(this.email)
                .build();
    }

    public UserEntity(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
    }
    public UserEntity(User user, RefreshTokenEntity refreshTokenEntity) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.refreshTokenEntity = refreshTokenEntity;
    }
}


