package uni.projects.backend.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.projects.backend.controller.BaseController;
import uni.projects.backend.controller.paths.PATHS;
import uni.projects.backend.controller.paths.PATH_PREFIXES;
import uni.projects.backend.exceptions.UserValidationException;
import uni.projects.backend.models.user.User;
import uni.projects.backend.services.verification.UserVerificationService;
import uni.projects.backend.services.UserService;
import uni.projects.backend.web.OfficeDto;
import uni.projects.backend.web.UserDto;

import java.util.HashMap;
import java.util.Map;

/**
 * User Controller
 *
 * This controller provides endpoints for managing users, including:
 * - Registration of new users
 * - Retrieval of existing user details
 * - Logical deletion of users
 * - Updating user information
 *
 * All operations employ a verification mechanism to ensure that:
 * - Users are active and meet required conditions.
 * - Input data is valid and processed securely.
 *
 * Endpoints:
 * - POST /users: Register a new user.
 * - GET /users: Retrieve user details by email.
 * - DELETE /users: Logically delete a user by email.
 * - PUT /users: Update user details selectively.
 */
@RestController
@RequestMapping(path = UserController.USERS_PATH)
public class UserController extends BaseController implements UserManagementController {

    static final String USERS_PATH = PATH_PREFIXES.USER_PATH + PATHS.USERS_PATH;

    private final UserService userService;

    public UserController(UserService userService) {
        super(new UserVerificationService(),
                new HashMap<>() {{
                    put("user", UserDto.class);
                }});
        this.userService = userService;
    }

    /**
     * Register a new user.
     *
     * @param body json containing the user details for registration
     * @return the registered user details
     * @throws UserValidationException if the user validation fails
     */
    @Override
    @Operation(
            summary = "Register a new user",
            description = "Registers a new user by validating the input and saving the user to the database.",
            tags = {"User"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully registered",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "412",
                    description = "Not all arguments provided",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Invalid email format | Invalid birth date",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User with email already exists | User with phone number already exists",
                    content = @Content
            ),
    })
    @PostMapping
    public ResponseEntity<UserDto> registerUser(@RequestBody(required = false) Map<String, Object> body) {
        UserDto user = deserializeArgument(body, "user");
        UserDto userDto = UserDto.valueFrom(userService.register(user));
        return ResponseEntity.ok(userDto);
    }

    /**
     * Retrieves a user by email.
     *
     * @param authorization the authorization token
     * @return ResponseEntity containing the user's details or an error status
     * @throws UserValidationException if the user does not exist or is deleted
     */
    @Override
    @Operation(summary = "Get user by email",
            description = "Retrieve a user's details by their email.",
            tags = {"User"},
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
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Authorization header not provided",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "User not found or already deleted",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<UserDto> getUserData(@RequestHeader(value = "Authorization") String authorization) {
        User currentUser = getUser(authorization);
        return ResponseEntity.ok(UserDto.valueFrom(currentUser));
    }

    /**
     * Deletes a user by email.
     *
     * @param authorization the authorization token
     * @return ResponseEntity containing the deletion status
     * @throws UserValidationException if the user does not exist or is already deleted
     */
    @Override
    @Operation(summary = "Delete user by email",
            description = "Mark a user as deleted in the system.",
            tags = {"User"},
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
            @ApiResponse(responseCode = "200", description = "User successfully deleted",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "boolean"))),
            @ApiResponse(responseCode = "400", description = "Authorization header not provided",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Invalid input or user not found",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping
    public ResponseEntity<Boolean> deleteUser(@RequestHeader(value = "Authorization") String authorization) {
        User currentUser = getUser(authorization);
        boolean isDeleted = userService.deleteUserByEmail(currentUser, verification);
        return ResponseEntity.ok(isDeleted);
    }

    /**
     * Updates the details of an existing user.
     *
     * @param body json object holding the updated UserDto
     * @param authorization the authorization token
     * @return ResponseEntity containing the updated user details
     * @throws UserValidationException if the user is not found or already deleted
     */
    @Override
    @Operation(summary = "Update user details",
            description = "Update the details of an existing user. Only non-null fields in the request will be updated.",
            tags = {"User"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class)
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
            @ApiResponse(responseCode = "200", description = "User successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found or already deleted",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input data | Authorization header not provided",
                    content = @Content(mediaType = "application/json"))
    })
    @PutMapping
    public ResponseEntity<UserDto> updateUser(@RequestBody(required = false) Map<String, Object> body,
                                              @RequestHeader(value = "Authorization") String authorization) {
        User currentUser = getUser(authorization);
        UserDto user = deserializeArgument(body, "user");
        User updatedUser = userService.updateUser(currentUser, user, verification);
        UserDto userDto = UserDto.valueFrom(updatedUser);
        return ResponseEntity.ok(userDto);
    }
}