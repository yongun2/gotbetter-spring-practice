package hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.studyroom;

import hongik.pcrc.gotbetterserver.application.domain.studyroom.StudyRoom;
import hongik.pcrc.gotbetterserver.application.domain.studyroom.StudyRoomCategory;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.category.CategoryEntity;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.category.StudyRoomCategoryEntity;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.studyroom.StudyRoomEntity;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
class StudyRoomRepositoryTest {

    @Autowired
    private StudyRoomRepository studyRoomRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StudyRoomCategoryRepository studyRoomCategoryRepository;

    public static final int DEFAULT_RULE_ID = 1;

    public static final int STUDY_CATEGORY_ID = 1;
    public static final int EXERCISE_CATEGORY_ID = 2;

    @AfterEach
    void deleteStudyRoom() {
        studyRoomRepository.deleteAll();
    }

    @Test
    @DisplayName("스터디룸 생성")
    void createStudyRoom() {
       // given
        StudyRoom studyRoom = StudyRoom.builder()
                .name("😎알고리즘 스터디룸")
                .description("JAVA 알고리즘 스터디룸 입니다.")
                .entryFee(10000)
                .entryCode("testEntryCodeA")
                .maxUserNum(4)
                .collectionAccount("국민 112-323-123392")
                .startDateTime(LocalDateTime.of(2024, 2, 12, 1, 2))
                .endDateTime(LocalDateTime.of(2024, 2, 15, 1, 2))
                .build();

        // when
        StudyRoomEntity result = studyRoomRepository.save(new StudyRoomEntity(studyRoom));

        // then
        assertThat(result.getName()).isEqualTo(studyRoom.getName());
    }

    @Test
    @DisplayName("스터디룸 생성 실패")
    void createStudyRoomFail() {
        // given
        StudyRoom defaultStudyRoom = StudyRoom.builder()
                .name("😎알고리즘 스터디룸")
                .description("JAVA 알고리즘 스터디룸 입니다.")
                .entryFee(10000)
                .entryCode("testEntryCodeA")
                .maxUserNum(4)
                .collectionAccount("국민 112-323-123392")
                .startDateTime(LocalDateTime.of(2024, 2, 12, 1, 2))
                .endDateTime(LocalDateTime.of(2024, 2, 15, 1, 2))
                .build();

        StudyRoom fail_studyRoom_duplicated_entryCode = StudyRoom.builder()
                .name("😎알고리즘 스터디룸")
                .description("JAVA 알고리즘 스터디룸 입니다.")
                .entryFee(10000)
                .entryCode("testEntryCodeA")
                .maxUserNum(4)
                .collectionAccount("국민 112-323-123392")
                .startDateTime(LocalDateTime.of(2024, 2, 12, 1, 2))
                .endDateTime(LocalDateTime.of(2024, 2, 15, 1, 2))
                .build();

        // when
        StudyRoomEntity result = studyRoomRepository.save(new StudyRoomEntity(defaultStudyRoom));

        // then
        assertThatThrownBy(() -> studyRoomRepository.save(new StudyRoomEntity(fail_studyRoom_duplicated_entryCode)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}