package uni.projects.backend.services.geocoding;

import uni.projects.backend.exceptions.GeocodingException;

import java.util.concurrent.CompletableFuture;

public interface GeocodingService {

    /**
     * Converts an address to geographic coordinates (latitude and longitude).
     *
     * @param address The address to geocode.
     * @return The Location object as a CompletableFuture<Location>.
     * @throws GeocodingException if an error occurs during the geocoding process.
     */
    CompletableFuture<Location> geocode(String address) throws GeocodingException;

    /**
     * Converts geographic coordinates (latitude and longitude) to an address.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @return The address as a CompletableFuture<String>.
     * @throws GeocodingException if an error occurs during the reverse geocoding process.
     */
    CompletableFuture<String> reverseGeocode(double latitude, double longitude) throws GeocodingException;
}

