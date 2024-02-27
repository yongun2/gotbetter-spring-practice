package hongik.pcrc.gotbetterserver.utils;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithJWTMockUserSecurityContextFactory.class)
public @interface WithCustomMockUser {

    int id() default 5903;
    String username() default "asd1234";

    String password() default "{bcrypt}$2a$10$j2yEpLwP7EEo3a/wpgIk8uZFJgz6L85SsB1qYYnaXEXMpvum8tb8q";

    String nickname() default "testUserA";

    String email() default "test@gmail.com";
}
