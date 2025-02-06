package uni.projects.backend.models.office;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.backend.models.user.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Office {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double metricArea;

    @Column(nullable = false)
    private int floor;

    @Column(nullable = false)
    private int roomNumber;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private double x; //lon

    @Column(nullable = false)
    private double y; //lat

    @Column(nullable = false)
    private double price;

    @ManyToMany
    @JoinTable(
            name = "office_amenity",
            joinColumns = @JoinColumn(name = "office_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    @Column
    private List<Amenity> amenities = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "office_image",
            joinColumns = @JoinColumn(name = "office_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    @Column
    private List<Image> images = new ArrayList<>();

    @Column(nullable = false)
    private boolean deleted = false;

    @ManyToOne(optional = false)
    private User owner;

    public String getFullAddress() {
        return address + ", " + postalCode + " " + city + ", " + country;
    }
}
