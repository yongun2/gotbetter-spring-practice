package hongik.pcrc.gotbetterserver.application.service.auth;

import hongik.pcrc.gotbetterserver.application.domain.auth.JWTToken;
import hongik.pcrc.gotbetterserver.exception.GotbetterException;
import hongik.pcrc.gotbetterserver.exception.MessageType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JWTTokenProvider {

    private final SecretKey key;

    @Value("${jwt.access-token-expiration}")
    private long ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${jwt.refresh-token-expiration}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    public JWTTokenProvider(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public JWTToken generateToken(Authentication authentication) {
        Date now = new Date();

        log.info("ACCESS_TOKEN_EXPIRATION_TIME = {}", ACCESS_TOKEN_EXPIRATION_TIME);
        log.info("REFRESH_TOKEN_EXPIRATION_TIME = {}", REFRESH_TOKEN_EXPIRATION_TIME);
        String accessToken = Jwts.builder()
                .subject("USER")
                .claim("nickname", authentication.getPrincipal())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(key)
                .compact();

        String refreshToken = Jwts.builder()
                .subject("USER")
                .claim("nickname", authentication.getPrincipal())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(key)
                .compact();

        return JWTToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new GotbetterException(MessageType.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new GotbetterException(MessageType.INVALID_TOKEN);
        }
    }

}

