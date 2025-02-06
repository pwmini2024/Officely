package uni.projects.backend.services.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uni.projects.backend.services.OfficeService;
import uni.projects.backend.web.OfficeDto;
import uni.projects.backend.web.api.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ParkingService {

    @Value("${parkly.api.url}")
    private String parklyApiUrl;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final double EARTH_RADIUS = 6371.0;
    private final double MAX_DISTANCE = 3.0;


    @Autowired
    private OfficeService officeService;

    public ParkingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public List<ParkingDto> fetchParkingAreas(String officeId, LocalDate dateStart, LocalDate dateEnd) throws IOException {
        List<ParkingDto> parkingAreas = getAllParkingAreas();

        return getSuitableParkingAreas(officeId, parkingAreas, dateStart, dateEnd);
    }

    private List<ParkingDto> getAllParkingAreas() throws IOException {
        String endpoint = parklyApiUrl + "/parking-areas/page/0?size=99";

        ResponseEntity<String> response = restTemplate.getForEntity(endpoint, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            ParkingResponseDto parkingResponse = objectMapper.readValue(response.getBody(), ParkingResponseDto.class);
            return parkingResponse.content();
        } else {
            throw new RuntimeException("Failed to fetch all parking areas data, status code: " + response.getStatusCode());
        }
    }

    public List<ParkingDto> getSuitableParkingAreas(String officeId, List<ParkingDto> parkingAreas, LocalDate dateStart, LocalDate dateEnd) {
        OfficeDto officeDto = officeService.getOfficeById(officeId);

        return parkingAreas.stream()
                //.filter(parkingArea -> isAvailable(parkingArea, dateStart, dateEnd))
                .filter(parkingArea -> isWithinRadius(parkingArea, officeDto))
                .collect(Collectors.toList());
    }

    private boolean isWithinRadius(ParkingDto parkingArea, OfficeDto officeDto) {
        Double officeLatitude = officeDto.y();
        Double officeLongitude = officeDto.x();
        Double parkingLatitude = parkingArea.latitude();
        Double parkingLongitude = parkingArea.longitude();

        if(officeLatitude == null || officeLongitude == null || parkingLatitude == null || parkingLongitude == null) {
            return false;
        }

        double distance = calculateDistance(officeLatitude, officeLongitude, parkingLatitude, parkingLongitude);
        return distance <= MAX_DISTANCE;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    public List<ParkingSpotDto> getParkingSpots(Integer id) throws JsonProcessingException {
        String endpoint = parklyApiUrl + "/parking-spots/pa?paId=" + id;

        String responseBody = restTemplate.getForObject(endpoint, String.class);

        return objectMapper.readValue(responseBody, new TypeReference<List<ParkingSpotDto>>() {});
    }

    public ParkingSpotDto getFirstAvailableParkingSpot(Integer id) throws JsonProcessingException {
        List<ParkingSpotDto> parkingSpots = getParkingSpots(id);
        return parkingSpots.stream()
                .filter(ParkingSpotDto::isAvailable)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No available parking spots"));
    }

    public ParklyUserDto createOrLogin(ParklyUserRequestDto parklyUser) {
        String loginEndpoint = parklyApiUrl + "/users/login";
        String registerEndpoint = parklyApiUrl + "/users";

        try {
            // Attempt to log in the user
            LoginRequestDto loginRequest = new LoginRequestDto(parklyUser.username());
            Map<String, Integer> loginResponse = restTemplate.postForObject(loginEndpoint, loginRequest, Map.class);
            Integer userId = loginResponse.get("userId");
            return new ParklyUserDto(userId, parklyUser.username(), parklyUser.email(), parklyUser.firstName(), parklyUser.lastName(), parklyUser.role());
        } catch (Exception loginException) {
            try {
                // If login fails, attempt to register the user
                ResponseEntity<ParklyUserDto> registerResponse = restTemplate.postForEntity(registerEndpoint, parklyUser, ParklyUserDto.class);
                return registerResponse.getBody();
            } catch (Exception registerException) {
                // If registration fails, throw an error
                throw new RuntimeException("Failed to register user", registerException);
            }
        }
    }
}
