package hongik.pcrc.gotbetterserver.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hongik.pcrc.gotbetterserver.exception.MessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    public static final String BASE_URL = "/api/v1/users";

    @Test
    @Transactional
    @DisplayName("회원가입 요청 테스트")
    void signup() throws Exception {
        // given
        UserCreateRequest create_201 = UserCreateRequest.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserA")
                .build();
        UserCreateRequest create_400_username_blank = UserCreateRequest.builder()
                .username("")
                .password("qwer1234!")
                .nickname("testUserA")
                .build();
        UserCreateRequest create_400_username_invalid_short = UserCreateRequest.builder()
                .username("d")
                .password("qwer1234!")
                .nickname("testUserA")
                .build();
        UserCreateRequest create_400_username_invalid_long = UserCreateRequest.builder()
                .username("ddfdsfdsfjdslkfjdslkfjlkdsjflkdsjfldsjfldsjflsdj")
                .password("qwer1234!")
                .nickname("testUserA")
                .build();
        UserCreateRequest create_400_username_invalid_character = UserCreateRequest.builder()
                .username("asD123^")
                .password("qwer1234!")
                .nickname("testUserA")
                .build();


        UserCreateRequest create_400_password_blank = UserCreateRequest.builder()
                .username("fdjfkd")
                .password("")
                .nickname("testUserA")
                .build();
        UserCreateRequest create_400_password_invalid_short = UserCreateRequest.builder()
                .username("fdjfkd")
                .password("dfd")
                .nickname("testUserA")
                .build();
        UserCreateRequest create_400_password_invalid_long = UserCreateRequest.builder()
                .username("fdjfkd")
                .password("dfddfddfddfddfddfddfddfddfddfddfddfddfddfddfddfddfddfddfddfd")
                .nickname("testUserA")
                .build();
        UserCreateRequest create_400_password_invalid_character = UserCreateRequest.builder()
                .username("fdjfkd")
                .password("dfdㄴㅇㄹ")
                .nickname("testUserA")
                .build();

        UserCreateRequest create_400_nickname_blank = UserCreateRequest.builder()
                .username("jccmdsd")
                .password("qwer1234!")
                .nickname("")
                .build();

        UserCreateRequest create_400_nickname_invalid_short = UserCreateRequest.builder()
                .username("jccmdsd")
                .password("qwer1234!")
                .nickname("a")
                .build();

        UserCreateRequest create_400_nickname_invalid_long = UserCreateRequest.builder()
                .username("jccmdsd")
                .password("qwer1234!")
                .nickname("fdskfjdslfjdslkfjdslkfdskfjdslfjdslkfjdslk")
                .build();

        UserCreateRequest create_400_not_include_required = UserCreateRequest.builder()
                .password("qwer1234!")
                .nickname("")
                .build();
        // when
        ResultActions perform_201 = mvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(create_201))
        );

        /**
         * username invalid test
         */
        ResultActions perform_400_username_blank = mvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(create_400_username_blank)));
        ResultActions perform_400_username_invalid_short = mvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(create_400_username_invalid_short)));
        ResultActions perform_400_username_invalid_long = mvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(create_400_username_invalid_long)));
        ResultActions perform_400_username_invalid_character = mvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(create_400_username_invalid_character)));

        /**
         * password invalid test
         */
        ResultActions perform_400_password_blank = mvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(create_400_password_blank)));
        ResultActions perform_400_password_invalid_short = mvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(create_400_password_invalid_short)));
        ResultActions perform_400_password_invalid_long = mvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(create_400_password_invalid_long)));
        ResultActions perform_400_password_invalid_character = mvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(create_400_password_invalid_character)));

        /**
         * nickname invalid test
         */
        ResultActions perform_400_nickname_blank = mvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(create_400_nickname_blank)));
        ResultActions perform_400_nickname_invalid_short = mvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(create_400_nickname_invalid_short)));
        ResultActions perform_400_nickname_invalid_long = mvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(create_400_nickname_invalid_long)));

        ResultActions perform_400_required = mvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(create_400_not_include_required)));


        // then
        perform_201.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id", is(notNullValue())))
                .andExpect(jsonPath("$.data.username", is(create_201.getUsername())))
                .andExpect(jsonPath("$.data.nickname", is(create_201.getNickname())));

        perform_400_username_blank.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_REQUEST.getMessage())));
        perform_400_username_invalid_short.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_USER_ID_PATTERN.getMessage())));
        perform_400_username_invalid_long.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_USER_ID_PATTERN.getMessage())));
        perform_400_username_invalid_character.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_USER_ID_PATTERN.getMessage())));

        perform_400_password_blank.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_REQUEST.getMessage())));
        perform_400_password_invalid_short.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_PASSWORD_PATTERN.getMessage())));
        perform_400_password_invalid_long.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_PASSWORD_PATTERN.getMessage())));
        perform_400_password_invalid_character.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_PASSWORD_PATTERN.getMessage())));

        perform_400_nickname_blank.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_REQUEST.getMessage())));
        perform_400_nickname_invalid_short.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_NICKNAME_PATTERN.getMessage())));
        perform_400_nickname_invalid_long.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_NICKNAME_PATTERN.getMessage())));


        perform_400_required.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_REQUEST.getMessage())));

    }

    @Test
    @Transactional
    @DisplayName("로그인 요청 테스트")
    void login() throws Exception {
        // given
        final String DUMMY_USERNAME = "asd1234";
        final String DUMMY_PASSWORD = "qwer1234!";
        final String DUMMY_NICKNAME = "testUserA";

        UserCreateRequest create_201 = UserCreateRequest.builder()
                .username(DUMMY_USERNAME)
                .password(DUMMY_PASSWORD)
                .nickname(DUMMY_NICKNAME)
                .build();

        ResultActions perform_201 = mvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(create_201))
        );

        LoginRequest login_200 = LoginRequest.builder()
                .username(DUMMY_USERNAME)
                .password(DUMMY_PASSWORD)
                .build();

        LoginRequest login_400_username = LoginRequest.builder()
                .username("")
                .password(DUMMY_PASSWORD)
                .build();

        LoginRequest login_400_password = LoginRequest.builder()
                .username(DUMMY_USERNAME)
                .password("")
                .build();

        LoginRequest login_404_invalid_username = LoginRequest.builder()
                .username("fd")
                .password(DUMMY_PASSWORD)
                .build();

        LoginRequest login_404_invalid_password = LoginRequest.builder()
                .username(DUMMY_USERNAME)
                .password("fd")
                .build();
        // when
        ResultActions perform_200 = mvc.perform(post(BASE_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login_200))
        );

        ResultActions perform_400_username = mvc.perform(post(BASE_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login_400_username))
        );

        ResultActions perform_400_password = mvc.perform(post(BASE_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login_400_password)));

        ResultActions perform_404_invalid_username = mvc.perform(post(BASE_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login_404_invalid_username)));

        ResultActions perform_404_invalid_password = mvc.perform(post(BASE_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login_404_invalid_password)));

        // then
        perform_200.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken", is(notNullValue())))
                .andExpect(jsonPath("$.data.refreshToken", is(notNullValue())));

        perform_400_username.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_REQUEST.getMessage())));

        perform_400_password.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_REQUEST.getMessage())));

        perform_404_invalid_username.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.USER_NOT_FOUND.getMessage())));

        perform_404_invalid_password.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.USER_NOT_FOUND.getMessage())));


    }

    @Test
    @Transactional
    @DisplayName("아이디 중복확인 요청 테스트")
    void checkDuplicate() throws Exception {
        // given
        final String DUMMY_USERNAME = "asd1234";
        final String DUMMY_PASSWORD = "qwer1234!";
        final String DUMMY_NICKNAME = "testUserA";

        UserCreateRequest create_201 = UserCreateRequest.builder()
                .username(DUMMY_USERNAME)
                .password(DUMMY_PASSWORD)
                .nickname(DUMMY_NICKNAME)
                .build();

        ResultActions perform_201 = mvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(create_201))
        );
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "fd");
        params.add("nickname", "fdj");

        final String SUCCESS_USERNAME = "helloworld";
        final String SUCCESS_NICKNAME = "testUserH";

        // when
        ResultActions perform_200_username = mvc.perform(get(BASE_URL + "/duplicate")
                .param("username", SUCCESS_USERNAME));
        ResultActions perform_200_nickname = mvc.perform(get(BASE_URL + "/duplicate")
                .param("nickname", SUCCESS_NICKNAME));

        ResultActions perform_409_username = mvc.perform(get(BASE_URL + "/duplicate")
                .param("username", DUMMY_USERNAME)
        );
        ResultActions perform_409_nickname = mvc.perform(get(BASE_URL + "/duplicate")
                .param("nickname", DUMMY_NICKNAME)
        );
        ResultActions perform_400 = mvc.perform(get(BASE_URL + "/duplicate")
                .param("hello", DUMMY_NICKNAME)
        );
        ResultActions perform_400_username_nickname_params = mvc.perform(get(BASE_URL + "/duplicate")
                .params(params)
        );

        // then
        perform_200_username.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username", is(SUCCESS_USERNAME)));
        perform_200_nickname.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname", is(SUCCESS_NICKNAME)));

        perform_409_username.andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.DUPLICATED_USER_ID.getMessage())));
        perform_409_nickname.andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.DUPLICATE_NICKNAME.getMessage())));
        perform_400.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_REQUEST.getMessage())));
        perform_400_username_nickname_params.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_REQUEST.getMessage())));
    }


    @Getter
    @Builder
    static class UserCreateRequest {
        private String username;
        private String password;
        private String nickname;
    }

    @Getter
    @Builder
    static class LoginRequest {
        private String username;
        private String password;
    }
}