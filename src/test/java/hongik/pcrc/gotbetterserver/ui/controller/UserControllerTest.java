package hongik.pcrc.gotbetterserver.ui.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.application.service.auth.JWTTokenProvider;
import hongik.pcrc.gotbetterserver.application.service.user.UserService;
import hongik.pcrc.gotbetterserver.exception.MessageType;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;

import static hongik.pcrc.gotbetterserver.application.service.user.UserReadUseCase.LoginRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private UserService userService;

    public static final String BASE_URI = "/api/v1";

    @Test
    @Transactional
    @DisplayName("회원가입 테스트")
    void signup() throws Exception {
        // given

        DummyUserCreateRequest userIdWithCapital = DummyUserCreateRequest.builder()
                .username("testUserA")
                .password("qwer1234!")
                .nickname("테스트유저1")
                .build();
        DummyUserCreateRequest userIdWithKorean = DummyUserCreateRequest.builder()
                .username("안녕하세요")
                .password("qwer1234!")
                .nickname("테스트유저2")
                .build();
        DummyUserCreateRequest userIdShort = DummyUserCreateRequest.builder()
                .username("hel")
                .password("qwer1234!")
                .nickname("테스트유저3")
                .build();
        DummyUserCreateRequest ok = DummyUserCreateRequest.builder()
                .username("helloworld")
                .password("Qwer1234!")
                .nickname("테스트유저4")
                .build();
        // when

        // then
        mvc.perform(post(BASE_URI + "/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userIdWithCapital))
                )
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    assertThat(result.getResponse()
                            .getContentAsString(StandardCharsets.UTF_8))
                            .contains(MessageType.BAD_USER_ID_PATTERN.name());
                });


        mvc.perform(post(BASE_URI + "/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userIdShort))
                )
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    assertThat(result.getResponse()
                            .getContentAsString(StandardCharsets.UTF_8))
                            .contains(MessageType.BAD_USER_ID_PATTERN.name());
                });

        mvc.perform(post(BASE_URI + "/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userIdWithKorean))
                )
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    assertThat(result.getResponse()
                            .getContentAsString(StandardCharsets.UTF_8))
                            .contains(MessageType.BAD_USER_ID_PATTERN.name());
                });


        mvc.perform(post(BASE_URI + "/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(ok))
                )
                .andExpect(status().isCreated())
                .andDo((result) -> {
                    DummyUserCreateRequest nicknameConflict = DummyUserCreateRequest.builder()
                            .username("helloworld2")
                            .password("Qwer1234!")
                            .nickname("테스트유저4")
                            .build();
                    mvc.perform(post(BASE_URI + "/users/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(mapper.writeValueAsString(nicknameConflict))
                            ).andExpect(status().isConflict())
                            .andDo(result1 -> {
                                assertThat(result1.getResponse()
                                        .getContentAsString(StandardCharsets.UTF_8))
                                        .contains(MessageType.DUPLICATE_NICKNAME.name());
                            });
                });


    }

    @Test
    @Transactional
    @DisplayName("중복확인 테스트")
    void checkDuplicate() throws Exception {
        // given
        User userA = User.builder()
                .username("helloworld")
                .password("qwer1234!")
                .nickname("테스트유저1")
                .build();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userId", "fjdk");
        params.add("nickname", "fjdk");
        // when
        userService.createUser(userA);
        // then

        // 400 Error: included userId and nickname
        mvc.perform(get(BASE_URI + "/users/duplicate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .params(params)
                )
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    assertThat(result.getResponse()
                            .getContentAsString(StandardCharsets.UTF_8))
                            .contains(MessageType.BAD_REQUEST.name());
                });

        // 409 Error: nickname conflict
        mvc.perform(get(BASE_URI + "/users/duplicate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("nickname", "테스트유저1")
                )
                .andExpect(status().isConflict())
                .andDo(result -> {
                    assertThat(result.getResponse()
                            .getContentAsString(StandardCharsets.UTF_8))
                            .contains(MessageType.DUPLICATE_NICKNAME.name());
                });

        // 200 Ok
        mvc.perform(get(BASE_URI + "/users/duplicate")
                        .param("nickname", "테스트유저")
                )
                .andExpect(status().isOk())
                .andDo(result -> {
                    assertThat(result.getResponse()
                            .getContentAsString(StandardCharsets.UTF_8))
                            .contains("테스트유저");
                });

        // 400 Error: nickname with blank
        mvc.perform(get(BASE_URI + "/users/duplicate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("nickname", "")
                )
                .andExpect(status().isBadRequest());

        // 409 Error: userId duplicate
        mvc.perform(get(BASE_URI + "/users/duplicate")
                        .param("userId", "helloworld")
                )
                .andExpect(status().isConflict())
                .andDo(result -> {
                    assertThat(result.getResponse()
                            .getContentAsString(StandardCharsets.UTF_8))
                            .contains(MessageType.DUPLICATED_USER_ID.name());
                });

        // 200 Ok
        mvc.perform(get(BASE_URI + "/users/duplicate")
                        .param("userId", "helloworld2")
                )
                .andExpect(status().isOk())
                .andDo(result -> {
                    assertThat(result.getResponse()
                            .getContentAsString(StandardCharsets.UTF_8))
                            .contains("helloworld2");
                });

        // 400 Error: userId with blank
        mvc.perform(get(BASE_URI + "/users/duplicate")
                        .param("userId", "")
                )
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("로그인 테스트")
    @Transactional
    void login() throws Exception {
        // given
        LoginRequest loginRequest200 = LoginRequest.builder()
                .username("test1")
                .password("Qwer1234@")
                .build();

        LoginRequest loginRequest400 = LoginRequest.builder()
                .username("failed")
                .password("d")
                .build();

        LoginRequest loginRequest400_2 = LoginRequest.builder()
                .username("")
                .password("")
                .build();

        // when
        ResultActions perform = mvc.perform(post(BASE_URI+ "/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginRequest200))
        );

        ResultActions perform400 = mvc.perform(post(BASE_URI + "/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginRequest400))
        );

        ResultActions perform400_2 = mvc.perform(post(BASE_URI + "/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginRequest400_2))
        );
        // then
        perform.andExpect(status().isOk());
        perform400.andExpect(status().isBadRequest());
        perform400_2.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("토큰 검증 필터 테스트")
    @Transactional
    void jwtFilter() throws Exception {
        // given
        DummyUserCreateRequest ok = DummyUserCreateRequest.builder()
                .username("helloworld")
                .password("Qwer1234!")
                .nickname("테스트유저4")
                .build();

        LoginRequest loginRequest200 = LoginRequest.builder()
                .username("helloworld")
                .password("Qwer1234!")
                .build();
        // when
        ResultActions register = mvc.perform(post(BASE_URI + "/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(ok))
        );

        ResultActions login = mvc.perform(post(BASE_URI + "/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginRequest200))
        );

        // then
        register.andExpect(status().isCreated());
        login.andExpect(result -> {
            JsonNode jsonNode = mapper.readTree(result.getResponse().getContentAsString());
            String accessToken = jsonNode.get("data").get("accessToken").asText();
            log.info(accessToken);
            ResultActions perform = mvc.perform(get(BASE_URI + "/users/test")
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
            ).andDo(result1 -> {
                log.info(result1.getResponse().getContentAsString());
            });
        });

    }

    @Getter
    @Builder
    @ToString
    static class DummyUserCreateRequest {
        private String username;
        private String password;
        private String nickname;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    static class JWTToken {
        private String accessToken;
        private String refreshToken;
    }
}