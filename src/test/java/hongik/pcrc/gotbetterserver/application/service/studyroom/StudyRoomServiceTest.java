package hongik.pcrc.gotbetterserver.application.service.studyroom;

import hongik.pcrc.gotbetterserver.application.domain.studyroom.Role;
import hongik.pcrc.gotbetterserver.application.utils.RandomStringGenerator;
import hongik.pcrc.gotbetterserver.exception.GotbetterException;
import hongik.pcrc.gotbetterserver.exception.MessageType;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.category.StudyRoomCategoryEntity;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.studyroom.ParticipantEntity;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.studyroom.StudyRoomEntity;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.studyroom.ParticipantRepository;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.studyroom.StudyRoomCategoryRepository;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.studyroom.StudyRoomRepository;
import hongik.pcrc.gotbetterserver.utils.WithCustomMockUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static hongik.pcrc.gotbetterserver.application.service.studyroom.StudyRoomOperationUseCase.*;
import static hongik.pcrc.gotbetterserver.application.service.studyroom.StudyRoomReadUseCase.*;
import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class StudyRoomServiceTest {

    @Autowired
    private StudyRoomService service;
    @Autowired
    StudyRoomRepository studyRoomRepository;
    @Autowired
    StudyRoomCategoryRepository studyRoomCategoryRepository;
    @Autowired
    ParticipantRepository participantRepository;

    public static final int STUDY_CATEGORY_ID = 1;
    public static final int EXERCISE_CATEGORY_ID = 2;
    public static final int DEVELOP_CATEGORY_ID = 3;
    public static final String DEFAULT_TEST_USER_NICKNAME = "testUserA";

    @AfterEach
    void deleteStudyRoom() {
        studyRoomRepository.deleteAll();
    }

    @Test
    @Transactional
    @WithCustomMockUser
    @DisplayName("스터디룸 생성 서비스 테스트")
    void createStudyRoom() {
        // given
        StudyRoomCreateCommand command = StudyRoomCreateCommand.builder()
                .name("😎알고리즘 스터디룸")
                .description("JAVA 알고리즘 스터디룸 입니다.")
                .entryFee(10000)
                .maxUserNum(4)
                .collectionAccount("국민 112-323-123392")
                .startDateTime(LocalDateTime.of(2024, 2, 12, 1, 2))
                .endDateTime(LocalDateTime.of(2024, 2, 15, 1, 2))
                .categoryId(STUDY_CATEGORY_ID)
                .build();
        // when
        FindStudyRoomResult result = service.createStudyRoom(command);
        Optional<StudyRoomCategoryEntity> studyRoomCategoryEntityOptional = studyRoomCategoryRepository.findStudyRoomCategoryEntityByStudyRoomEntity_Id(result.id());
        Optional<ParticipantEntity> participantEntityOptional = participantRepository.findParticipantEntityByStudyRoomEntity_Name(command.name());

        // then
        assertThat(studyRoomCategoryEntityOptional.isPresent()).isTrue();
        assertThat(studyRoomCategoryEntityOptional.get().getCategoryEntity().getId()).isEqualTo(STUDY_CATEGORY_ID);
        assertThat(participantEntityOptional.isPresent()).isTrue();
        assertThat(participantEntityOptional.get().getUserEntity().getNickname()).isEqualTo(DEFAULT_TEST_USER_NICKNAME);
        assertThat(participantEntityOptional.get().getRole()).isEqualTo(Role.MANAGER);

    }

    @Test
    @WithCustomMockUser
    @DisplayName("스터디룸 생성 서비스 실패 테스트")
    void createStudyRoomFailed() {
        // given
        StudyRoomCreateCommand defaultCommand = StudyRoomCreateCommand.builder()
                .name("😎알고리즘 스터디룸 1")
                .description("JAVA 알고리즘 스터디룸 입니다.")
                .entryFee(10000)
                .maxUserNum(4)
                .collectionAccount("국민 112-323-123392")
                .startDateTime(LocalDateTime.of(2024, 2, 12, 1, 2))
                .endDateTime(LocalDateTime.of(2024, 2, 15, 1, 2))
                .categoryId(STUDY_CATEGORY_ID)
                .build();

        StudyRoomCreateCommand fail_command_no_category = StudyRoomCreateCommand.builder()
                .name("😎알고리즘 스터디룸 2")
                .description("JAVA 알고리즘 스터디룸 입니다.")
                .entryFee(10000)
                .maxUserNum(4)
                .collectionAccount("국민 112-323-123392")
                .startDateTime(LocalDateTime.of(2024, 2, 12, 1, 2))
                .endDateTime(LocalDateTime.of(2024, 2, 15, 1, 2))
                .build();


        StudyRoomCreateCommand fail_command_invalid_duration = StudyRoomCreateCommand.builder()
                .name("😎알고리즘 스터디룸 4")
                .description("JAVA 알고리즘 스터디룸 입니다.")
                .entryFee(10000)
                .maxUserNum(4)
                .collectionAccount("국민 112-323-123392")
                .startDateTime(LocalDateTime.of(2024, 3, 12, 1, 2))
                .endDateTime(LocalDateTime.of(2024, 2, 15, 1, 2))
                .categoryId(STUDY_CATEGORY_ID)
                .build();

        // when
        service.createStudyRoom(defaultCommand);

        // then
        assertThatThrownBy(() -> service.createStudyRoom(fail_command_no_category))
                .isInstanceOf(GotbetterException.class)
                .hasMessage(MessageType.CATEGORY_NOT_FOUND.getMessage());

        assertThatThrownBy(() -> service.createStudyRoom(fail_command_invalid_duration))
                .isInstanceOf(GotbetterException.class)
                .hasMessage(MessageType.INVALID_STUDY_ROOM_DURATION.getMessage());

        assertThat(studyRoomRepository.count()).isEqualTo(1);
        assertThat(studyRoomCategoryRepository.count()).isEqualTo(1);
        assertThat(participantRepository.count()).isEqualTo(1);
    }

    @Test
    @Transactional
    @WithCustomMockUser
    @DisplayName("스터디룸 조회 테스트")
    void getStudyRoomListByAccepted() {
        // given
        StudyRoomCreateCommand testStudyRoom1 = StudyRoomCreateCommand.builder()
                .name("😎알고리즘 스터디룸 1")
                .description("JAVA 알고리즘 스터디룸 입니다.")
                .entryFee(10000)
                .maxUserNum(4)
                .collectionAccount("국민 112-323-123392")
                .startDateTime(LocalDateTime.of(2024, 2, 12, 1, 2))
                .endDateTime(LocalDateTime.of(2024, 2, 15, 1, 2))
                .categoryId(STUDY_CATEGORY_ID)
                .build();
        StudyRoomCreateCommand testStudyRoom2 = StudyRoomCreateCommand.builder()
                .name("😎파이썬 알고리즘 스터디룸 2")
                .description("Python 알고리즘 스터디룸 입니다.")
                .entryFee(20000)
                .maxUserNum(5)
                .collectionAccount("국민 112-323-123392")
                .startDateTime(LocalDateTime.of(2024, 2, 12, 1, 2))
                .endDateTime(LocalDateTime.of(2024, 2, 28, 1, 2))
                .categoryId(STUDY_CATEGORY_ID)
                .build();
        StudyRoomCreateCommand testStudyRoom3 = StudyRoomCreateCommand.builder()
                .name("😎김영한 Spring Boot 스터디")
                .description("Spring Boot 알차게 공부하자.")
                .entryFee(60000)
                .maxUserNum(2)
                .collectionAccount("국민 112-323-123392")
                .startDateTime(LocalDateTime.of(2024, 2, 12, 1, 2))
                .endDateTime(LocalDateTime.of(2024, 2, 20, 1, 2))
                .categoryId(STUDY_CATEGORY_ID)
                .build();
        StudyRoomCreateCommand testStudyRoom4 = StudyRoomCreateCommand.builder()
                .name("😎오늘도 건강하셨어?")
                .description("매주 운동해서 건강해지는 그날까지")
                .entryFee(60000)
                .maxUserNum(6)
                .collectionAccount("국민 112-323-123392")
                .startDateTime(LocalDateTime.of(2024, 2, 12, 1, 2))
                .endDateTime(LocalDateTime.of(2024, 12, 20, 1, 2))
                .categoryId(EXERCISE_CATEGORY_ID)
                .build();
        // when
        service.createStudyRoom(testStudyRoom1);
        service.createStudyRoom(testStudyRoom2);
        service.createStudyRoom(testStudyRoom3);
        service.createStudyRoom(testStudyRoom4);

        log.info("===================== 생성 끝 =====================");

        List<FindStudyRoomResult> acceptedStudyRoomList = service.getStudyRoomListByAccepted(true);
        for (FindStudyRoomResult findStudyRoomResult : acceptedStudyRoomList) {
            log.info("{}", findStudyRoomResult.toString());
        }
        List<FindStudyRoomResult> notAcceptedStudyRoomList = service.getStudyRoomListByAccepted(false);
        // then
        assertThat(acceptedStudyRoomList.size()).isEqualTo(4);
        assertThat(notAcceptedStudyRoomList.size()).isEqualTo(0);
    }

}