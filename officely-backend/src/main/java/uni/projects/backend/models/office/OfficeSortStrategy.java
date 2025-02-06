package uni.projects.backend.models.office;

import org.springframework.data.domain.Sort;
import uni.projects.backend.models.SortStrategy;

import java.util.Map;
import java.util.function.Function;

public class OfficeSortStrategy implements SortStrategy {
    private static final Map<String, Function<Sort.Direction, Sort>> OFFICE_SORT_STRATEGIES = Map.of(
            "name", direction -> Sort.by(direction, "name"),
            "country", direction -> Sort.by(direction, "country"),
            "city", direction -> Sort.by(direction, "city")
    );

    @Override
    public Sort getSort(String sortBy, boolean ascending) {
        Function<Sort.Direction, Sort> function = OFFICE_SORT_STRATEGIES.get(sortBy);
        if(function == null) {
            throw new IllegalArgumentException("Invalid sort parameter");
        }
        return function.apply(ascending ? Sort.Direction.ASC : Sort.Direction.DESC);
    }
}
