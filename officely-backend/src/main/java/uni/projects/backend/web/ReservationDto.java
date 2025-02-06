package uni.projects.backend.web;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import uni.projects.backend.models.reservation.PaymentType;
import uni.projects.backend.models.reservation.Reservation;
import uni.projects.backend.models.reservation.ReservationStatus;

import java.time.LocalDate;


public record ReservationDto(
        @Schema(description = "The ID of the reservation (should be left empty when created, will be returned but ignored if user passes it)", example = "1")
        String id,

        @Schema(description = "The office details (should be left empty when created, will be returned but ignored if user passes it)")
        OfficeDto officeDto,

        @Schema(description = "The start time of the reservation", example = "2023-01-01")
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate startTime,

        @Schema(description = "The end time of the reservation", example = "2023-01-10")
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate endTime,

        @Schema(description = "The booking date (should be left empty when created, will be returned but ignored if user passes it)", example = "2023-01-01")
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate bookedAt,

        @Schema(description = "The duration of the reservation in days (should be left empty when created, will be returned but ignored if user passes it)", example = "10")
        Long duration,

        @Schema(description = "The total price of the reservation (should be left empty when created, will be returned but ignored if user passes it)", example = "100.0")
        Double totalPrice,

        @Schema(type = "Enum", types = {"CANCELLED", "PENDING", "ONGOING", "COMPLETED"}, description = "The status of the reservation (should be left empty when created, will be returned but ignored if user passes it)", example = "PENDING")
        ReservationStatus status,

        @Schema(type = "Enum", types = {"CASH", "CARD", "TRANSFER", "BLIK"}, description = "The payment type for the reservation", example = "CARD")
        PaymentType paymentType,

        @Schema(description = "The price per day (should be left empty when created, will be returned but ignored if user passes it)", example = "10.0")
        double pricePerDay,

        @Schema(description = "The price multiplier (should be left empty when created, will be returned but ignored if user passes it)", example = "1.0")
        double priceMultiplier,

        @Schema(description = "Additional comments for the reservation", example = "No special requests")
        String comments,

        @Schema(description = "Whether the reservation is paid (should be left empty when created, will be returned but ignored if user passes it)", example = "false")
        boolean paid,

        @Schema(description = "The payment date (should be left empty when created, will be returned but ignored if user passes it)", example = "2023-01-01")
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate paidAt
) {

    public static ReservationDto valueFrom(Reservation reservation) {
        return new ReservationDto(
                reservation.getId(),
                OfficeDto.valueFrom(reservation.getOffice()),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getBookedAt(),
                reservation.getDuration(),
                reservation.getTotalPrice(),
                reservation.getStatus(),
                reservation.getPaymentType(),
                reservation.getPricePerDay(),
                reservation.getPriceMultiplier(),
                reservation.getComments(),
                reservation.isPaid(),
                reservation.getPaidAt()
        );
    }

    public static Reservation convertTo(ReservationDto reservationDto) {
        Reservation reservation = new Reservation();
        reservation.setOffice(OfficeDto.convertTo(reservationDto.officeDto()));
        reservation.setStartTime(reservationDto.startTime());
        reservation.setEndTime(reservationDto.endTime());
        reservation.setStatus(reservationDto.status());
        reservation.setPaymentType(reservationDto.paymentType());
        reservation.setComments(reservationDto.comments());
        return reservation;
    }

}
