package uni.projects.backend.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uni.projects.backend.models.office.Office;
import uni.projects.backend.models.office.OfficeFilter;
import uni.projects.backend.models.reservation.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OfficeRepository extends JpaRepository<Office, String> {
    Optional<List<Office>> findAllByName(String name);
    boolean existsByAddressAndFloor(String address, int floor);
    boolean existsByXAndY(Double x, Double y);

    @Query("SELECT o FROM Office o WHERE o.id NOT IN (" +
            "SELECT r.office.id FROM Reservation r " +
            "WHERE r.status != 'CANCELLED' " +
            "AND ((r.startTime <= :endTime AND r.endTime >= :startTime)))")
    List<Office> findAvailableOffices(@Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime);

    @Query("SELECT r FROM Reservation r " +
            "WHERE r.office.id = :officeId " +
            "AND r.status != 'CANCELLED' " +
            "AND ((r.startTime <= :endOfMonth AND r.endTime >= :startOfMonth))")
    List<Reservation> findReservationsByOfficeIdAndDateRange(@Param("officeId") String officeId,
                                                             @Param("startOfMonth") LocalDate startOfMonth,
                                                             @Param("endOfMonth") LocalDate endOfMonth);

    @Query("SELECT o FROM Office o WHERE " +
            "o.deleted = false AND " +
            "(:#{#officeFilter.availableFrom} IS NULL OR o.id NOT IN (" +
            "  SELECT r.office.id FROM Reservation r " +
            "  WHERE r.status != 'CANCELLED' " +
            "  AND ((r.startTime <= :startDate AND r.endTime >= :endDate)))) " +
            "AND (:#{#officeFilter.pricePerDayMin} IS NULL OR o.price >= :#{#officeFilter.pricePerDayMin}) " +
            "AND (:#{#officeFilter.pricePerDayMax} IS NULL OR o.price <= :#{#officeFilter.pricePerDayMax}) " +
            "AND (:#{#officeFilter.areaMin} IS NULL OR o.metricArea >= :#{#officeFilter.areaMin}) " +
            "AND (:#{#officeFilter.areaMax} IS NULL OR o.metricArea <= :#{#officeFilter.areaMax}) " +
            "AND (:#{#officeFilter.country} IS NULL OR o.country = :#{#officeFilter.country}) " +
            "AND (:#{#officeFilter.city} IS NULL OR o.city = :#{#officeFilter.city}) " +
            "AND (:#{#officeFilter.postalCode} IS NULL OR o.postalCode = :#{#officeFilter.postalCode}) " +
            "AND (:#{#officeFilter.address} IS NULL OR o.address LIKE %:#{#officeFilter.address}%) " +
            "AND (:#{#officeFilter.x} IS NULL OR :#{#officeFilter.y} IS NULL OR :#{#officeFilter.distance} IS NULL OR " +
            "(6371 * ACOS(COS(RADIANS(:#{#officeFilter.x})) * COS(RADIANS(o.x)) * " +
            "COS(RADIANS(o.y) - RADIANS(:#{#officeFilter.y})) + SIN(RADIANS(:#{#officeFilter.x})) * SIN(RADIANS(o.x)))) <= :#{#officeFilter.distance})")
    List<Office> findAvailableOffices(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("officeFilter") OfficeFilter officeFilter, Pageable pageable);


}
