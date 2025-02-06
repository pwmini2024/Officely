package uni.projects.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import uni.projects.backend.exceptions.GeocodingException;
import uni.projects.backend.services.geocoding.GeocodingService;
import uni.projects.backend.services.geocoding.GoogleGeocodingService;
import uni.projects.backend.services.geocoding.Location;

import java.util.concurrent.CompletableFuture;

@SpringBootApplication
@EnableAsync
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);

	}
}
