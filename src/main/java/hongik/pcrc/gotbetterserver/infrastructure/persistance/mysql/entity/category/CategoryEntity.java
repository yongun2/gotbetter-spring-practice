package hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.category;

import hongik.pcrc.gotbetterserver.application.domain.studyroom.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@Table(name = "category")
@NoArgsConstructor
public class CategoryEntity {
    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String categoryImgUrl;

    @OneToMany(mappedBy = "categoryEntity")
    private List<StudyRoomCategoryEntity> studyRoomCategoryEntities;

    public CategoryEntity(Category category) {
        this.id = category.getId();
        this.title = category.getTitle();
        this.categoryImgUrl = category.getCategoryImgUrl();
    }

    public Category toCategory() {
        return Category.builder()
                .id(this.id)
                .title(this.title)
                .categoryImgUrl(this.categoryImgUrl)
                .build();
    }
}
