package hongik.pcrc.gotbetterserver.ui.controller;

import hongik.pcrc.gotbetterserver.application.service.studyroom.StudyRoomOperationUseCase;
import hongik.pcrc.gotbetterserver.application.service.studyroom.StudyRoomReadUseCase;
import hongik.pcrc.gotbetterserver.ui.requestBody.studyroom.StudyRoomCreateRequest;
import hongik.pcrc.gotbetterserver.ui.view.ApiResponseView;
import hongik.pcrc.gotbetterserver.ui.view.studyroom.StudyRoomView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static hongik.pcrc.gotbetterserver.application.service.studyroom.StudyRoomOperationUseCase.StudyRoomCreateCommand;

@Slf4j
@RestController
@RequestMapping("/api/v1/study-rooms")
@RequiredArgsConstructor
public class StudyRoomController {

    private final StudyRoomOperationUseCase operationUseCase;
    private final StudyRoomReadUseCase readUseCase;

    @PostMapping("")
    ResponseEntity<ApiResponseView<StudyRoomView>> create(@RequestBody @Validated StudyRoomCreateRequest request) {
        log.info(request.toString());

        StudyRoomCreateCommand command = StudyRoomCreateCommand.builder()
                .name(request.name())
                .description(request.description())
                .entryFee(request.entryFee())
                .maxUserNum(request.maxUserNum())
                .collectionAccount(request.collectionAccount())
                .startDateTime(request.startDateTime())
                .endDateTime(request.endDateTime())
                .categoryId(request.categoryId())
                .build();

        StudyRoomReadUseCase.FindStudyRoomResult result = operationUseCase.createStudyRoom(command);
        log.info("result = {}", result);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + ";charset=" + StandardCharsets.UTF_8)
                .body(new ApiResponseView<>(new StudyRoomView(result)));
    }

    @GetMapping("")
    ResponseEntity<ApiResponseView<List<StudyRoomView>>> getStudyRooms(@RequestParam boolean accepted) {
        List<StudyRoomReadUseCase.FindStudyRoomResult> results = readUseCase.getStudyRoomListByAccepted(accepted);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponseView<>(results.stream().map(StudyRoomView::new).collect(Collectors.toList())));
    }

}
