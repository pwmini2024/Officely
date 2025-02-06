package uni.projects.backend.services.geocoding;

import org.springframework.scheduling.annotation.Async;
import org.springframework.beans.factory.annotation.Value;
import java.util.concurrent.CompletableFuture;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uni.projects.backend.exceptions.GeocodingException;

@Service
public class GoogleGeocodingService implements GeocodingService {

    @Value("${google.api.key}")
    private String apiKey;

    private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    public GoogleGeocodingService() {}

    @Async
    @Override
    public CompletableFuture<Location> geocode(String address) throws GeocodingException {
        try {
            String encodedAddress = java.net.URLEncoder.encode(address, "UTF-8");
            String requestUrl = GEOCODE_URL + "?address=" + encodedAddress + "&key=" + apiKey;

            String response = sendRequest(requestUrl);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            if (!"OK".equals(root.path("status").asText())) {
                throw new GeocodingException("Geocoding failed with status: " + root.path("status").asText());
            }

            JsonNode location = root.path("results").get(0).path("geometry").path("location");

            double lat = location.path("lat").asDouble();
            double lng = location.path("lng").asDouble();

            return CompletableFuture.completedFuture(new Location(lat, lng));
        } catch (Exception e) {
            throw new GeocodingException("Error during geocoding", e);
        }
    }

    @Async
    @Override
    public CompletableFuture<String> reverseGeocode(double latitude, double longitude) throws GeocodingException {
        try {
            String requestUrl = GEOCODE_URL + "?latlng=" + latitude + "," + longitude + "&key=" + apiKey;

            String response = sendRequest(requestUrl);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            if (!"OK".equals(root.path("status").asText())) {
                throw new GeocodingException("Reverse geocoding failed with status: " + root.path("status").asText());
            }

            return CompletableFuture.completedFuture(root.path("results").get(0).path("formatted_address").asText());
        } catch (Exception e) {
            throw new GeocodingException("Error during reverse geocoding", e);
        }
    }

    private String sendRequest(String requestUrl) throws Exception {
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }
}
