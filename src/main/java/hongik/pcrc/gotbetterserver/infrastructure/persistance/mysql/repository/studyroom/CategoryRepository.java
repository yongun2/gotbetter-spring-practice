package hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.repository.studyroom;

import hongik.pcrc.gotbetterserver.infrastructure.persistance.mysql.entity.category.CategoryEntity;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<CategoryEntity, Integer> {
}
