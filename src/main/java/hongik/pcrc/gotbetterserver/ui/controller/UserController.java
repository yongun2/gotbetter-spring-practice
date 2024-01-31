package hongik.pcrc.gotbetterserver.ui.controller;

import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.application.service.user.UserService;
import hongik.pcrc.gotbetterserver.exception.GotbetterException;
import hongik.pcrc.gotbetterserver.exception.MessageType;
import hongik.pcrc.gotbetterserver.ui.requestBody.user.UserCreateRequest;
import hongik.pcrc.gotbetterserver.ui.view.ApiResponseView;
import hongik.pcrc.gotbetterserver.ui.view.user.UserView;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;


    @PostMapping("/users")
    public ResponseEntity<ApiResponseView<UserView>> signup(@RequestBody @Validated UserCreateRequest createRequest) {

        // password encode
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

    @GetMapping("/users")
    public ResponseEntity<ApiResponseView<UserView>> checkDuplicate(@RequestParam String userId) {
        boolean isDuplicate = userService.checkUserIdDuplicate(userId);

        if (isDuplicate) {
            throw new GotbetterException(MessageType.DUPLICATED_ID);
        }

        User passUserId = User.builder().userId(userId).build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponseView<>(new UserView(passUserId)));
    }
}
