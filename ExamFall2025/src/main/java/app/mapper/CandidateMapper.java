package app.mapper;

import app.dtos.CandidateCreateDTO;
import app.dtos.CandidateDTO;
import app.entities.Candidate;
import app.entities.CandidateSkill;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CandidateMapper {

    public static CandidateDTO toDto(Candidate c) {
        if (c == null) return null;
        Set<Long> skillIds = c.getCandidateSkills() == null
                ? Set.of()
                : c.getCandidateSkills().stream()
                .map(CandidateSkill::getSkill)
                .filter(s -> s != null && s.getId() != null)
                .map(s -> s.getId())
                .collect(Collectors.toSet());

        return CandidateDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .phone(c.getPhone())
                .education(c.getEducation())
                .skillIds(skillIds)
                .build();
    }

    public static void updateEntityFromCreateDto(Candidate candidate, CandidateCreateDTO dto) {
        if (dto.getName() != null) candidate.setName(dto.getName());
        if (dto.getPhone() != null) candidate.setPhone(dto.getPhone());
        if (dto.getEducation() != null) candidate.setEducation(dto.getEducation());
        // Do not touch skills here; linking handled by DAO operations to avoid transient ID issues
    }

    public static Candidate toEntity(CandidateCreateDTO dto) {
        if (dto == null) return null;
        Candidate c = Candidate.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .education(dto.getEducation())
                .build();

        // Keep the collection non-null to avoid NPEs in service/DAO code.
        c.setCandidateSkills(new HashSet<>());

        return c;
    }
}
