package uni.projects.backend.controller.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.projects.backend.controller.BaseController;
import uni.projects.backend.controller.paths.PATHS;
import uni.projects.backend.controller.paths.PATH_PREFIXES;
import uni.projects.backend.models.reservation.ReservationFilter;
import uni.projects.backend.models.user.User;
import uni.projects.backend.services.ReservationService;
import uni.projects.backend.services.verification.AdminVerificationService;
import uni.projects.backend.services.verification.VerificationService;
import uni.projects.backend.web.ReservationDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uni.projects.backend.controller.reservation.ReservationAdminController.RESERVATION_PATH;

@RestController
@RequestMapping(RESERVATION_PATH)
public class ReservationAdminController extends BaseController {

    public static final String RESERVATION_PATH = PATH_PREFIXES.ADMIN_PATH + PATHS.RESERVATIONS_PATH;

    public ReservationAdminController() {
        super(new AdminVerificationService(),
                new HashMap<>() {{

                }});
    }

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationDto>> getReservations(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "true") boolean ascending,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @ModelAttribute ReservationFilter filter,
            @RequestHeader("Authorization") String authorization) {

        if (page == null) {
            page = 0;
        }
        if (pageSize == null) {
            pageSize = 10;
        }

        User currentUser = getUser(authorization);

        List<ReservationDto> reservations = reservationService.getAdminReservations(currentUser, sortBy, ascending, filter, page, pageSize);

        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getReservation(@PathVariable String id,
                                                         @RequestHeader("Authorization") String authorization) {

        User currentUser = getUser(authorization);

        ReservationDto reservation = reservationService.getAdminReservation(id, currentUser);

        return ResponseEntity.ok(reservation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationDto> updateReservation(@PathVariable String id,
                                                            @RequestBody(required = false) Map<String, Object> body,
                                                            @RequestHeader("Authorization") String authorization) {

        User currentUser = getUser(authorization);

        ReservationDto reservationDto = deserializeArgument(body, "reservation");

        ReservationDto reservation = reservationService.updateAdminReservation(id, reservationDto, currentUser);
        return ResponseEntity.ok(reservation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ReservationDto> deleteReservation(@PathVariable String id,
                                                            @RequestHeader("Authorization") String authorization) {

        User currentUser = getUser(authorization);

        ReservationDto reservation = reservationService.cancelAdminReservation(id, currentUser);

        return ResponseEntity.ok(reservation);
    }
}
