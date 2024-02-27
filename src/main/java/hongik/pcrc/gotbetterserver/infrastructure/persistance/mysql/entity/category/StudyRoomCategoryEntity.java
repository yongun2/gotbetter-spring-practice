package hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.category;

import hongik.pcrc.gotbetterserver.application.domain.studyroom.StudyRoomCategory;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.category.CategoryEntity;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.studyroom.StudyRoomEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "study_room_category")
@NoArgsConstructor
public class StudyRoomCategoryEntity {
    @Id
    @Column(name = "study_room_category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "study_room_id")
    private StudyRoomEntity studyRoomEntity;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity categoryEntity;

    public StudyRoomCategoryEntity(StudyRoomCategory studyRoomCategory) {
        this.studyRoomEntity = new StudyRoomEntity(studyRoomCategory.getStudyRoom());
        this.categoryEntity = new CategoryEntity(studyRoomCategory.getCategory());
    }



}
