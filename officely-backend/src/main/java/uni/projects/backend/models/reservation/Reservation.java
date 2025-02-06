package uni.projects.backend.models.reservation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.backend.models.office.Office;
import uni.projects.backend.models.user.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "office_id")
    private Office office;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDate startTime;

    @Column(nullable = false)
    private LocalDate endTime;

    @Column(nullable = false)
    private LocalDate bookedAt;

    @Column(nullable = false)
    private double pricePerDay;

    @Column(nullable = false)
    private double priceMultiplier;

    @Column(nullable = false)
    private double totalPrice; // Total price based on duration and pricePerDay

    @Column(nullable = false)
    private long duration; // Duration in days

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(nullable = false)
    private String comments;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(nullable = false)
    private boolean paid;

    @Column(nullable = true)
    private LocalDate paidAt;

    @Column(nullable = false)
    private boolean deleted;

    public void setStartTime(LocalDate startTime) {
        this.startTime = startTime;
        recalculateDurationAndTotalPrice();
    }

    public void setEndTime(LocalDate endTime) {
        this.endTime = endTime;
        recalculateDurationAndTotalPrice();
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
        recalculateTotalPrice();
    }

    // Method to recalculate both duration and total price
    private void recalculateDurationAndTotalPrice() {
        recalculateDuration();
        recalculateTotalPrice();
    }

    // Method to recalculate duration
    private void recalculateDuration() {
        if (startTime != null && endTime != null) {
            this.duration = ChronoUnit.DAYS.between(startTime, endTime);
        } else {
            this.duration = 0;
        }
    }

    // Method to recalculate total price
    private void recalculateTotalPrice() {
        if (duration > 0 && pricePerDay > 0) {
            double calculatedPrice = duration * pricePerDay * priceMultiplier;
            this.totalPrice = Math.round(calculatedPrice * 100.0) / 100.0; // Round to 2 decimal places
        } else {
            this.totalPrice = 0.0;
        }
    }
}

