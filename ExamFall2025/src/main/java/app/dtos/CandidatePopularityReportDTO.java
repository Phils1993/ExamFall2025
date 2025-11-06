package app.dtos;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CandidatePopularityReportDTO {
    private Integer id;
    private Double averagePopularity;
}
