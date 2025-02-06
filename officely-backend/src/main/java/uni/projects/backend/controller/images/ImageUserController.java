package uni.projects.backend.controller.images;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uni.projects.backend.controller.BaseController;
import uni.projects.backend.controller.paths.PATHS;
import uni.projects.backend.controller.paths.PATH_PREFIXES;
import uni.projects.backend.models.user.User;
import uni.projects.backend.services.ImageService;
import uni.projects.backend.services.verification.AnyVerificationService;
import uni.projects.backend.services.verification.UserVerificationService;
import uni.projects.backend.web.ImageDto;

import java.util.HashMap;
import java.util.List;

/**
 * Image Management Controller for Users
 *
 * This controller provides endpoints for users to:
 * - Retrieve details of a specific image by ID.
 *
 * Endpoints:
 * - GET /user/images/{id}: Retrieve details of a specific image by ID.
 */
@Controller
@RequestMapping(path = ImageUserController.IMAGES_PATH)
public class ImageUserController extends BaseController {

        static final String IMAGES_PATH = PATH_PREFIXES.USER_PATH + PATHS.IMAGES_PATH;

        @Autowired
        private ImageService imageService;

        public ImageUserController() {
                super(new AnyVerificationService(),
                        new HashMap<>() {{
                        }});
        }

        /**
         * Retrieve details of a specific image by ID.
         *
         * @param id the ID of the image
         * @param authorization the authorization token (user email)
         * @return the details of the image
         */
        @GetMapping("/{id}")
        @Operation(
                summary = "Retrieve details of a specific image by ID",
                description = "Retrieve details of a specific image by its ID.",
                tags = {"Image"}
        )
        @ApiResponses(value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Image details retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ImageDto.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Image not found",
                        content = @Content
                )
        })
        public ResponseEntity<ImageDto> getImage(@PathVariable Integer id,
                                                 @RequestHeader(value = "Authorization") String authorization) {
                User currentUser = getUser(authorization);

                return ResponseEntity.ok().body(imageService.getImage(id));
        }
}