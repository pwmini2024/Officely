package uni.projects.backend.models;


import org.springframework.data.domain.Sort;

public interface SortStrategy {
    Sort getSort(String sortBy, boolean ascending);
}
