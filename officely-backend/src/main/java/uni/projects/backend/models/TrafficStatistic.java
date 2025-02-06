package uni.projects.backend.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
public class TrafficStatistic {

    @Id
    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Column(nullable = false)
    private int visitors;

    @Column(nullable = false)
    private boolean deleted;
}
