package hongik.pcrc.gotbetterserver.utils;

import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.application.domain.auth.JWTAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithJWTMockUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {
    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser mockUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User details = User.builder()
                .id(mockUser.id())
                .username(mockUser.username())
                .nickname(mockUser.nickname())
                .email(mockUser.email())
                .build();
        JWTAuthenticationToken authenticationToken = new JWTAuthenticationToken(mockUser.nickname(), null);
        authenticationToken.setDetails(details);

        context.setAuthentication(authenticationToken);

        return context;
    }
}
