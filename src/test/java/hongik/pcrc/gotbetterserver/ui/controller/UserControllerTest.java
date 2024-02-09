package hongik.pcrc.gotbetterserver.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.application.service.user.UserService;
import hongik.pcrc.gotbetterserver.exception.MessageType;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;

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
                .userId("testUserA")
                .password("qwer1234!")
                .nickname("테스트유저1")
                .build();
        DummyUserCreateRequest userIdWithKorean = DummyUserCreateRequest.builder()
                .userId("안녕하세요")
                .password("qwer1234!")
                .nickname("테스트유저2")
                .build();
        DummyUserCreateRequest userIdShort = DummyUserCreateRequest.builder()
                .userId("hel")
                .password("qwer1234!")
                .nickname("테스트유저3")
                .build();
        DummyUserCreateRequest ok = DummyUserCreateRequest.builder()
                .userId("helloworld")
                .password("Qwer1234!")
                .nickname("테스트유저4")
                .build();
        // when

        // then
        mvc.perform(post(BASE_URI + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userIdWithCapital))
                )
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    assertThat(result.getResponse()
                            .getContentAsString(StandardCharsets.UTF_8))
                            .contains(MessageType.BAD_USER_ID_PATTERN.name());
                });


        mvc.perform(post(BASE_URI + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userIdShort))
                )
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    assertThat(result.getResponse()
                            .getContentAsString(StandardCharsets.UTF_8))
                            .contains(MessageType.BAD_USER_ID_PATTERN.name());
                });

        mvc.perform(post(BASE_URI + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userIdWithKorean))
                )
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    assertThat(result.getResponse()
                            .getContentAsString(StandardCharsets.UTF_8))
                            .contains(MessageType.BAD_USER_ID_PATTERN.name());
                });


        mvc.perform(post(BASE_URI + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(ok))
                )
                .andExpect(status().isCreated())
                .andDo((result) -> {
                    DummyUserCreateRequest nicknameConflict = DummyUserCreateRequest.builder()
                            .userId("helloworld2")
                            .password("Qwer1234!")
                            .nickname("테스트유저4")
                            .build();
                    mvc.perform(post(BASE_URI + "/users")
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
                .userId("helloworld")
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



    @Getter
    @Builder
    @ToString
    static class DummyUserCreateRequest {
        private String userId;
        private String password;
        private String nickname;
    }
}