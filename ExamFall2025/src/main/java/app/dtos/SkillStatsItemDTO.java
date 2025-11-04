package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillStatsItemDTO {
    @JsonProperty("slug")
    private String slug;

    @JsonProperty("popularityScore")
    private Integer popularityScore;

    @JsonProperty("averageSalary")
    private Integer averageSalary;

    // optional fields if you later want them
    @JsonProperty("id")
    private String externalId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("categoryKey")
    private String categoryKey;

    @JsonProperty("updatedAt")
    private OffsetDateTime updatedAt;
}
