package hongik.pcrc.gotbetterserver.application.domain.studyroom;

import hongik.pcrc.gotbetterserver.application.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Participant {
    private final int id;
    private final boolean accepted;
    private final Role role;

    private final User user;
    private final StudyRoom studyRoom;
}
