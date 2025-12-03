package app.services;

import app.dtos.SkillEnrichedDTO;
import app.dtos.SkillStatsResponseDTO;
import app.dtos.SkillStatsDTO;
import app.exceptions.ApiException;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final ObjectMapper objectMapper = new Utils().getObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(ServiceAPI.class);

    private static final String BASE_URL = "https://apiprovider.cphbusinessapps.dk/api/v1/skills/stats";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    public Map<String, SkillEnrichedDTO> fetchSkillFromExternalApi(List<String> slugs) {
        if (slugs == null || slugs.isEmpty()) return Collections.emptyMap();

        String joinedSlugs = slugs.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> URLEncoder.encode(s, StandardCharsets.UTF_8))
                .collect(Collectors.joining(","));

        if (joinedSlugs.isEmpty()) return Collections.emptyMap();

        String url = BASE_URL + "?slugs=" + joinedSlugs;

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(TIMEOUT)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(TIMEOUT)
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 || response.body() == null || response.body().isBlank()) {
                logger.warn("API call failed with status: {}", response.statusCode());
                return Collections.emptyMap();
            }

            SkillStatsResponseDTO responseDTO = objectMapper.readValue(response.body(), SkillStatsResponseDTO.class);
            List<SkillStatsDTO> data = responseDTO.getData();

            if (data == null || data.isEmpty()) {
                logger.info("No skill data returned from API");
                return Collections.emptyMap();
            }

            return data.stream()
                    .filter(Objects::nonNull)
                    .filter(dto -> dto.getSlug() != null && !dto.getSlug().isBlank())
                    .collect(Collectors.toMap(
                            SkillStatsDTO::getSlug,
                            dto -> SkillEnrichedDTO.builder()
                                    .slug(dto.getSlug())
                                    .popularityScore(dto.getPopularityScore())
                                    .averageSalary(dto.getAverageSalary())
                                    .build(),
                            (a, b) -> a,
                            LinkedHashMap::new
                    ));

        } catch (Exception e) {
            logger.error("Failed to fetch skill details from external API", e);
            throw new ApiException( 500, "Failed to fetch data from external API");
        }
    }
}
