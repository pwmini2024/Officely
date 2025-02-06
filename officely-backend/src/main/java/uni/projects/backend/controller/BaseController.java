package uni.projects.backend.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uni.projects.backend.exceptions.ArgumentException;
import uni.projects.backend.models.user.User;
import uni.projects.backend.services.UserService;
import uni.projects.backend.services.verification.VerificationService;
import uni.projects.backend.utils.BodyArgumentDeserializer;

import java.util.HashMap;
import java.util.Map;

import static uni.projects.backend.utils.StringUtils.capitalizeFirstLetter;

public abstract class BaseController {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final VerificationService verification;

    protected final HashMap<String, Class<?>> BODY_ARGUMENTS = new HashMap<>();

    @Autowired
    private UserService userService;

    protected BaseController(VerificationService verificationService,
                             HashMap<String, Class<?>> BODY_ARGUMENTS) {
        this.verification = verificationService;
        this.BODY_ARGUMENTS.putAll(BODY_ARGUMENTS);

    }

    public User getUser(Map<String, Object> body) {
        String email = deserializeArgument(body, "email");
        return userService.getUserByEmailOrPhoneNumber(email, verification);
    }

    public User getUser(String header) {
        String email = parseEmailFromHeader(header);
        return userService.getUserByEmailOrPhoneNumber(email, verification);
    }

    private String parseEmailFromHeader(String emailHeader) {
        if (emailHeader == null) {
            throw new IllegalArgumentException("Invalid email in Authorization header");
        }
        return emailHeader;
    }

    public <T> T deserializeArgument(Map<String, Object> body, String key) {
        T t = BodyArgumentDeserializer.deserializeBodyArgument(body, key, BODY_ARGUMENTS.get(key));

        if(t == null) {
            throw new ArgumentException(capitalizeFirstLetter(key) + " is required");
        }

        return t;
    }
}
