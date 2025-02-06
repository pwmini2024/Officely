package uni.projects.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.projects.backend.models.office.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
}
