package hongik.pcrc.gotbetterserver.ui.view.studyroom;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import hongik.pcrc.gotbetterserver.application.domain.studyroom.Category;
import hongik.pcrc.gotbetterserver.application.service.studyroom.StudyRoomReadUseCase.FindStudyRoomResult;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StudyRoomView(
        int id,
        String name,
        String description,
        int entryFee,
        String entryCode,
        int maxUserNum,
        String collectionAccount,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDateTime startDateTime,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDateTime endDateTime,
        Category category
) {
    public StudyRoomView(FindStudyRoomResult result) {
        this(
                result.id(),
                result.name(),
                result.description(),
                result.entryFee(),
                result.entryCode(),
                result.maxUserNum(),
                result.collectionAccount(),
                result.startDateTime(),
                result.endDateTime(),
                result.category()
        );
    }
}
