package uni.projects.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uni.projects.backend.dao.OfficeRepository;
import uni.projects.backend.dao.ReservationRepository;
import uni.projects.backend.exceptions.ArgumentException;
import uni.projects.backend.exceptions.ResourceNotFoundException;
import uni.projects.backend.models.SortStrategy;
import uni.projects.backend.models.office.Office;
import uni.projects.backend.models.reservation.*;
import uni.projects.backend.models.user.User;
import uni.projects.backend.web.ReservationDto;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private OfficeRepository officeRepository;

    @Autowired
    private TrafficStatisticService trafficStatisticService;

    public ReservationDto createReservation(ReservationDto reservationDto, User user, String officeId) {

        Office office = officeRepository.findById(officeId).orElseThrow( () -> new RuntimeException("Office not found"));
        if (office.isDeleted()) {
            throw new ArgumentException("Office is deleted");
        }
        if (reservationDto.startTime() == null || reservationDto.endTime() == null) {
            throw new ArgumentException("Start time and end time must be set");
        }
        if (reservationDto.paymentType() == null) {
            throw new ArgumentException("Payment type must be set");
        }
        if(reservationDto.startTime().isBefore(LocalDate.now()) || reservationDto.endTime().isBefore(LocalDate.now())) {
            throw new ArgumentException("Reservation cannot be in the past");
        }
        if(reservationDto.startTime().isAfter(reservationDto.endTime())) {
            throw new ArgumentException("Start time cannot be after end time");
        }
        if (reservationDto.startTime().isEqual(reservationDto.endTime())) {
            throw new ArgumentException("Start time cannot be equal to end time");
        }
        if (reservationRepository.existsOverlappingReservation(officeId, reservationDto.startTime(), reservationDto.endTime())) {
            throw new ArgumentException("Reservation overlaps with another reservation");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setOffice(office);
        reservation.setStartTime(reservationDto.startTime());
        reservation.setEndTime(reservationDto.endTime());
        reservation.setBookedAt(LocalDate.now());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setPaymentType(reservationDto.paymentType());
        double priceMultiplier = trafficStatisticService.calculateMultiplier(reservationDto.startTime(), reservationDto.endTime());
        reservation.setPriceMultiplier(priceMultiplier);
        reservation.setPricePerDay(office.getPrice());
        reservation.setComments(reservationDto.comments());
        if (reservationDto.comments() == null) {
            reservation.setComments("");
        }
        reservation.setPaid(false);

        Reservation savedReservation = reservationRepository.save(reservation);
        if (savedReservation.getId() == null) {
            throw new RuntimeException("Failed to save reservation");
        }

        return ReservationDto.valueFrom(reservation);
    }

    public List<ReservationDto> getReservations(User user,
                                                String sortBy,
                                                boolean ascending,
                                                ReservationFilter filter,
                                                Integer page,
                                                Integer pageSize) {
        Sort sort = Sort.unsorted();

        if (sortBy != null) {
            SortStrategy sortStrategy = new ReservationSortStrategy();
            sort = sortStrategy.getSort(sortBy, ascending);
        }

        PageRequest pageRequest = PageRequest.of(page, pageSize, sort);

        List<Reservation> reservations = reservationRepository.findAllByUserWithFilters(user, pageRequest, filter);
        return reservations.stream().map(ReservationDto::valueFrom).toList();
    }

    public ReservationDto getReservation(String id, User user) {
        return ReservationDto.valueFrom(reservationRepository.findByIdAndUser(id, user).orElseThrow(
                () -> new ResourceNotFoundException("Reservation not found"))
        );
    }

    public ReservationDto cancelReservation(String id, User user) {
        Reservation reservation = reservationRepository.findByIdAndUser(id, user).orElseThrow(
                () -> new ResourceNotFoundException("Reservation not found")
        );

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ArgumentException("Reservation is already cancelled");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation savedReservation = reservationRepository.save(reservation);
        if (savedReservation.getId() == null) {
            throw new RuntimeException("Failed to save reservation");
        }
        return ReservationDto.valueFrom(reservation);
    }

    public ReservationDto payReservation(String id, User user) {

        Reservation reservation = reservationRepository.findByIdAndUser(id, user).orElseThrow(
                () -> new ResourceNotFoundException("Reservation not found")
        );

        if (reservation.isPaid()) {
            throw new ArgumentException("Reservation is already paid");
        }
        if (reservation.getPaymentType() == PaymentType.CASH) {
            throw new ArgumentException("You cannot pay online for cash reservations");
        }
        if(reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ArgumentException("Reservation must be pending to be paid");
        }
        reservation.setPaid(true);
        reservation.setPaidAt(LocalDate.now());

        Reservation savedReservation = reservationRepository.save(reservation);
        if (savedReservation.getId() == null) {
            throw new RuntimeException("Failed to save reservation");
        }

        return ReservationDto.valueFrom(reservation);
    }

    public ReservationDto updateReservation(String id, ReservationDto reservationDto, User user) {

        Reservation reservation = reservationRepository.findByIdAndUser(id, user).orElseThrow(
                () -> new ResourceNotFoundException("Reservation not found")
        );

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ArgumentException("Reservation must be pending to be updated");
        }

        LocalDate newStartTime = reservationDto.startTime() != null ? reservationDto.startTime() : reservation.getStartTime();
        LocalDate newEndTime = reservationDto.endTime() != null ? reservationDto.endTime() : reservation.getEndTime();

        if (newStartTime.isBefore(LocalDate.now()) || newEndTime.isBefore(LocalDate.now())) {
            throw new ArgumentException("Reservation dates cannot be in the past");
        }

        if (newStartTime.isAfter(newEndTime)) {
            throw new ArgumentException("Start time cannot be after end time");
        }

        if (!newStartTime.equals(reservation.getStartTime()) || !newEndTime.equals(reservation.getEndTime())) {
            if (reservationRepository.existsOverlappingReservationExcluding(reservation.getOffice().getId(), reservation.getId(), newStartTime, newEndTime)) {
                throw new ArgumentException("Updated reservation dates overlap with another reservation");
            }
        }

        if (reservationDto.startTime() != null) {
            reservation.setStartTime(reservationDto.startTime());
        }
        if (reservationDto.endTime() != null) {
            reservation.setEndTime(reservationDto.endTime());
        }
        if (reservationDto.comments() != null) {
            reservation.setComments(reservationDto.comments());
        }

        Reservation savedReservation = reservationRepository.save(reservation);
        if (savedReservation.getId() == null) {
            throw new RuntimeException("Failed to save reservation");
        }

        return ReservationDto.valueFrom(reservation);
    }

    public List<ReservationDto> getAdminReservations(User currentUser, String sortBy, boolean ascending, ReservationFilter filter, Integer page, Integer pageSize) {
        Sort sort = Sort.unsorted();

        if (sortBy != null) {
            SortStrategy sortStrategy = new ReservationSortStrategy();
            sort = sortStrategy.getSort(sortBy, ascending);
        }

        PageRequest pageRequest = PageRequest.of(page, pageSize, sort);

        List<Reservation> reservations = reservationRepository.findAllWithFilters(pageRequest, filter);
        return reservations.stream().map(ReservationDto::valueFrom).toList();
    }

    public ReservationDto getAdminReservation(String id, User currentUser) {
        return ReservationDto.valueFrom(reservationRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Reservation not found"))
        );
    }

    public ReservationDto updateAdminReservation(String id, ReservationDto reservationDto, User currentUser) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Reservation not found")
        );

        LocalDate newStartTime = reservationDto.startTime() != null ? reservationDto.startTime() : reservation.getStartTime();
        LocalDate newEndTime = reservationDto.endTime() != null ? reservationDto.endTime() : reservation.getEndTime();

        if (newStartTime.isBefore(LocalDate.now()) || newEndTime.isBefore(LocalDate.now())) {
            throw new ArgumentException("Reservation dates cannot be in the past");
        }

        if (newStartTime.isAfter(newEndTime)) {
            throw new ArgumentException("Start time cannot be after end time");
        }

        if (!newStartTime.equals(reservation.getStartTime()) || !newEndTime.equals(reservation.getEndTime())) {
            if (reservationRepository.existsOverlappingReservationExcluding(reservation.getOffice().getId(), reservation.getId(), newStartTime, newEndTime)) {
                throw new ArgumentException("Updated reservation dates overlap with another reservation");
            }
        }

        if (reservationDto.startTime() != null) {
            reservation.setStartTime(reservationDto.startTime());
        }
        if (reservationDto.endTime() != null) {
            reservation.setEndTime(reservationDto.endTime());
        }
        if (reservationDto.comments() != null) {
            reservation.setComments(reservationDto.comments());
        }

        Reservation savedReservation = reservationRepository.save(reservation);
        if (savedReservation.getId() == null) {
            throw new RuntimeException("Failed to save reservation");
        }

        return ReservationDto.valueFrom(reservation);
    }

    public ReservationDto cancelAdminReservation(String id, User currentUser) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Reservation not found")
        );

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ArgumentException("Reservation is already cancelled");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation savedReservation = reservationRepository.save(reservation);
        if (savedReservation.getId() == null) {
            throw new RuntimeException("Failed to save reservation");
        }
        return ReservationDto.valueFrom(reservation);
    }
}
