package ua.edu.internship.interview.data.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import ua.edu.internship.interview.data.documents.UserQuestionDocument;
import java.util.List;
import java.util.Optional;

public interface UserQuestionRepository extends MongoRepository<UserQuestionDocument, String> {
    List<UserQuestionDocument> findAllByUserId(String userId);

    List<UserQuestionDocument> findAllByUserIdAndSkill_Id(String userId, ObjectId skillId);

    Optional<UserQuestionDocument> findByUserIdAndId(String userId, ObjectId id);

    void deleteByUserIdAndSkill_Id(String userId, ObjectId skillId);
}
