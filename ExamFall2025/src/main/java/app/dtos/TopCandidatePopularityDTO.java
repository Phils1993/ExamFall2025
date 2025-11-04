package app.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TopCandidatePopularityDTO {
    private Long id;
    private Double averagePopularity;
}
