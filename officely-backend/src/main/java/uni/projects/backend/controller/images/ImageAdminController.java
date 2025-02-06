package uni.projects.backend.controller.images;

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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uni.projects.backend.controller.BaseController;
import uni.projects.backend.controller.paths.PATHS;
import uni.projects.backend.controller.paths.PATH_PREFIXES;
import uni.projects.backend.exceptions.UserValidationException;
import uni.projects.backend.models.user.User;
import uni.projects.backend.services.ImageService;
import uni.projects.backend.services.verification.AdminVerificationService;
import uni.projects.backend.web.ImageDto;

import java.util.HashMap;
import java.util.Map;

/**
 * Image Management Controller for Admins
 *
 * This controller provides endpoints for managing images, including:
 * - Creation of new images
 *
 * Endpoints:
 * - POST /admin/images: Create a new image.
 */
@Controller
@RequestMapping(path = ImageAdminController.IMAGES_ADMIN_PATH)
public class ImageAdminController extends BaseController {

    static final String IMAGES_ADMIN_PATH = PATH_PREFIXES.ADMIN_PATH + PATHS.IMAGES_PATH;

    @Autowired
    private ImageService imageService;

    public ImageAdminController() {
        super(new AdminVerificationService(),
                new HashMap<>() {{
                    put("image", ImageDto.class);
                }});
    }

    /**
     * Create a new image.
     *
     * @param body json containing the image details for creation
     * @param authorization the authorization token (user email)
     * @return the created image details
     * @throws UserValidationException if the user validation fails
     */
    @PostMapping
    @Operation(
            summary = "Create a new image",
            description = "Creates a new image by validating the input and saving the image to the database.",
            tags = {"Image"},
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
                    responseCode = "201",
                    description = "Image successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ImageDto.class)
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
                    description = "Invalid image data",
                    content = @Content
            )
    })
    public ResponseEntity<ImageDto> createImage(@RequestBody(required = false) Map<String, Object> body,
                                                @RequestHeader(value = "Authorization") String authorization) {
        User currentUser = getUser(authorization);

        ImageDto image = deserializeArgument(body, "image");

        return new ResponseEntity<>(imageService.createImage(image), HttpStatus.CREATED);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Boolean> deleteImage(@PathVariable Integer imageId, @RequestHeader(value = "Authorization") String authorization) {
        User currentUser = getUser(authorization);
        imageService.deleteImage(imageId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}