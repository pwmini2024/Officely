package uni.projects.backend.web.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record ParkingSpotDto(
        @JsonProperty("Id")
        @Schema(description = "The id of the parking spot")
        Integer id,

        @JsonProperty("spotNumber")
        @Schema(description = "The number of the parking spot")
        String spotNumber,

        @JsonProperty("parkingAreaId")
        @Schema(description = "The id of the parking area")
        Integer parkingArea,

        @JsonProperty("isAvailable")
        @Schema(description = "The availability of the parking spot")
        Boolean isAvailable
) {
}
