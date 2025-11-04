package app.daos;

import app.entities.Skill;

import java.util.Optional;

public interface ISkillDAO extends IDAO<Skill> {
    Optional<Skill> findByName(String name);
}
