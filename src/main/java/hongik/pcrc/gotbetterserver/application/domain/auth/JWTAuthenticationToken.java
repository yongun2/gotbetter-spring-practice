package hongik.pcrc.gotbetterserver.application.domain.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JWTAuthenticationToken extends AbstractAuthenticationToken {
    private String nickname;

    public JWTAuthenticationToken(String nickname, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.nickname = nickname;
    }
    public JWTAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.nickname;
    }

}
