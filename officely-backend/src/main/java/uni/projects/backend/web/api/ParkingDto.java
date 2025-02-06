package uni.projects.backend.web.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The parking lot data - imported from the Parkly API")
public record ParkingDto(
    @Schema(description = "The ID of the parking lot")
    Integer id,

    @Schema(description = "The name of the parking lot")
    String name,

    @Schema(description = "The address of the parking lot")
    String address,

    @Schema(description = "The city where the parking lot is located")
    String city,

    @Schema(description = "The hourly rate of parking in the parking lot")
    Double hourlyRate,

    @Schema(description = "The longitude of the parking lot")
    Double longitude,

    @Schema(description = "The latitude of the parking lot")
    Double latitude
    )
{
}

