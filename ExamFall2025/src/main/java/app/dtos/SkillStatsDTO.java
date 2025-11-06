package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillStatsDTO {
    @JsonProperty("slug")
    private String slug;

    @JsonProperty("popularityScore")
    private Integer popularityScore;

    @JsonProperty("averageSalary")
    private Integer averageSalary;

    // Optional fields if needed later
    @JsonProperty("id")
    private String externalId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("categoryKey")
    private String categoryKey;

    @JsonProperty("updatedAt")
    private OffsetDateTime updatedAt;
}
