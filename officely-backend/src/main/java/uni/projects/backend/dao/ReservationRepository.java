package uni.projects.backend.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uni.projects.backend.models.user.User;
import uni.projects.backend.models.reservation.Reservation;
import uni.projects.backend.models.reservation.ReservationFilter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

    List<Reservation> findAllByUser(User user, Sort sort);

    Optional<Reservation> findByIdAndUser(String id, User user);

    @Query("SELECT r FROM Reservation r WHERE r.user = :user " +
            "AND (:#{#filter.paid} IS NULL OR r.paid = :#{#filter.paid}) " +
            "AND (:#{#filter.priceTotalMin} IS NULL OR r.pricePerDay * (r.endTime - r.startTime) >= :#{#filter.priceTotalMin}) " +
            "AND (:#{#filter.priceTotalMax} IS NULL OR r.pricePerDay * (r.endTime - r.startTime) <= :#{#filter.priceTotalMax}) " +
            "AND (:#{#filter.pricePerDayMin} IS NULL OR r.pricePerDay >= :#{#filter.pricePerDayMin}) " +
            "AND (:#{#filter.pricePerDayMax} IS NULL OR r.pricePerDay <= :#{#filter.pricePerDayMax}) " +
            "AND (:#{#filter.paymentType} IS NULL OR r.paymentType = :#{#filter.paymentType}) " +
            "AND (:#{#filter.status} IS NULL OR r.status = :#{#filter.status}) " +
            "AND (:#{#filter.bookedAtFrom} IS NULL OR r.bookedAt >= :#{#filter.bookedAtFrom}) " +
            "AND (:#{#filter.bookedAtTo} IS NULL OR r.bookedAt <= :#{#filter.bookedAtTo}) " +
            "AND (:#{#filter.startTimeFrom} IS NULL OR r.startTime >= :#{#filter.startTimeFrom}) " +
            "AND (:#{#filter.startTimeTo} IS NULL OR r.startTime <= :#{#filter.startTimeTo}) " +
            "AND (:#{#filter.endTimeFrom} IS NULL OR r.endTime >= :#{#filter.endTimeFrom}) " +
            "AND (:#{#filter.endTimeTo} IS NULL OR r.endTime <= :#{#filter.endTimeTo}) ")
    List<Reservation> findAllByUserWithFilters(@Param("user") User user, Pageable pageable, @Param("filter") ReservationFilter filter);

    @Query("SELECT r FROM Reservation r WHERE " +
            "(:#{#filter.paid} IS NULL OR r.paid = :#{#filter.paid}) " +
            "AND (:#{#filter.priceTotalMin} IS NULL OR r.pricePerDay * (r.endTime - r.startTime) >= :#{#filter.priceTotalMin}) " +
            "AND (:#{#filter.priceTotalMax} IS NULL OR r.pricePerDay * (r.endTime - r.startTime) <= :#{#filter.priceTotalMax}) " +
            "AND (:#{#filter.pricePerDayMin} IS NULL OR r.pricePerDay >= :#{#filter.pricePerDayMin}) " +
            "AND (:#{#filter.pricePerDayMax} IS NULL OR r.pricePerDay <= :#{#filter.pricePerDayMax}) " +
            "AND (:#{#filter.paymentType} IS NULL OR r.paymentType = :#{#filter.paymentType}) " +
            "AND (:#{#filter.status} IS NULL OR r.status = :#{#filter.status}) " +
            "AND (:#{#filter.bookedAtFrom} IS NULL OR r.bookedAt >= :#{#filter.bookedAtFrom}) " +
            "AND (:#{#filter.bookedAtTo} IS NULL OR r.bookedAt <= :#{#filter.bookedAtTo}) " +
            "AND (:#{#filter.startTimeFrom} IS NULL OR r.startTime >= :#{#filter.startTimeFrom}) " +
            "AND (:#{#filter.startTimeTo} IS NULL OR r.startTime <= :#{#filter.startTimeTo}) " +
            "AND (:#{#filter.endTimeFrom} IS NULL OR r.endTime >= :#{#filter.endTimeFrom}) " +
            "AND (:#{#filter.endTimeTo} IS NULL OR r.endTime <= :#{#filter.endTimeTo}) ")
    List<Reservation> findAllWithFilters(Pageable pageable, @Param("filter") ReservationFilter filter);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r " +
            "WHERE r.office.id = :officeId " +
            "AND r.status != 'CANCELLED' " +
            "AND ((r.startTime <= :endTime AND r.endTime >= :startTime))")
    boolean existsOverlappingReservation(@Param("officeId") String officeId,
                                         @Param("startTime") LocalDate startTime,
                                         @Param("endTime") LocalDate endTime);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r " +
            "WHERE r.office.id = :officeId " +
            "AND r.status != 'CANCELLED' " +
            "AND r.id != :reservationId " +
            "AND ((r.startTime <= :endTime AND r.endTime >= :startTime))")
    boolean existsOverlappingReservationExcluding(@Param("officeId") String officeId,
                                                  @Param("reservationId") String reservationId,
                                                  @Param("startTime") LocalDate startTime,
                                                  @Param("endTime") LocalDate endTime);

    @Query("SELECT r FROM Reservation r " +
            "WHERE r.office.id = :officeId " +
            "AND r.status != 'CANCELLED' " +
            "AND r.id != :reservationId " +
            "AND ((r.startTime <= :endTime AND r.endTime >= :startTime))")
    List<Reservation> findOverlappingReservationsExcluding(@Param("officeId") String officeId,
                                                           @Param("reservationId") String reservationId,
                                                           @Param("startTime") LocalDate startTime,
                                                           @Param("endTime") LocalDate endTime);

}
