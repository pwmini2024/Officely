package uni.projects.backend.services;

import lombok.SneakyThrows;
import uni.projects.backend.dao.UserRepository;
import uni.projects.backend.exceptions.ArgumentException;
import uni.projects.backend.exceptions.UnauthorizedException;
import uni.projects.backend.exceptions.UserValidationException;
import uni.projects.backend.models.user.User;
import uni.projects.backend.services.verification.VerificationService;
import uni.projects.backend.web.UserDto;

import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserMainService implements UserService {

    protected final UserRepository userRepository;

    public UserMainService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean emailFormat(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @SneakyThrows
    @Override
    public void verifyUser(VerificationService verification, User user) {
        verification.verifyUser(user);
    }

    @Override
    public User register(UserDto user) {
        if (user.email() == null || user.birthDate() == null || user.name() == null || user.surname() == null || user.phoneNumber() == null) {
            throw new ArgumentException("Not all arguments provided");
        }
        if (!emailFormat(user.email())) {
            throw new UserValidationException("Invalid email format");
        }
        if (userRepository.existsByEmailAndDeletedFalse(user.email())) {
            throw new UnauthorizedException("User with email " + user.email() + " already exists");
        }
        if (userRepository.existsByPhoneNumberAndDeletedFalse(user.phoneNumber())) {
            throw new UnauthorizedException("User with phone number " + user.phoneNumber() + " already exists");
        }
        if (user.birthDate().isAfter(LocalDate.now())) {
            throw new UserValidationException("Invalid birth date");
        }
        User newUser = UserDto.convertToUser(user);
        return userRepository.save(newUser);
    }

    @Override
    public User getUserByEmailOrPhoneNumber(String data, VerificationService verification) {
        Optional<User> user = userRepository.findByEmail(data);
        if (user.isEmpty() || user.get().isDeleted()) {
            user = userRepository.findByPhoneNumber(data);
        }
        verifyUser(verification, user.orElse(null));
        if (user.isEmpty()) {
            throw new UserValidationException("User with email or phone number " + data + " not found");
        }
        return user.get();
    }

    @Override
    public boolean deleteUserByEmail(User currentUser, VerificationService verification) {
        currentUser.setDeleted(true);
        userRepository.save(currentUser);
        return currentUser.isDeleted();
    }

    @Override
    public User updateUser(User currentUser, UserDto user, VerificationService verification) {

        if(user.name() != null)
            currentUser.setName(user.name());
        if(user.surname() != null)
            currentUser.setSurname(user.surname());
        if(user.birthDate() != null)
            currentUser.setBirthDate(user.birthDate());
        return userRepository.save(currentUser);
    }
}
