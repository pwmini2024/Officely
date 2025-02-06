package uni.projects.backend.models.reservation;


import org.springframework.data.domain.Sort;
import uni.projects.backend.models.SortStrategy;

import java.util.Map;
import java.util.function.Function;

public class ReservationSortStrategy implements SortStrategy {

    private static final Map<String, Function<Sort.Direction, Sort>> RESERVATION_SORT_STRATEGIES = Map.of(
            "duration", direction -> Sort.by(direction, "duration"),
            "date", direction -> Sort.by(direction, "startTime"),
            "priceTotal", direction -> Sort.by(direction, "totalPrice"),
            "pricePerDay", direction -> Sort.by(direction, "pricePerDay"),
            "paymentType", direction -> Sort.by(direction, "paymentType"),
            "status", direction -> Sort.by(direction, "status"),
            "paid", direction -> Sort.by(direction, "paid"),
            "bookedAt", direction -> Sort.by(direction, "bookedAt"),
            "city", direction -> Sort.by(direction, "city")
    );

    @Override
    public Sort getSort(String sortBy, boolean ascending) {
        Function<Sort.Direction, Sort> function = RESERVATION_SORT_STRATEGIES.get(sortBy);
        if(function == null) {
            throw new IllegalArgumentException("Invalid sort parameter");
        }
        return function.apply(ascending ? Sort.Direction.ASC : Sort.Direction.DESC);
    }
}
