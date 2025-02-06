package uni.projects.backend.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uni.projects.backend.dao.TrafficStatisticRepository;
import uni.projects.backend.models.TrafficStatistic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrafficStatisticService {

    @Autowired
    private TrafficStatisticRepository trafficStatisticRepository;

    private final double IMPACT = 0.5;
    private final double MIN_MULTIPLIER = 0.5;


    public Double calculateMultiplier(LocalDate startDate, LocalDate endDate) {
        List<TrafficStatistic> trafficStatistics = trafficStatisticRepository.findAllByDateBetween(startDate, endDate);
        if (trafficStatistics.isEmpty()) {
            return 1.0;
        }

        Double averageSearches = trafficStatisticRepository.calculateAverageVisitorsFromDate(LocalDate.now());
        Double standardDeviation = trafficStatisticRepository.calculateStandardDeviationFromDate(LocalDate.now());

        if (averageSearches == null || standardDeviation == null || standardDeviation == 0) {
            return 1.0;
        }


        Optional<Integer> searchesForEndDateOpt = trafficStatisticRepository.maxVisitorsBetweenDates(startDate, endDate);
        if (searchesForEndDateOpt.isEmpty()) {
            return 1.0;
        }
        int searchesForEndDate = searchesForEndDateOpt.get();

        return Math.max(1 + ((searchesForEndDate - averageSearches) / standardDeviation) * IMPACT, MIN_MULTIPLIER);
    }


    public void incrementVisitorsBetweenDates(LocalDate startDate, LocalDate endDate) {
        List<TrafficStatistic> trafficStatistics = trafficStatisticRepository.findAllByDateBetween(startDate, endDate);
        Map<LocalDate, TrafficStatistic> statisticsMap = trafficStatistics.stream()
                .collect(Collectors.toMap(TrafficStatistic::getDate, stat -> stat));

        List<TrafficStatistic> updatedStatistics = new ArrayList<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            TrafficStatistic trafficStatistic = statisticsMap.get(currentDate);

            if (trafficStatistic == null) {
                trafficStatistic = new TrafficStatistic();
                trafficStatistic.setDate(currentDate);
                trafficStatistic.setVisitors(1);
                trafficStatistic.setDeleted(false);
            } else {
                trafficStatistic.setVisitors(trafficStatistic.getVisitors() + 1);
            }

            updatedStatistics.add(trafficStatistic);

            currentDate = currentDate.plusDays(1);
        }

        trafficStatisticRepository.saveAll(updatedStatistics);
    }


}
