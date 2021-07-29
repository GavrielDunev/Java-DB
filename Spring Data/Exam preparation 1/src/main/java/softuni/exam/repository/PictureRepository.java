package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import softuni.exam.models.entity.Picture;

public interface PictureRepository extends JpaRepository<Picture, Long> {

}
