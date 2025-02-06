package uni.projects.backend.web;

import io.swagger.v3.oas.annotations.media.Schema;
import uni.projects.backend.models.office.Office;
import uni.projects.backend.models.user.User;

import java.util.List;

public record OfficeDto(

        @Schema(description = "The ID of the office (should be left empty when created, will be returned but ignored if user passes it)", example = "1")
        String id,

        @Schema(description = "The name of the office", example = "Main Office")
        String name,

        @Schema(description = "The metric area of the office in square meters", example = "100.0")
        Double metricArea,

        @Schema(description = "The floor number where the office is located", example = "2")
        Integer floor,

        @Schema(description = "The room number of the office", example = "201")
        Integer roomNumber,

        @Schema(description = "The country where the office is located", example = "USA")
        String country,

        @Schema(description = "The city where the office is located", example = "New York")
        String city,

        @Schema(description = "The postal code of the office location", example = "10001")
        String postalCode,

        @Schema(description = "The address of the office", example = "123 Main St")
        String address,

        @Schema(description = "The X coordinate of the office location (should be left empty when created, will be returned but ignored if user passes it)", example = "40.7128")
        Double x,

        @Schema(description = "The Y coordinate of the office location (should be left empty when created, will be returned but ignored if user passes it)", example = "-74.0060")
        Double y,

        @Schema(description = "The price of the office per day", example = "150.0")
        Double price,

        @Schema(description = "The list of amenities available in the office")
        List<AmenityDto> amenities,

        @Schema(description = "The list of image IDs associated with the office")
        List<ImageDto> images,

        @Schema(description = "The email of the office owner (should be left empty when created, will be returned but ignored if user passes it)", example = "owner@example.com")
        String owner_email,

        @Schema(description = "The phone number of the office owner (should be left empty when created, will be returned but ignored if user passes it)", example = "+1234567890")
        String owner_phone_number)
{

    public static OfficeDto valueFrom(Office office) {
        return new OfficeDto(
                office.getId(),
                office.getName(),
                office.getMetricArea(),
                office.getFloor(),
                office.getRoomNumber(),
                office.getCountry(),
                office.getCity(),
                office.getPostalCode(),
                office.getAddress(),
                office.getX(),
                office.getY(),
                office.getPrice(),
                office.getAmenities().stream().map(AmenityDto::valueFrom).toList(),
                office.getImages().stream().map(ImageDto::onlyId).toList(),
                office.getOwner().getEmail(),
                office.getOwner().getPhoneNumber()
        );
    }

    public static Office convertTo(OfficeDto officeDto) {
        Office newOffice = new Office();
        newOffice.setId(officeDto.id());
        newOffice.setName(officeDto.name());
        newOffice.setMetricArea(officeDto.metricArea());
        newOffice.setFloor(officeDto.floor());
        newOffice.setRoomNumber(officeDto.roomNumber());
        newOffice.setCountry(officeDto.country());
        newOffice.setCity(officeDto.city());
        newOffice.setPostalCode(officeDto.postalCode());
        newOffice.setAddress(officeDto.address());
        newOffice.setX(officeDto.x());
        newOffice.setY(officeDto.y());
        newOffice.setPrice(officeDto.price());
        newOffice.setAmenities(officeDto.amenities().stream().map(AmenityDto::convertTo).toList());
        newOffice.setImages(officeDto.images().stream().map(ImageDto::convertTo).toList());
        newOffice.setDeleted(false);
        newOffice.setOwner(new User( "-1", officeDto.owner_email, null, null, officeDto.owner_phone_number, null, null, false));
        return newOffice;
    }
}