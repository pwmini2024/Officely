package uni.projects.backend.models;

import org.springframework.context.annotation.Profile;

@Profile({"!cors"})
public class CorsDefaultProperties extends CorsProperties {
}
