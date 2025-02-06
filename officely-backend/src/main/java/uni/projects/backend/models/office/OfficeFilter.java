package uni.projects.backend.models.office;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfficeFilter {

    private LocalDate availableFrom;
    private LocalDate availableTo;

    private Double pricePerDayMin;
    private Double pricePerDayMax;

    private Double areaMin;
    private Double areaMax;

    private String country;
    private String city;
    private String postalCode;
    private String address;

    private Double x;
    private Double y;
    private Double distance; //in km

}
