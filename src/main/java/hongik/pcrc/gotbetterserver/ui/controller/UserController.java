package hongik.pcrc.gotbetterserver.ui.controller;

import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.application.service.user.UserService;
import hongik.pcrc.gotbetterserver.exception.GotbetterException;
import hongik.pcrc.gotbetterserver.exception.MessageType;
import hongik.pcrc.gotbetterserver.ui.requestBody.user.UserCreateRequest;
import hongik.pcrc.gotbetterserver.ui.view.ApiResponseView;
import hongik.pcrc.gotbetterserver.ui.view.user.UserView;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder;


    @PostMapping("")
    public ResponseEntity<ApiResponseView<UserView>> signup(@RequestBody @Validated UserCreateRequest createRequest) {

        // check id pattern
        String userIdPattern = "^[a-z0-9_-]{5,20}$";
        Matcher userIdMatcher = Pattern.compile(userIdPattern)
                .matcher(createRequest.getUserId());

        if (!userIdMatcher.matches()) {
            throw new GotbetterException(MessageType.BAD_USER_ID_PATTERN);
        }

        // check password pattern
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()-_=+\\\\|{};:'\",<.>/?])[A-Za-z\\d!@#$%^&*()-_=+\\\\|{};:'\",<.>/?]{8,16}$";
        Matcher passwordMatcher = Pattern.compile(passwordPattern)
                .matcher(createRequest.getPassword());

        if (!passwordMatcher.matches()) {
            throw new GotbetterException(MessageType.BAD_PASSWORD_PATTERN);
        }

        // check nickname pattern
        if (createRequest.getNickname().length() < 4 || createRequest.getNickname().length() > 12) {
            throw new GotbetterException(MessageType.BAD_NICKNAME_PATTERN);
        }

        // password encode before store
        String encodedPassword = passwordEncoder.encode(createRequest.getPassword());

        User createdUser = userService.createUser(
                User.builder()
                        .userId(createRequest.getUserId())
                        .password(encodedPassword)
                        .nickname(createRequest.getNickname())
                        .build()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponseView<>(new UserView(createdUser)));

    }

    @GetMapping("/duplicate")
    public ResponseEntity<ApiResponseView<UserView>> checkDuplicate(@RequestParam @Nullable String userId, @RequestParam @Nullable String nickname) {

        if (userId != null && nickname != null) {
            throw new GotbetterException(MessageType.BAD_REQUEST);
        }

        boolean isDuplicate;
        User result = null;
        // check is userId duplicate
        if (userId != null) {

            if(userId.isBlank()) {
                throw new GotbetterException(MessageType.BAD_USER_ID_PATTERN);
            }

            isDuplicate = userService.checkUserIdDuplicate(userId);
            if (isDuplicate) {
                throw new GotbetterException(MessageType.DUPLICATED_USER_ID);
            }

            result = User.builder()
                    .userId(userId)
                    .build();
        }

        // check is nickname duplicate
        if (nickname != null) {

            if(nickname.isBlank()) {
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
}
