package uni.projects.backend.web.api;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ParkingResponseDto(

        @Schema(description = "The content of the response containing the parking lots")
        List<ParkingDto> content
) {}
