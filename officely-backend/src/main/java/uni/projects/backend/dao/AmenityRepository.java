package uni.projects.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.projects.backend.models.office.Amenity;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Integer> {
    boolean existsById(int id);
    void deleteById(int id);
}
