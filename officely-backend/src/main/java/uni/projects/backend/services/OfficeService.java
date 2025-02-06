package uni.projects.backend.services;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uni.projects.backend.dao.OfficeRepository;
import uni.projects.backend.exceptions.ArgumentException;
import uni.projects.backend.exceptions.ResourceNotFoundException;
import uni.projects.backend.models.SortStrategy;
import uni.projects.backend.models.office.*;
import uni.projects.backend.models.reservation.Reservation;
import uni.projects.backend.models.reservation.ReservationStatus;
import uni.projects.backend.models.user.User;
import uni.projects.backend.services.geocoding.GeocodingService;
import uni.projects.backend.services.geocoding.Location;
import uni.projects.backend.web.AmenityDto;
import uni.projects.backend.web.ImageDto;
import uni.projects.backend.web.OfficeDto;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OfficeService {
    private final OfficeRepository officeRepository;

    @Autowired
    private AmenityService amenityService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private TrafficStatisticService trafficStatisticService;

    public OfficeService(OfficeRepository officeRepository) {
        this.officeRepository = officeRepository;
    }

    @SneakyThrows
    public Office createOffice(OfficeDto office, User user) {
        if(office.name() == null || office.metricArea() == null || office.floor() == null
                || office.roomNumber() == null || office.country() == null || office.city() == null
                || office.postalCode() == null || office.x() == null || office.y() == null
                || office.name().isEmpty() || office.price() == null || office.address() == null
                || office.amenities() == null || office.images() == null) {
            log.error(office.toString());
            System.out.println(office.toString());
            throw new IllegalArgumentException("All attributes are required");
        }

        if(officeRepository.existsByXAndY(office.x(), office.y())
                && officeRepository.existsByAddressAndFloor(office.address(), office.floor())) {
            throw new ArgumentException("Office already exists");
        }

        Office newOffice = new Office();
        newOffice.setName(office.name());
        newOffice.setMetricArea(office.metricArea());
        newOffice.setFloor(office.floor());
        newOffice.setRoomNumber(office.roomNumber());
        newOffice.setCountry(office.country());
        newOffice.setCity(office.city());
        newOffice.setPostalCode(office.postalCode());
        newOffice.setAddress(office.address());
        Location location = geocodingService.geocode(newOffice.getFullAddress()).get();
        newOffice.setX(location.getLongitude());
        newOffice.setY(location.getLatitude());
        newOffice.setPrice(office.price());
        List<Amenity> amenities = amenityService.getAmenitiesByIds(office.amenities().stream().map(AmenityDto::id).toList());
        newOffice.setAmenities(amenities);
        List<Image> images = imageService.getImagesByIds(office.images().stream().map(ImageDto::id).toList());
        newOffice.setImages(images);
        newOffice.setOwner(user);
        return officeRepository.save(newOffice);
    }

    @SneakyThrows
    public OfficeDto updateOffice(String officeId, OfficeDto officeDto) {

        Optional<Office> office = officeRepository.findById(officeId);
        if(office.isEmpty()) {
            throw new ResourceNotFoundException("Office with id " + officeId + " not found");
        }

        Office updatedOffice = OfficeDto.convertTo(officeDto);
        updatedOffice.setId(officeId);
        updatedOffice.setOwner(office.get().getOwner());
        Location location = geocodingService.geocode(updatedOffice.getFullAddress()).get();
        updatedOffice.setX(location.getLongitude());
        updatedOffice.setY(location.getLatitude());
        return OfficeDto.valueFrom(officeRepository.save(updatedOffice));
    }

    public boolean deleteOffice(String officeId) {
        Optional<Office> office = officeRepository.findById(officeId);

        if(office.isEmpty()) {
            throw new ResourceNotFoundException("Office with id" + officeId + "doesn't exist.");
        }

        office.get().setDeleted(true);
        officeRepository.save(office.get());
        return office.get().isDeleted();
    }

    public List<OfficeDto> findAvailableOfficesAndFilter(LocalDate dateStart, LocalDate dateEnd, OfficeFilter officeFilter,
                                                         String sortBy, boolean ascending, int page, int pageSize) {
        Sort sort = Sort.unsorted();

        Double priceMultiplier = trafficStatisticService.calculateMultiplier(dateStart, dateEnd);

        if(sortBy != null) {
            SortStrategy sortStrategy = new OfficeSortStrategy();
            sort = sortStrategy.getSort(sortBy, ascending);
        }

        PageRequest pageRequest = PageRequest.of(page, pageSize, sort);

        List<Office> offices = officeRepository.findAvailableOffices(dateStart, dateEnd, officeFilter, pageRequest).stream()
                .peek(office -> office.setPrice(Math.round(office.getPrice() * priceMultiplier)))
                .toList();

        if(offices.isEmpty()) {
            throw new ResourceNotFoundException("No available offices found");
        }
        return offices.stream().map(OfficeDto::valueFrom).toList();
    }

    public OfficeDto getOfficeById(String id) {
        return OfficeDto.valueFrom(officeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Office with id " + id + " not found")));
    }

    public List<Availability> getAvailableDatesWithPriceMultiplyer(String id, LocalDate dateStart) {
        YearMonth yearMonth = YearMonth.from(dateStart);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        List<Reservation> reservations = officeRepository.findReservationsByOfficeIdAndDateRange(id, startOfMonth, endOfMonth);
        List<Availability> availableDates = new ArrayList<>();

        for (LocalDate date = startOfMonth; !date.isAfter(endOfMonth); date = date.plusDays(1)) {
            boolean isAvailable = true;
            for (Reservation reservation : reservations) {
                if (!reservation.getStatus().equals(ReservationStatus.CANCELLED) &&
                        (date.isEqual(reservation.getStartTime()) || date.isEqual(reservation.getEndTime()) ||
                                (date.isAfter(reservation.getStartTime()) && date.isBefore(reservation.getEndTime())))) {
                    isAvailable = false;
                    break;
                }
            }
            if (isAvailable) {
                double priceMultiplier = getPriceMultiplierForDate(date); // Implement this method to get the price multiplier
                availableDates.add(new Availability(date, priceMultiplier));
            }
        }

        return availableDates;
    }

    private double getPriceMultiplierForDate(LocalDate date) {

        return trafficStatisticService.calculateMultiplier(date, date);
    }

    @Getter
    public class Availability {
        private final LocalDate date;
        private final double priceMultiplier;

        public Availability(LocalDate date, double priceMultiplier) {
            this.date = date;
            this.priceMultiplier = priceMultiplier;
        }
    }
}
