package ua.edu.internship.interview.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.edu.internship.interview.data.entity.SkillEntity;

public interface SkillRepository extends MongoRepository<SkillEntity, String> {
}
