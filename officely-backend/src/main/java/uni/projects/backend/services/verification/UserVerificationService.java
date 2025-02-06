package uni.projects.backend.services.verification;


import uni.projects.backend.exceptions.UserValidationException;
import uni.projects.backend.models.user.User;

public class UserVerificationService implements VerificationService {
    @Override
    public void verifyUser(User user) {
        if (user == null)
            throw new UserValidationException("No such user");
    }
}
