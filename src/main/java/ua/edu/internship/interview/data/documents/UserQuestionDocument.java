package ua.edu.internship.interview.data.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import ua.edu.internship.interview.data.enumeration.QuestionDifficulty;
import ua.edu.internship.interview.data.enumeration.QuestionType;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Builder
@Document(collection = "user_questions")
public class UserQuestionDocument {
    @Id
    @EqualsAndHashCode.Include
    @Field(name = "id")
    private ObjectId id;
    @Field(name = "userId")
    private String userId;
    @DocumentReference
    private SkillDocument skill;
    @Field(name = "text")
    private String text;
    @Field(name = "difficulty")
    private QuestionDifficulty difficulty;
    @Field(name = "type")
    private QuestionType type;
}