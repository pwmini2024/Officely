package uni.projects.backend.controller.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.projects.backend.controller.BaseController;
import uni.projects.backend.controller.paths.PATHS;
import uni.projects.backend.controller.paths.PATH_PREFIXES;
import uni.projects.backend.models.reservation.ReservationFilter;
import uni.projects.backend.models.user.User;
import uni.projects.backend.services.ReservationService;
import uni.projects.backend.services.verification.UserVerificationService;
import uni.projects.backend.web.ImageDto;
import uni.projects.backend.web.ReservationDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ReservationController.RESERVATION_PATH)
public class ReservationController extends BaseController {

    static final String RESERVATION_PATH = PATH_PREFIXES.USER_PATH + PATHS.RESERVATIONS_PATH;

    public ReservationController() {
        super(new UserVerificationService(),
                new HashMap<>() {{
                    put("reservation", ReservationDto.class);
                    put("filter", ReservationFilter.class);
                }}
        );
    }

    @Autowired
    private ReservationService reservationService;

    @PostMapping("/office/{officeId}")
    @Operation(
            summary = "Create a new reservation",
            description = "Creates a new reservation for a specific office.",
            tags = {"Reservation"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationDto.class)
                    )
            ),
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Authorization token (user email) to authenticate the user",
                            schema = @Schema(type = "string")
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservation successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden: User validation failed",
                    content = @Content
            )
    })
    public ResponseEntity<ReservationDto> createReservation(@RequestBody(required = false) Map<String, Object> body,
                                                            @PathVariable String officeId,
                                                            @RequestHeader("Authorization") String authorization) {

        User currentUser = getUser(authorization);
        ReservationDto reservationDto = deserializeArgument(body, "reservation");

        ReservationDto reservation = reservationService.createReservation(reservationDto, currentUser, officeId);

        return ResponseEntity.ok(reservation);
    }

    @GetMapping
    @Operation(
            summary = "Get reservations",
            description = "Retrieve a list of reservations with optional filters and sorting.",
            tags = {"Reservation"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationFilter.class)
                    )
            ),
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Authorization token (user email) to authenticate the user",
                            schema = @Schema(type = "string")
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of reservations retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided",
                    content = @Content
            )
    })
    public ResponseEntity<List<ReservationDto>> getReservations(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "true") boolean ascending,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @ModelAttribute ReservationFilter filter,
            @RequestHeader("Authorization") String authorization) {

        if (page == null) {
            page = 0;
        }
        if (pageSize == null) {
            pageSize = 10;
        }

        User currentUser = getUser(authorization);

        List<ReservationDto> reservations = reservationService.getReservations(currentUser, sortBy, ascending, filter, page, pageSize);

        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get reservation by ID",
            description = "Retrieve details of a specific reservation by ID.",
            tags = {"Reservation"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ImageDto.class)
                    )
            ),
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Authorization token (user email) to authenticate the user",
                            schema = @Schema(type = "string")
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservation details retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reservation not found",
                    content = @Content
            )
    })
    public ResponseEntity<ReservationDto> getReservation(@PathVariable String id,
                                                         @RequestHeader("Authorization") String authorization) {

        User currentUser = getUser(authorization);

        ReservationDto reservation = reservationService.getReservation(id, currentUser);

        return ResponseEntity.ok(reservation);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update reservation",
            description = "Update the details of an existing reservation.",
            tags = {"Reservation"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationDto.class)
                    )
            ),
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Authorization token (user email) to authenticate the user",
                            schema = @Schema(type = "string")
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservation successfully updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden: User validation failed",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reservation not found",
                    content = @Content
            )
    })
    public ResponseEntity<ReservationDto> updateReservation(@PathVariable String id,
                                                            @RequestBody(required = false) Map<String, Object> body,
                                                            @RequestHeader("Authorization") String authorization) {

        User currentUser = getUser(authorization);

        ReservationDto reservationDto = deserializeArgument(body, "reservation");

        ReservationDto reservation = reservationService.updateReservation(id, reservationDto, currentUser);
        return ResponseEntity.ok(reservation);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete reservation",
            description = "Cancel a reservation by ID.",
            tags = {"Reservation"},
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Authorization token (user email) to authenticate the user",
                            schema = @Schema(type = "string")
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservation successfully canceled",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden: User validation failed",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reservation not found",
                    content = @Content
            )
    })
    public ResponseEntity<ReservationDto> deleteReservation(@PathVariable String id,
                                                            @RequestHeader("Authorization") String authorization) {

        User currentUser = getUser(authorization);

        ReservationDto reservation = reservationService.cancelReservation(id, currentUser);

        return ResponseEntity.ok(reservation);
    }

    @PostMapping("/{id}/pay")
    @Operation(
            summary = "Pay for reservation",
            description = "Mark a reservation as paid.",
            tags = {"Reservation"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservation successfully paid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden: User validation failed",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reservation not found",
                    content = @Content
            )
    })
    public ResponseEntity<ReservationDto> payReservation(@PathVariable String id,
                                                         @RequestHeader("Authorization") String authorization) {

        User currentUser = getUser(authorization);
        ReservationDto reservation = reservationService.payReservation(id, currentUser);

        return ResponseEntity.ok(reservation);
    }
}