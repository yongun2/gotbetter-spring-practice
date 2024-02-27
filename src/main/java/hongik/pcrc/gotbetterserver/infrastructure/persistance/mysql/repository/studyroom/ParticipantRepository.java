package hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.studyroom;

import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.studyroom.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Integer> {
    Optional<ParticipantEntity> findParticipantEntityByStudyRoomEntity_Name(String studyRoomName);

    List<ParticipantEntity> findParticipantEntitiesByUserEntity_NicknameAndAccepted(String nickname, boolean accepted);
}
