package hongik.pcrc.gotbetterserver.ui.requestBody.studyroom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record StudyRoomCreateRequest (
    @NotBlank
    String name,
    @NotBlank
    String description,
    @NotNull
    Integer entryFee,
    @NotNull
    Integer maxUserNum,
    @NotBlank
    String collectionAccount,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime,
    @NotNull
    Integer categoryId
) {

}
