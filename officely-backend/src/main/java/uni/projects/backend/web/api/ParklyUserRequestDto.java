package uni.projects.backend.web.api;


import io.swagger.v3.oas.annotations.media.Schema;
import uni.projects.backend.models.user.Roles;

public record ParklyUserRequestDto(

        @Schema(description = "The username of the user")
    String username,

    @Schema(description = "The email of the user")
    String email,

    @Schema(description = "The first name of the user")
    String firstName,

    @Schema(description = "The last name of the user")
    String lastName,

    @Schema(description = "The role of the user")
    Roles role

    ){}
