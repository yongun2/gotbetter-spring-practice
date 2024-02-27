package hongik.pcrc.gotbetterserver.application.domain.studyroom;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class StudyRoomCategory {
    private final StudyRoom studyRoom;
    private final Category category;
}
