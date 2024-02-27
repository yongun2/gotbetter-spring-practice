package hongik.pcrc.gotbetterserver.application.service.studyroom;

import hongik.pcrc.gotbetterserver.application.domain.studyroom.Category;
import hongik.pcrc.gotbetterserver.application.domain.studyroom.StudyRoom;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public interface StudyRoomReadUseCase {

    List<FindStudyRoomResult> getStudyRoomListByAccepted(boolean accepted);

    record StudyRoomFindQuery(int studyRoomId) {
    }

    @Builder
    record FindStudyRoomResult(
            int id,
            String name,
            String description,
            int entryFee,
            String entryCode,
            int maxUserNum,
            String collectionAccount,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Category category
    ) {
        public static FindStudyRoomResult findByStudyRoom(StudyRoom studyRoom) {
            return FindStudyRoomResult.builder()
                    .id(studyRoom.getId())
                    .name(studyRoom.getName())
                    .description(studyRoom.getDescription())
                    .entryFee(studyRoom.getEntryFee())
                    .entryCode(studyRoom.getEntryCode())
                    .maxUserNum(studyRoom.getMaxUserNum())
                    .collectionAccount(studyRoom.getCollectionAccount())
                    .startDateTime(studyRoom.getStartDateTime())
                    .endDateTime(studyRoom.getEndDateTime())
                    .build();
        }

        public static FindStudyRoomResult findByStudyRoomCategory(StudyRoom studyRoom, Category category) {
            return FindStudyRoomResult.builder()
                    .id(studyRoom.getId())
                    .name(studyRoom.getName())
                    .description(studyRoom.getDescription())
                    .entryFee(studyRoom.getEntryFee())
                    .entryCode(studyRoom.getEntryCode())
                    .maxUserNum(studyRoom.getMaxUserNum())
                    .collectionAccount(studyRoom.getCollectionAccount())
                    .startDateTime(studyRoom.getStartDateTime())
                    .endDateTime(studyRoom.getEndDateTime())
                    .category(category)
                    .build();
        }
    }
}
