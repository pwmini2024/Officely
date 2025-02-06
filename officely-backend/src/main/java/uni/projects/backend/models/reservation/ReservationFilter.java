package uni.projects.backend.models.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationFilter {

    private Boolean paid;
    private Double priceTotalMin;
    private Double priceTotalMax;
    private Double pricePerDayMin;
    private Double pricePerDayMax;
    private String paymentType;
    private String status;
    private LocalDate bookedAtFrom;
    private LocalDate bookedAtTo;
    private LocalDate startTimeFrom;
    private LocalDate startTimeTo;
    private LocalDate endTimeFrom;
    private LocalDate endTimeTo;

}
