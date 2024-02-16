package hongik.pcrc.gotbetterserver.ui.controller;

import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.application.domain.auth.JWTToken;
import hongik.pcrc.gotbetterserver.application.service.user.UserReadUseCase;
import hongik.pcrc.gotbetterserver.application.service.user.UserService;
import hongik.pcrc.gotbetterserver.exception.GotbetterException;
import hongik.pcrc.gotbetterserver.exception.MessageType;
import hongik.pcrc.gotbetterserver.ui.requestBody.user.UserCreateRequest;
import hongik.pcrc.gotbetterserver.ui.requestBody.user.UserLoginRequest;
import hongik.pcrc.gotbetterserver.ui.view.ApiResponseView;
import hongik.pcrc.gotbetterserver.ui.view.user.JWTTokenView;
import hongik.pcrc.gotbetterserver.ui.view.user.UserView;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseView<UserView>> signup(@RequestBody @Validated UserCreateRequest createRequest) {

        // check id pattern
        Matcher usernameMatcher = validateUsername(createRequest.getUsername());

        if (!usernameMatcher.matches()) {
            throw new GotbetterException(MessageType.BAD_USER_ID_PATTERN);
        }

        // check password pattern
        Matcher passwordMatcher = validatePassword(createRequest.getPassword());

        if (!passwordMatcher.matches()) {
            throw new GotbetterException(MessageType.BAD_PASSWORD_PATTERN);
        }

        // check nickname pattern
        if (!validateNickname(createRequest.getNickname())) {
            throw new GotbetterException(MessageType.BAD_NICKNAME_PATTERN);
        }

        User createdUser = userService.createUser(
                User.builder()
                        .username(createRequest.getUsername())
                        .password(createRequest.getPassword())
                        .nickname(createRequest.getNickname())
                        .build()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponseView<>(new UserView(createdUser)));

    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseView<JWTTokenView>> login(@RequestBody UserLoginRequest request) {
        JWTToken jwtToken = userService.login(UserReadUseCase.LoginRequest.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseView<>(new JWTTokenView(jwtToken)));
    }

    @GetMapping("/duplicate")
    public ResponseEntity<ApiResponseView<UserView>> checkDuplicate(@RequestParam @Nullable String username, @RequestParam @Nullable String nickname) {

        if (username != null && nickname != null || username == null && nickname == null) {
            throw new GotbetterException(MessageType.BAD_REQUEST);
        }

        boolean isDuplicate;
        User result = null;
        // check is userId duplicate
        if (username != null) {

            if (!validateUsername(username).matches()) {
                throw new GotbetterException(MessageType.BAD_USER_ID_PATTERN);
            }

            isDuplicate = userService.checkUsernameDuplicate(username);
            if (isDuplicate) {
                throw new GotbetterException(MessageType.DUPLICATED_USER_ID);
            }

            result = User.builder()
                    .username(username)
                    .build();
        }

        // check is nickname duplicate
        if (nickname != null) {

            if (!validateNickname(nickname)) {
                throw new GotbetterException(MessageType.BAD_NICKNAME_PATTERN);
            }

            isDuplicate = userService.checkUserNicknameDuplicate(nickname);
            if (isDuplicate) {
                throw new GotbetterException(MessageType.DUPLICATE_NICKNAME);
            }
            result = User.builder()
                    .nickname(nickname)
                    .build();
        }

        if (result == null) {
            throw new GotbetterException(MessageType.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponseView<>(new UserView(result)));
    }

    @GetMapping("/test")
    ResponseEntity<String> hello(@AuthenticationPrincipal String user) {
        log.info("user");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("hello world {}", authentication.getDetails());

        return ResponseEntity.status(HttpStatus.OK)
                .body("hello");
    }

    private Matcher validateUsername(String userId) {
        String userIdPattern = "^[a-z0-9_-]{5,20}$";
        return Pattern.compile(userIdPattern)
                .matcher(userId);
    }

    private Matcher validatePassword(String password) {
        String passwordPattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\",./<>?])[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{};':\",./<>?]{8,16}$";
        return Pattern.compile(passwordPattern)
                .matcher(password);
    }

    private boolean validateNickname(String nickname) {
        return !nickname.isBlank() && nickname.length() >= 2 && nickname.length() <= 12;
    }
}
