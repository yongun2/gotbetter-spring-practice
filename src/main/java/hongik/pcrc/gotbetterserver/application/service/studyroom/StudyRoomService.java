package hongik.pcrc.gotbetterserver.application.service.studyroom;

import hongik.pcrc.gotbetterserver.application.domain.User;
import hongik.pcrc.gotbetterserver.application.domain.studyroom.*;
import hongik.pcrc.gotbetterserver.application.utils.RandomStringGenerator;
import hongik.pcrc.gotbetterserver.exception.GotbetterException;
import hongik.pcrc.gotbetterserver.exception.MessageType;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.category.StudyRoomCategoryEntity;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.studyroom.ParticipantEntity;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.studyroom.StudyRoomEntity;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.studyroom.CategoryRepository;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.studyroom.ParticipantRepository;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.studyroom.StudyRoomCategoryRepository;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.studyroom.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyRoomService implements StudyRoomReadUseCase, StudyRoomOperationUseCase {

    private final StudyRoomRepository studyRoomRepository;
    private final CategoryRepository categoryRepository;
    private final StudyRoomCategoryRepository studyRoomCategoryRepository;
    private final ParticipantRepository participantRepository;

    @Override
    @Transactional
    public FindStudyRoomResult createStudyRoom(StudyRoomCreateCommand command) {

        validateStudyRoom(command);

        String entryCode = RandomStringGenerator.generateRandomString();
        StudyRoom studyRoom = StudyRoom.builder()
                .name(command.name())
                .description(command.description())
                .entryFee(command.entryFee())
                .entryCode(entryCode)
                .collectionAccount(command.collectionAccount())
                .maxUserNum(command.maxUserNum())
                .startDateTime(command.startDateTime())
                .endDateTime(command.endDateTime())
                .build();

        studyRoomSaveResult result = getStudyRoomSaveResult(command, studyRoom);

        addStudyRoomManagerParticipant(result.savedStudyRoom());

        return FindStudyRoomResult.findByStudyRoomCategory(
                result.savedStudyRoom(),
                result.studyRoomCategory()
        );
    }

    @Override
    public List<FindStudyRoomResult> getStudyRoomListByAccepted(boolean accepted) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getDetails();
        return participantRepository.findParticipantEntitiesByUserEntity_NicknameAndAccepted(currentUser.getNickname(), accepted)
                .stream()
                .map(ParticipantEntity::toParticipant)
                .map(Participant::getStudyRoom)
                .map(FindStudyRoomResult::findByStudyRoom)
                .collect(Collectors.toList());
    }

    private studyRoomSaveResult getStudyRoomSaveResult(StudyRoomCreateCommand command, StudyRoom studyRoom) {
        StudyRoom savedStudyRoom = studyRoomRepository.save(new StudyRoomEntity(studyRoom)).toStudyRoom();
        Category studyRoomCategory = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new GotbetterException(MessageType.CATEGORY_NOT_FOUND))
                .toCategory();

        studyRoomCategoryRepository.save(
                new StudyRoomCategoryEntity(
                        StudyRoomCategory.builder()
                                .studyRoom(savedStudyRoom)
                                .category(studyRoomCategory)
                                .build()
                )
        );
        return new studyRoomSaveResult(savedStudyRoom, studyRoomCategory);
    }

    private record studyRoomSaveResult(StudyRoom savedStudyRoom, Category studyRoomCategory) {
    }

    private void addStudyRoomManagerParticipant(StudyRoom savedStudyRoom) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getDetails();
        participantRepository.save(
                new ParticipantEntity(Participant.builder()
                        .accepted(true)
                        .role(Role.MANAGER)
                        .studyRoom(savedStudyRoom)
                        .user(user)
                        .build()
                )
        );
    }

    private void validateStudyRoom(StudyRoomCreateCommand command) {
        LocalDateTime startDateTime = command.startDateTime();
        LocalDateTime endDateTime = command.endDateTime();

        if (startDateTime.isAfter(endDateTime)) {
            throw new GotbetterException(MessageType.INVALID_STUDY_ROOM_DURATION);
        }
    }


}
