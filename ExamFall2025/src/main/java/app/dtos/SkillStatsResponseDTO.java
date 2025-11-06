package app.dtos;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

// Bruges til at deserialisere hele responsen fra ekstern API
public class SkillStatsResponseDTO {
    @JsonProperty("data")
    private List<SkillStatsDTO> data;

}
