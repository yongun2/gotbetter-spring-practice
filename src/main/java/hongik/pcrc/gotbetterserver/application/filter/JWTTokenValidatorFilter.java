package hongik.pcrc.gotbetterserver.application.filter;

import hongik.pcrc.gotbetterserver.application.config.SecurityConstants;
import hongik.pcrc.gotbetterserver.application.domain.auth.JWTAuthenticationToken;
import hongik.pcrc.gotbetterserver.application.service.auth.JWTTokenProvider;
import hongik.pcrc.gotbetterserver.exception.GotbetterException;
import hongik.pcrc.gotbetterserver.exception.MessageType;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.UserEntity;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class JWTTokenValidatorFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    private final JWTTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(SecurityConstants.JWT_HEADER);

        if (jwt != null) {
            try {
                Claims claims = jwtTokenProvider.parseClaims(jwt);
                String nickname = String.valueOf(claims.get("nickname"));

                Optional<UserEntity> user = userRepository.findUserEntityByNickname(nickname);

                user.ifPresentOrElse(
                        (userEntity -> {
                            JWTAuthenticationToken auth = new JWTAuthenticationToken(userEntity.getNickname(), null);
                            auth.setDetails(userEntity.toUser());
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }),
                        () -> {
                            throw new GotbetterException(MessageType.USER_NOT_FOUND);
                        }
                );
            } catch (ExpiredJwtException err) {
                throw new GotbetterException(MessageType.TOKEN_EXPIRED);
            }
        }

        filterChain.doFilter(request, response);
    }
}
