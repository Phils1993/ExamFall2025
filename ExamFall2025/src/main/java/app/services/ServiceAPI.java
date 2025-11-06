package app.services;

import app.dtos.SkillEnrichedDTO;
import app.dtos.SkillStatsResponseDTO;
import app.dtos.SkillStatsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class ServiceAPI {

    private final Logger logger = LoggerFactory.getLogger(ServiceAPI.class);

    private static final String DEFAULT_BASE_URL = "https://apiprovider.cphbusinessapps.dk/api/v1/skills/stats";
    private final String baseUrl;
    private final HttpClient client;
    private final ObjectMapper mapper;
    private final Duration timeout;
    private final String apiKeyHeaderName = "x-api-key";

    public ServiceAPI() {
        this(DEFAULT_BASE_URL, Duration.ofSeconds(5));
    }

    public ServiceAPI(String baseUrl, Duration timeout) {
        this.baseUrl = (baseUrl == null || baseUrl.isBlank()) ? DEFAULT_BASE_URL : baseUrl;
        this.timeout = (timeout == null) ? Duration.ofSeconds(5) : timeout;
        this.client = HttpClient.newBuilder().connectTimeout(this.timeout).build();

        this.mapper = new ObjectMapper();
        // ensure Java 8+ date/time types are supported
        this.mapper.registerModule(new JavaTimeModule());
        // ensure ISO date/time strings are used instead of timestamps
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Fetch stats for the provided slugs. Returns an empty map on error or when provider has no data.
     */
    public Map<String, SkillEnrichedDTO> fetchStatsForSlugs(Set<String> slugs) {
        if (slugs == null || slugs.isEmpty()) return Collections.emptyMap();

        try {
            String joined = slugs.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(s -> URLEncoder.encode(s, StandardCharsets.UTF_8))
                    .collect(Collectors.joining(","));
            if (joined.isEmpty()) return Collections.emptyMap();

            String url = baseUrl + "?slugs=" + joined;
            HttpRequest.Builder rb = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(timeout)
                    .GET()
                    .header("Accept", "application/json");

            String apiKey = System.getenv("SKILL_STATS_API_KEY");
            if (apiKey != null && !apiKey.isBlank()) {
                rb.header(apiKeyHeaderName, apiKey);
            }

            HttpRequest req = rb.build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() != 200 || resp.body() == null || resp.body().isBlank()) {
                System.err.println("API call failed: " + resp.statusCode());
                return Collections.emptyMap();
            }

            SkillStatsResponseDTO wrapper = mapper.readValue(resp.body(), SkillStatsResponseDTO.class);
            List<SkillStatsDTO> items = wrapper.getData();

            if (items == null || items.isEmpty()) {
                System.err.println("No skill data returned from API");
                return Collections.emptyMap();
            }

            return items.stream()
                    .filter(Objects::nonNull)
                    .filter(i -> i.getSlug() != null && !i.getSlug().isBlank())
                    .collect(Collectors.toMap(
                            SkillStatsDTO::getSlug,
                            i -> new SkillEnrichedDTO(i.getSlug(), i.getPopularityScore(), i.getAverageSalary()),
                            (a, b) -> a,
                            LinkedHashMap::new
                    ));

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            return Collections.emptyMap();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }
}
