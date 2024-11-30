package ua.edu.internship.interview.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.edu.internship.interview.data.documents.UserSkillsDocument;
import java.util.Optional;

public interface UserSkillRepository extends MongoRepository<UserSkillsDocument, String> {
    Optional<UserSkillsDocument> findByUserId(String userId);
}
