package uni.projects.backend.web.api;


import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ParkingReservationDto (

        @Schema(description = "The id of the parking spot")
        Integer parkingSpotId,

    @Schema(description = "The id of the user")
    Integer userId,

    @Schema(description = "The start time of the reservation")
    LocalDateTime startTime,

    @Schema(description = "The end time of the reservation")
    LocalDateTime endTime,

    @Schema(description = "The total cost of the reservation")
    Double totalCost
) {

}
