package uni.projects.backend.web;


import io.swagger.v3.oas.annotations.media.Schema;
import uni.projects.backend.models.office.Image;

import java.util.Base64;

public record ImageDto(
        @Schema(description = "The ID of the image (should be left empty when created, will be returned but ignored if user passes it)", example = "1")
        Integer id,

        @Schema(description = "The Base64 encoded data of the image", example = "iVBORw0KGgoAAAANSUhEUgAA...")
        String data
) {

    public static ImageDto valueFrom(Image image) {
        return new ImageDto(
                image.getId(),
                Base64.getEncoder().encodeToString(image.getData()) // Encode to Base64
        );
    }

    public static ImageDto onlyId(Image image) {
        return new ImageDto(
                image.getId(),
                null
        );
    }

    public static Image convertTo(ImageDto imageDto) {
        Image image = new Image();
        image.setId(imageDto.id());
        image.setData(Base64.getDecoder().decode(imageDto.data())); // Decode from Base64
        return image;
    }
}
