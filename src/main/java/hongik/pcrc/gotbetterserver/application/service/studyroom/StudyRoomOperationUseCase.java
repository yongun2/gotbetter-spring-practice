package hongik.pcrc.gotbetterserver.application.service.studyroom;

import hongik.pcrc.gotbetterserver.application.service.studyroom.StudyRoomReadUseCase.FindStudyRoomResult;
import lombok.Builder;

import java.time.LocalDateTime;

public interface StudyRoomOperationUseCase {

    FindStudyRoomResult createStudyRoom(StudyRoomCreateCommand roomCreateCommand);

    @Builder
    record StudyRoomCreateCommand(
            String name,
            String description,
            int entryFee,
            int maxUserNum,
            String collectionAccount,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            int categoryId
    ) {

    }
}
