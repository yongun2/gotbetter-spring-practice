package hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity;

import hongik.pcrc.gotbetterserver.application.domain.auth.RefreshToken;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Table(name = "refresh_tokens")
@NoArgsConstructor
public class RefreshTokenEntity {
    @Id
    @Column(name = "refresh_token_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    private String token;

    @OneToOne(mappedBy = "refreshTokenEntity")
    private UserEntity userEntity;
    public RefreshTokenEntity(RefreshToken refreshToken) {
        this.id = refreshToken.getId();
        this.token = refreshToken.getToken();
    }

    public RefreshToken toRefreshToken() {
        return RefreshToken.builder()
                .id(this.id)
                .token(this.token)
                .build();
    }
}
