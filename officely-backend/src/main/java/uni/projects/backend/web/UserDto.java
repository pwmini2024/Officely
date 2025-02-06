package uni.projects.backend.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import uni.projects.backend.models.user.Roles;
import uni.projects.backend.models.user.User;

import java.time.LocalDate;

public record UserDto (
        @Schema(description = "User email, ignored when editing a user", example = "user@email.com")
        @Email String email,

        @Schema(description = "User name", example = "John")
        String name,

        @Schema(description = "User surname", example = "Doe")
        String surname,

        @Schema(description = "User phone number, ignored when editing a user", example = "+1234567890")
        String phoneNumber,

        @Schema(description = "User birth date", example = "2000-01-01")
        LocalDate birthDate
) {

    public static UserDto valueFrom(User user) {
        return new UserDto(user.getEmail(), user.getName(), user.getSurname(), user.getPhoneNumber(), user.getBirthDate());
    }

    public static User convertToUser(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.email());
        user.setName(userDto.name());
        user.setSurname(userDto.surname());
        user.setBirthDate(userDto.birthDate());
        user.setPhoneNumber(userDto.phoneNumber());
        user.setDeleted(false);
        user.setRole(Roles.USER);
        return user;
    }
}
