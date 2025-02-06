package uni.projects.backend.web.api;

// LoginRequestDto.java

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequestDto(

        @Schema(description = "The username of the user")
        String username) {}
