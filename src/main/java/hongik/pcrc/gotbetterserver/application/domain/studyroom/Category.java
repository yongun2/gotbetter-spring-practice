package hongik.pcrc.gotbetterserver.application.domain.studyroom;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Category {
    private final int id;
    private final String title;
    private final String categoryImgUrl;
}
