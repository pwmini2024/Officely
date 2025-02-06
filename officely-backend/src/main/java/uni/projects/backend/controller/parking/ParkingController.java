package uni.projects.backend.controller.parking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.projects.backend.controller.BaseController;
import uni.projects.backend.controller.paths.PATHS;
import uni.projects.backend.controller.paths.PATH_PREFIXES;
import uni.projects.backend.models.user.User;
import uni.projects.backend.services.api.ParkingReservationService;
import uni.projects.backend.services.api.ParkingService;
import uni.projects.backend.services.verification.UserVerificationService;
import uni.projects.backend.web.api.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping(path = ParkingController.PARKING_PATH)
public class ParkingController extends BaseController {
    static final String PARKING_PATH = PATH_PREFIXES.USER_PATH + PATHS.PARKING_PATH;

    public ParkingController() {
        super(new UserVerificationService(),
                new HashMap<>(){{
            put("parkingAreaId", Integer.class);
            put("startTime", LocalDate.class);
            put("endTime", LocalDate.class);
                }});
    }

    @Autowired
    private ParkingService parkingService;

    @Autowired
    private ParkingReservationService parkingReservationService;


    @GetMapping()
    public ResponseEntity<List<ParkingDto>> getParkingAreas(@RequestParam (required = true)  String officeId,
                                                            @RequestParam (required = true)  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateStart,
                                                            @RequestParam (required = true)  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateEnd,
                                                            @RequestHeader(value = "Authorization") String authorization) {
        User currentUser = getUser(authorization);
        try {
            return ResponseEntity.ok(parkingService.fetchParkingAreas(officeId, dateStart, dateEnd));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/parking-area/{id}")
    public ResponseEntity<ParkingSpotDto> getParkingSpot(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(parkingService.getFirstAvailableParkingSpot(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    private String generateUsername(User user) {
        return user.getName() + "_" + user.getSurname() + "_" + user.getId();
    }

    @PostMapping("/reservation")
    @ResponseBody
    public ParkingReservationDto reserveParkingSpot(@RequestBody Map<String, Object> reservationRequest,
                                                    @RequestHeader(value = "Authorization") String authorization) {

        User currentUser = getUser(authorization);

        String username = generateUsername(currentUser);
        ParklyUserRequestDto parklyUser = new ParklyUserRequestDto(username, currentUser.getEmail(), currentUser.getName(), currentUser.getSurname(), currentUser.getRole());

        try {

            Integer parkingAreaId = deserializeArgument(reservationRequest, "parkingAreaId");


            ParklyUserDto parklyUserDto = parkingService.createOrLogin(parklyUser);
            Integer parkingSpotId = getParkingSpot(parkingAreaId).getBody().id();
            Integer userId = parklyUserDto.id();
            LocalDate startTime = deserializeArgument(reservationRequest,"startTime");
            LocalDate endTime = deserializeArgument(reservationRequest,"endTime");

            return parkingReservationService.reserveParkingSpot(
                    parkingSpotId,
                    userId,
                    startTime,
                    endTime

            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to reserve parking spot");
        }
    }

    @DeleteMapping("/reservation")
    public ResponseEntity<Void> cancelParkingReservation(@RequestParam Integer id) {
        try {
            parkingReservationService.cancelParkingReservation(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
