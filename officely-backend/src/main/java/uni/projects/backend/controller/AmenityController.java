package uni.projects.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.projects.backend.controller.paths.PATHS;
import uni.projects.backend.controller.paths.PATH_PREFIXES;
import uni.projects.backend.exceptions.UserValidationException;
import uni.projects.backend.models.office.Amenity;
import uni.projects.backend.models.user.User;
import uni.projects.backend.services.AmenityService;
import uni.projects.backend.services.verification.AdminVerificationService;
import uni.projects.backend.web.AmenityDto;
import uni.projects.backend.web.OfficeDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Amenity Management Controller
 *
 * This controller provides endpoints for managing amenities, including:
 * - Adding new amenities
 * - Deleting existing amenities
 *
 * Endpoints:
 * - POST /admin/amenities: Add a new amenity.
 * - DELETE /admin/amenities: Delete an amenity by ID.
 */
@RestController
@RequestMapping(path = AmenityController.AMENITY_PATH)
public class AmenityController extends BaseController {

    public static final String AMENITY_PATH = PATH_PREFIXES.ADMIN_PATH + PATHS.AMENITIES_PATH;

    @Autowired
    private AmenityService amenityService;

    public AmenityController() {
        super(new AdminVerificationService(),
                new HashMap<>() {{
                    put("amenity", AmenityDto.class);
                }});
    }

    /**
     * Add a new amenity.
     *
     * @param body json containing the amenity details for creation
     * @param authorization the authorization token (user email)
     * @return the created amenity details
     * @throws UserValidationException if the user validation fails
     */
    @PostMapping
    @Operation(
            summary = "Add a new amenity",
            description = "Adds a new amenity by validating the input and saving the amenity to the database.",
            tags = {"Amenity"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AmenityDto.class)
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
                    responseCode = "201",
                    description = "Amenity successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AmenityDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided (e.g., missing or malformed data)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden: User validation failed",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid amenity data",
                    content = @Content
            )
    })
    public ResponseEntity<AmenityDto> addAmenity(
            @RequestBody(required = false) Map<String, Object> body,
            @RequestHeader(value = "Authorization") String authorization) {

        User currentUser = getUser(authorization);
        AmenityDto amenity = deserializeArgument(body, "amenity");

        Amenity newAmenity = amenityService.addAmenity(amenity);

        return new ResponseEntity<>(AmenityDto.valueFrom(newAmenity), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AmenityDto>> getAmenities(@RequestHeader(value = "Authorization") String authorization) {
        User currentUser = getUser(authorization);
        return new ResponseEntity<>(amenityService.getAmenities(), HttpStatus.OK);
    }

    /**
     * Delete an amenity by ID.
     *
     * @param id the ID of the amenity to delete
     * @param authorization the authorization token (user email)
     * @return ResponseEntity containing the deletion status
     * @throws UserValidationException if the user validation fails
     */
    @DeleteMapping
    @Operation(
            summary = "Delete an amenity by ID",
            description = "Deletes an amenity by its ID.",
            tags = {"Amenity"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Amenity successfully deleted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "boolean")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided (e.g., missing or malformed data)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden: User validation failed",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Amenity not found",
                    content = @Content
            )
    })
    public ResponseEntity<Boolean> deleteAmenity(
            @RequestParam int id,
            @RequestHeader(value = "Authorization") String authorization) {

        User currentUser = getUser(authorization);

        boolean deleted = amenityService.deleteAmenity(id);
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }
}