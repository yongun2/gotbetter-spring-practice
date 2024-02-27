package hongik.pcrc.gotbetterserver.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hongik.pcrc.gotbetterserver.application.category.CategoryId;
import hongik.pcrc.gotbetterserver.application.config.SecurityConstants;
import hongik.pcrc.gotbetterserver.application.domain.auth.JWTAuthenticationToken;
import hongik.pcrc.gotbetterserver.application.domain.auth.JWTToken;
import hongik.pcrc.gotbetterserver.application.service.auth.JWTTokenProvider;
import hongik.pcrc.gotbetterserver.exception.MessageType;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class StudyRoomControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    JWTTokenProvider jwtTokenProvider;

    public static final String BASE_URL = "/api/v1/study-rooms";

    @Test
    @Transactional
    @DisplayName("스터디룸 생성 요청 테스트")
    void create() throws Exception {
        // given
        StudyRoomCreateRequest command_201 = StudyRoomCreateRequest.builder()
                .name("😎김영한 Spring Boot 스터디")
                .description("Spring Boot 알차게 공부하자.")
                .entryFee(60000)
                .maxUserNum(2)
                .collectionAccount("국민 112-323-123392")
                .startDateTime(LocalDateTime.of(2024, 2, 12, 1, 2))
                .endDateTime(LocalDateTime.of(2024, 2, 20, 1, 2))
                .categoryId(CategoryId.STUDY.getValue())
                .build();

        StudyRoomCreateRequest command_400_categoryId = StudyRoomCreateRequest.builder()
                .name("😎김영한 Spring Boot 스터디")
                .description("Spring Boot 알차게 공부하자.")
                .entryFee(60000)
                .maxUserNum(2)
                .collectionAccount("국민 112-323-123392")
                .startDateTime(LocalDateTime.of(2024, 2, 12, 1, 2))
                .endDateTime(LocalDateTime.of(2024, 2, 20, 1, 2))
                .build();

        StudyRoomCreateRequest command_404_categoryId = StudyRoomCreateRequest.builder()
                .name("😎김영한 Spring Boot 스터디")
                .description("Spring Boot 알차게 공부하자.")
                .entryFee(60000)
                .maxUserNum(2)
                .categoryId(Integer.MAX_VALUE)
                .collectionAccount("국민 112-323-123392")
                .startDateTime(LocalDateTime.of(2024, 2, 12, 1, 2))
                .endDateTime(LocalDateTime.of(2024, 2, 20, 1, 2))
                .build();


        JWTToken jwtToken = jwtTokenProvider.generateToken(new JWTAuthenticationToken("testUserA", null));
        // when
        ResultActions perform_201 = mvc.perform(post(BASE_URL)
                .header(SecurityConstants.JWT_HEADER, jwtToken.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(command_201))
        );
        ResultActions perform_400_categoryId = mvc.perform(post(BASE_URL)
                .header(SecurityConstants.JWT_HEADER, jwtToken.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(command_400_categoryId))
        );
        ResultActions perform_404_categoryId = mvc.perform(post(BASE_URL)
                .header(SecurityConstants.JWT_HEADER, jwtToken.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(command_404_categoryId))
        );

        // then
        MvcResult result = perform_201.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name", is(command_201.name())))
                .andExpect(jsonPath("$.data.description", is(command_201.description())))
                .andExpect(jsonPath("$.data.entryFee", is(command_201.entryFee())))
                .andExpect(jsonPath("$.data.maxUserNum", is(command_201.maxUserNum())))
                .andExpect(jsonPath("$.data.collectionAccount", is(command_201.collectionAccount())))
                .andExpect(jsonPath("$.data.startDateTime", is(command_201.startDateTime().format(DateTimeFormatter.ISO_DATE))))
                .andExpect(jsonPath("$.data.endDateTime", is(command_201.endDateTime().format(DateTimeFormatter.ISO_DATE))))
                .andReturn();


        perform_400_categoryId.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_REQUEST.getMessage())));

        perform_404_categoryId.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.CATEGORY_NOT_FOUND.getMessage())));

        log.info(result.getResponse().getContentAsString());
    }

    @Test
    void getStudyRooms() throws Exception {
        // given
        JWTToken jwtToken = jwtTokenProvider.generateToken(new JWTAuthenticationToken("testUserA", null));
        // when
        ResultActions perform_accepted = mvc.perform(get(BASE_URL)
                .param("accepted", "true")
                .header(SecurityConstants.JWT_HEADER, jwtToken.getAccessToken())
        );
        ResultActions perform_not_accepted = mvc.perform(get(BASE_URL)
                .param("accepted", "false")
                .header(SecurityConstants.JWT_HEADER, jwtToken.getAccessToken())
        );
        // then
        MvcResult result = perform_accepted.andExpect(status().isOk())
                .andReturn();
        perform_not_accepted.andExpect(status().isOk());

    }

    @Builder
    record StudyRoomCreateRequest(
            String name,
            String description,
            Integer entryFee,
            String entryCode,
            Integer maxUserNum,
            String collectionAccount,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Integer categoryId) {
    }
}