package uni.projects.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import uni.projects.backend.dao.UserRepository;
import uni.projects.backend.services.UserMainService;
import uni.projects.backend.services.UserService;

@Profile("!batch")
public class NonBatchConfig {

    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserMainService(userRepository);
    }

}
