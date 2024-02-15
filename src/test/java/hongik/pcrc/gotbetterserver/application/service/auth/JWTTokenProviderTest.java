package hongik.pcrc.gotbetterserver.application.service.auth;

import hongik.pcrc.gotbetterserver.application.domain.auth.JWTAuthenticationToken;
import hongik.pcrc.gotbetterserver.application.domain.auth.JWTToken;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class JWTTokenProviderTest {


    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @Test
    @DisplayName("jwt 토큰 생성 테스트")
    void generateToken() {
        JWTToken hello = jwtTokenProvider.generateToken(new UsernamePasswordAuthenticationToken("hello", null, null));
    }

    @Test
    @DisplayName("토큰 복호화 테스트")
    void parseClaims() {
        // given
        JWTToken hello = jwtTokenProvider.generateToken(new JWTAuthenticationToken("testUserA", null));
        // when
        Claims accessTokenClaim = jwtTokenProvider.parseClaims(hello.getAccessToken());
        Claims refreshTokenClaim = jwtTokenProvider.parseClaims(hello.getRefreshToken());
        // then
        assertThat((String) accessTokenClaim.get("nickname")).isEqualTo("hello");
        assertThat((String) refreshTokenClaim.get("nickname")).isEqualTo("hello");



        log.info(accessTokenClaim.toString());
        log.info(refreshTokenClaim.toString());
    }

}