package uni.projects.backend.services;

import org.springframework.stereotype.Service;
import uni.projects.backend.dao.AmenityRepository;
import uni.projects.backend.exceptions.ResourceNotFoundException;
import uni.projects.backend.models.office.Amenity;
import uni.projects.backend.web.AmenityDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AmenityService {
    private final AmenityRepository amenityRepository;

    public AmenityService(AmenityRepository amenityRepository) {
        this.amenityRepository = amenityRepository;
    }

    public Amenity addAmenity(AmenityDto amenityDto) {
        if(amenityDto.name() == null || amenityDto.name().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        return amenityRepository.save(AmenityDto.convertTo(amenityDto));
    }

    public boolean deleteAmenity(int amenityId) {
        if(!amenityRepository.existsById(amenityId))
            throw new ResourceNotFoundException("Amenity with id " + Integer.toString(amenityId) + " not found");

        amenityRepository.deleteById(amenityId);
        return !amenityRepository.existsById(amenityId);
    }

    public List<Amenity> getAmenitiesByIds(List<Integer> list) {
        return amenityRepository.findAllById(list);
    }

    public List<AmenityDto> getAmenities() {
        return amenityRepository.findAll().stream()
                .map(AmenityDto::valueFrom)
                .collect(Collectors.toList());
    }
}
