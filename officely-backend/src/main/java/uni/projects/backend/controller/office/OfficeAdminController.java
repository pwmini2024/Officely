package uni.projects.backend.controller.office;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.projects.backend.controller.BaseController;
import uni.projects.backend.controller.paths.PATHS;
import uni.projects.backend.controller.paths.PATH_PREFIXES;
import uni.projects.backend.exceptions.UserValidationException;
import uni.projects.backend.models.office.Office;
import uni.projects.backend.models.user.User;
import uni.projects.backend.services.OfficeService;
import uni.projects.backend.services.verification.AdminVerificationService;
import uni.projects.backend.utils.BodyArgumentDeserializer;
import uni.projects.backend.web.OfficeDto;

import java.util.HashMap;
import java.util.Map;

/**
 * Office Management Controller for Admins
 *
 * This controller provides endpoints for managing offices, including:
 * - Creation of new offices
 * - Retrieval of existing office details
 * - Logical deletion of offices
 * - Updating office information
 *
 * All operations employ a verification mechanism to ensure that:
 * - Users are active and meet required conditions.
 * - Input data is valid and processed securely.
 *
 * Endpoints:
 * - POST /admin/offices: Create a new office.
 * - DELETE /admin/offices: Logically delete an office by ID.
 * - PUT /admin/offices: Update office details selectively.
 */
@RestController
@RequestMapping(path = OfficeAdminController.OFFICES_ADMIN_PATH)
public class OfficeAdminController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(OfficeAdminController.class);
    static final String OFFICES_ADMIN_PATH = PATH_PREFIXES.ADMIN_PATH + PATHS.OFFICES_PATH;

    @Autowired
    private OfficeService officeService;

    protected OfficeAdminController() {
        super(new AdminVerificationService(),
                new HashMap<>() {{
                    put("office", OfficeDto.class);
                }}
        );
    }

    /**
     * Create a new office.
     *
     * @param body json containing the office details for creation
     * @param authorization the authorization token (user email)
     * @return the created office details
     * @throws UserValidationException if the user validation fails
     */
    @PostMapping
    @Operation(
            summary = "Create a new office",
            description = "Creates a new office by validating the input and saving the office to the database.",
            tags = {"Office"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OfficeDto.class)
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
                    description = "Office successfully created",
                    content = @Content(
                            mediaType = "application/json",

                            schema = @Schema(implementation = OfficeDto.class)
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
                    description = "Invalid office data",
                    content = @Content
            )
    })
    public ResponseEntity<OfficeDto> createOffice(@RequestBody(required = false) Map<String, Object> body,
                                                  @RequestHeader(value = "Authorization") String authorization) {

        User currentUser = getUser(authorization);

        OfficeDto office = deserializeArgument(body, "office");
        Office newOffice  = officeService.createOffice(office, currentUser);

        return new ResponseEntity<>(OfficeDto.valueFrom(newOffice), HttpStatus.CREATED);
    }

    /**
     * Delete an office by ID.
     *
     * @param id the ID of the office to delete
     * @param authorization the authorization token (user email)
     * @return ResponseEntity containing the deletion status
     * @throws UserValidationException if the user validation fails
     */
    @DeleteMapping
    @Operation(
            summary = "Delete an office by ID",
            description = "Logically deletes an office by its ID.",
            tags = {"Office"},
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
                    description = "Office successfully deleted",
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
                    description = "Office not found",
                    content = @Content
            )
    })
    public ResponseEntity<Boolean> deleteOffice(@RequestParam String id,
                                                @RequestHeader("Authorization") String authorization) {

        User currentUser = getUser(authorization);

        boolean isDeleted = officeService.deleteOffice(id);
        return ResponseEntity.ok(isDeleted);
    }

    /**
     * Update the details of an existing office.
     *
     * @param body json object holding the updated OfficeDto
     * @param id the ID of the office to update
     * @param authorization the authorization token (user email)
     * @return ResponseEntity containing the updated office details
     * @throws UserValidationException if the user validation fails
     */
    @PutMapping
    @Operation(
            summary = "Update office details",
            description = "Update the details of an existing office. Only non-null fields in the request will be updated.",
            tags = {"Office"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OfficeDto.class)
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
                    description = "Office successfully updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OfficeDto.class)
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
                    description = "Office not found",
                    content = @Content
            )
    })
    public ResponseEntity<OfficeDto> updateOffice(@RequestBody(required = false) Map<String, Object> body,
                                                  @RequestParam String id,
                                                  @RequestHeader("Authorization") String authorization) {

        User currentUser = getUser(authorization);

        OfficeDto office = BodyArgumentDeserializer.deserializeBodyArgument(body, "office", BODY_ARGUMENTS.get("office"));
        if (office == null) {
            throw new IllegalArgumentException("Office is required");
        }

        OfficeDto updatedOffice = officeService.updateOffice(id, office);
        return ResponseEntity.ok(updatedOffice);
    }

}