package uni.projects.backend.web;

import io.swagger.v3.oas.annotations.media.Schema;
import uni.projects.backend.models.office.Amenity;
import uni.projects.backend.models.office.Office;

import java.util.List;

public record AmenityDto(
        @Schema(description = "The ID of the amenity (should be left empty when created, will be returned but ignored if user passes it)", example = "1")
        Integer id,

        @Schema(description = "The name of the amenity (should be left when adding to the office)", example = "WiFi")
        String name
) {

    public static AmenityDto valueFrom(Amenity amenity) {
        return new AmenityDto(
                amenity.getId(),
                amenity.getName()
        );
    }

    public static Amenity convertTo(AmenityDto amenityDto) {
        Amenity amenity = new Amenity();
        amenity.setId(amenityDto.id());
        amenity.setName(amenityDto.name());
        return amenity;
    }
}
