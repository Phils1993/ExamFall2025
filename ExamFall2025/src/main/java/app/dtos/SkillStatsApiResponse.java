package app.dtos;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillStatsApiResponse {

    @JsonProperty("data")
    private List<SkillStatsItemDTO> data;

    @JsonProperty("objects")
    private List<WrapperObject> objects;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WrapperObject {
        @JsonProperty("content")
        private String content;
    }
}
