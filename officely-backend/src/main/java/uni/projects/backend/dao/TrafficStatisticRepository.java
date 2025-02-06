package uni.projects.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uni.projects.backend.models.TrafficStatistic;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrafficStatisticRepository extends JpaRepository<TrafficStatistic, String> {

    List<TrafficStatistic> findAllByDateBetween(LocalDate dateAfter, LocalDate dateBefore);
    Optional<TrafficStatistic> findByDate(LocalDate date);

    @Query("SELECT AVG(ts.visitors) FROM TrafficStatistic ts WHERE ts.date >= :startDate")
    Double calculateAverageVisitorsFromDate(@Param("startDate") LocalDate startDate);

    @Query("SELECT STDDEV(ts.visitors) FROM TrafficStatistic ts WHERE ts.date >= :startDate")
    Double calculateStandardDeviationFromDate(@Param("startDate") LocalDate startDate);

    @Query("SELECT ts.visitors FROM TrafficStatistic ts WHERE ts.date = :date")
    Optional<Integer> findVisitorsByDate(@Param("date") LocalDate date);

    @Query("SELECT MAX(ts.visitors) FROM TrafficStatistic ts WHERE ts.date BETWEEN :startDate AND :endDate")
    Optional<Integer> maxVisitorsBetweenDates(LocalDate startDate, LocalDate endDate);
}
