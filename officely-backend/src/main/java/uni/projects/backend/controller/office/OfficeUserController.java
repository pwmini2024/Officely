package uni.projects.backend.controller.office;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.projects.backend.controller.BaseController;
import uni.projects.backend.controller.paths.PATHS;
import uni.projects.backend.models.office.OfficeFilter;
import uni.projects.backend.services.OfficeService;
import uni.projects.backend.services.TrafficStatisticService;
import uni.projects.backend.services.verification.AnyVerificationService;
import uni.projects.backend.web.OfficeDto;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

/**
 * Office Management Controller for Users
 *
 * This controller provides endpoints for users to:
 * - Retrieve a list of available offices with optional filters and sorting.
 * - Retrieve details of a specific office by ID.
 * - Check the availability of a specific office by ID and date.
 *
 * Endpoints:
 * - GET /offices: Retrieve a list of available offices.
 * - GET /offices/{id}: Retrieve details of a specific office by ID.
 * - GET /offices/{id}/availability: Check the availability of a specific office by ID and date.
 */
@RestController
@RequestMapping(path = OfficeUserController.OFFICES_USER_PATH)
public class OfficeUserController extends BaseController {

    static final String OFFICES_USER_PATH = PATHS.OFFICES_PATH;

    public OfficeUserController() {
        super(new AnyVerificationService(),
                new HashMap<>() {{
                    put("officeFilter", OfficeFilter.class);
                    put("dateStart", LocalDate.class);
                }});
    }

    @Autowired
    private TrafficStatisticService trafficStatisticService;

    @Autowired
    private OfficeService officeService;

    /**
     * Retrieve a list of available offices.
     *
     * @param sortBy the field to sort by (optional)
     * @param ascending whether the sorting should be ascending (default is true)
     * @param page the page number for pagination (default is 0)
     * @param pageSize the number of items per page for pagination (default is 10)
     * @param officeFilter the filter criteria for offices
     * @return a list of available offices
     */
    @GetMapping()
    @Operation(
            summary = "Retrieve a list of available offices",
            description = "Retrieve a list of available offices with optional filters and sorting.",
            tags = {"Office"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OfficeFilter.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of available offices retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OfficeDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided (e.g., dateStart or dateEnd in the past)",
                    content = @Content
            )
    })
    public ResponseEntity<List<OfficeDto>> getOffice(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "true") boolean ascending,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @ModelAttribute OfficeFilter officeFilter) {

        if(page == null) {
            page = 0;
        }
        if(pageSize == null) {
            pageSize = 10;
        }

        LocalDate dateStart = officeFilter.getAvailableFrom();
        if (dateStart == null) {
            dateStart = LocalDate.now();
        }
        if (dateStart.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("dateStart cannot be in the past");
        }
        LocalDate dateEnd = officeFilter.getAvailableTo();
        if (dateEnd == null) {
            dateEnd = dateStart.plusDays(30);
        }
        if (dateEnd.isBefore(dateStart)) {
            throw new IllegalArgumentException("dateEnd cannot be before dateStart");
        }
        if (dateEnd.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("dateEnd cannot be in the past");
        }

        trafficStatisticService.incrementVisitorsBetweenDates(dateStart, dateEnd);

        List<OfficeDto> offices = officeService.findAvailableOfficesAndFilter(dateStart, dateEnd, officeFilter, sortBy, ascending, page, pageSize);

        return ResponseEntity.ok().body(offices);
    }

    /**
     * Retrieve details of a specific office by ID.
     *
     * @param id the ID of the office
     * @return the details of the office
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Retrieve details of a specific office by ID",
            description = "Retrieve details of a specific office by its ID.",
            tags = {"Office"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Office details retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OfficeDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Office not found",
                    content = @Content
            )
    })
    public ResponseEntity<OfficeDto> getOfficeById(@PathVariable String id) {
        return ResponseEntity.ok().body(officeService.getOfficeById(id));
    }

    /**
     * Check the availability of a specific office by ID and date.
     *
     * @param id the ID of the office
     * @param dateStart the start date to check availability
     * @return a list of availability statuses
     */
    @GetMapping("/{id}/availability")
    @Operation(
            summary = "Check the availability of a specific office by ID and date",
            description = "Check the availability of a specific office by its ID and a given start date.",
            tags = {"Office"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LocalDate.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Office availability retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OfficeService.Availability.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided (e.g., dateStart in the past)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Office not found",
                    content = @Content
            )
    })
    public ResponseEntity<List<OfficeService.Availability>> isOfficeAvailable(@PathVariable String id,
                                                                              @RequestParam(required = true) LocalDate dateStart) {

        List<OfficeService.Availability> availableDates = officeService.getAvailableDatesWithPriceMultiplyer(id, dateStart);

        return ResponseEntity.ok().body(availableDates);
    }
}