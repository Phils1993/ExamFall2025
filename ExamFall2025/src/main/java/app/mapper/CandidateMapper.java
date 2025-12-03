package app.mapper;

import app.dtos.CandidateCreateDTO;
import app.dtos.CandidateDTO;
import app.entities.Candidate;

public class CandidateMapper {

    public Candidate fromCreateDto(CandidateCreateDTO dto) {
        if (dto == null) return null;

        return Candidate.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .education(dto.getEducation())
                .build();
    }

    public CandidateDTO toDto(Candidate entity) {
        if (entity == null) return null;

        return CandidateDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .phone(entity.getPhone())
                .education(entity.getEducation())
                .skillIds(entity.getCandidateSkills().stream()
                        .map(cs -> cs.getSkill().getId())
                        .toList())
                .build();
    }
}
