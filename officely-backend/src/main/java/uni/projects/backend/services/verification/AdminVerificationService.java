package uni.projects.backend.services.verification;

import uni.projects.backend.exceptions.UserValidationException;
import uni.projects.backend.models.user.Roles;
import uni.projects.backend.models.user.User;

import java.util.ArrayList;
import java.util.List;

public class AdminVerificationService implements VerificationService {

    List<Roles> permittedRoles = new ArrayList<>(){{
        add(Roles.ADMIN);
    }};

    @Override
    public void verifyUser(User user) {
        if (user == null)
            throw new UserValidationException("No such user");
        else if (!permittedRoles.contains(user.getRole()))
            throw new UserValidationException("User is not an admin");
    }
}
