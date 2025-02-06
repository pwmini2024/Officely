package uni.projects.backend.services.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uni.projects.backend.web.api.ParkingReservationDto;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ParkingReservationService {

    @Value("${parkly.api.url}")
    private String parklyApiUrl;

    private final RestTemplate restTemplate;

    public ParkingReservationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ParkingReservationDto reserveParkingSpot(Integer parkingSpotId, Integer userId, LocalDate startTime, LocalDate endTime) {
        String endpoint = parklyApiUrl + "/reservations";

        LocalDateTime startDateTime = startTime.atStartOfDay();
        LocalDateTime endDateTime = endTime.atStartOfDay();

        ParkingReservationDto reservation = new ParkingReservationDto(
                parkingSpotId,
                userId,
                startDateTime,
                endDateTime,
                0.0
        );
        return restTemplate.postForObject(endpoint, reservation, ParkingReservationDto.class);
    }

    public void cancelParkingReservation(Integer reservationId) {
        String endpoint = parklyApiUrl + "/reservations/" + reservationId;
        restTemplate.delete(endpoint);
    }


}
