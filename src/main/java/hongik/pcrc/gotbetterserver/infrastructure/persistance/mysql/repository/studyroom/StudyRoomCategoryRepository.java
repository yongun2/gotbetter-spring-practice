package hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.studyroom;

import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.category.StudyRoomCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyRoomCategoryRepository extends JpaRepository<StudyRoomCategoryEntity, Integer> {

    Optional<StudyRoomCategoryEntity> findStudyRoomCategoryEntityByStudyRoomEntity_Id(int studyRoomId);
}
