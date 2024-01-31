package hongik.pcrc.gotbetterserver.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.application.service.user.UserService;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    @DisplayName("회원가입 테스트")
    void signup() throws Exception {
        // given
        int userId = 1;
        String password = "1234";
        String nickname = "userA";
        // when
        String requestBody = mapper.writeValueAsString(
                DummyUserCreateRequest.builder()
                        .userId(userId)
                        .nickname(nickname)
                        .build()
        );

        // then
        mvc.perform(post(BASE_URI + "/users")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

    }

    @Test
    @Transactional
    @DisplayName("아이디 중복확인 테스트")
    void checkDuplicate() throws Exception {
        // given
        User userA = User.builder()
                .userId("hello")
                .password("1234")
                .nickname("userA")
                .build();
        // when
        userService.createUser(userA);
        // then
        mvc.perform(get(BASE_URI + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "hello")
                )
                .andExpect(status().isConflict());

        mvc.perform(get(BASE_URI + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "world"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    log.info("result {}", response.getContentAsString());
                });

        mvc.perform(get(BASE_URI + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("hello", "world")
                )
                .andExpect(status().isBadRequest());

    }

    @Getter
    @Builder
    static class DummyUserCreateRequest {
        private int userId;
        private String nickname;
    }
}