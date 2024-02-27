package hongik.pcrc.gotbetterserver.application.domain.studyroom;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class StudyRoom {
    private final int id;
    private final String name;
    private final String description;
    private final int entryFee;
    private final String entryCode;
    private final int maxUserNum;
    private final String collectionAccount;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
}
