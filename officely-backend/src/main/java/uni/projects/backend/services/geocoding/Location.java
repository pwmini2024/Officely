package uni.projects.backend.services.geocoding;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Location {
    private final double latitude;
    private final double longitude;
}

