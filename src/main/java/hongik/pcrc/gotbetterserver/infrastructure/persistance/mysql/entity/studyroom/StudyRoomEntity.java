package hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.studyroom;

import hongik.pcrc.gotbetterserver.application.domain.studyroom.StudyRoom;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.category.StudyRoomCategoryEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Table(name = "study_rooms")
@NoArgsConstructor
public class StudyRoomEntity {
    @Id
    @Column(name = "study_room_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private int entryFee;
    private String entryCode;
    private int maxUserNum;
    private String collectionAccount;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @OneToMany(mappedBy = "studyRoomEntity", cascade = CascadeType.REMOVE)
    private List<StudyRoomCategoryEntity> studyRoomCategoryEntities;

    @OneToMany(mappedBy = "studyRoomEntity", cascade = CascadeType.REMOVE)
    private List<ParticipantEntity> participantEntities;


    public StudyRoomEntity(StudyRoom studyRoom) {
        this.id = studyRoom.getId();
        this.name = studyRoom.getName();
        this.description = studyRoom.getDescription();
        this.entryFee = studyRoom.getEntryFee();
        this.entryCode = studyRoom.getEntryCode();
        this.maxUserNum = studyRoom.getMaxUserNum();
        this.collectionAccount = studyRoom.getCollectionAccount();
        this.startDateTime = studyRoom.getStartDateTime();
        this.endDateTime = studyRoom.getEndDateTime();
    }

    public StudyRoom toStudyRoom() {
        return StudyRoom.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .entryFee(this.entryFee)
                .entryCode(this.entryCode)
                .maxUserNum(this.maxUserNum)
                .collectionAccount(this.collectionAccount)
                .startDateTime(this.startDateTime)
                .endDateTime(this.endDateTime)
                .build();
    }
}
