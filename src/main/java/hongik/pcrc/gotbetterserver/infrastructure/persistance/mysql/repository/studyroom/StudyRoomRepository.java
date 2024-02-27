package hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.studyroom;

import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.studyroom.StudyRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyRoomRepository extends JpaRepository<StudyRoomEntity, Integer> {
    Optional<StudyRoomEntity> findStudyRoomEntityByName(String name);
}
