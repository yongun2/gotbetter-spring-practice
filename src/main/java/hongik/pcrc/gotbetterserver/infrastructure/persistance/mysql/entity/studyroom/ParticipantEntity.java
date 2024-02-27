package hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.studyroom;

import hongik.pcrc.gotbetterserver.application.domain.studyroom.Participant;
import hongik.pcrc.gotbetterserver.application.domain.studyroom.Role;
import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "study_room_participants")
@NoArgsConstructor
public class ParticipantEntity {
    @Id
    @Column(name = "study_room_participant_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private boolean accepted;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "study_room_id")
    private StudyRoomEntity studyRoomEntity;

    public ParticipantEntity(Participant participant) {
        this.id = participant.getId();
        this.accepted = participant.isAccepted();
        this.role = participant.getRole();
        this.userEntity = new UserEntity(participant.getUser());
        this.studyRoomEntity = new StudyRoomEntity(participant.getStudyRoom());
    }

    public Participant toParticipant() {
        return Participant.builder()
                .id(this.id)
                .accepted(this.accepted)
                .role(this.role)
                .user(this.userEntity.toUser())
                .studyRoom(this.studyRoomEntity.toStudyRoom())
                .build();
    }
}
