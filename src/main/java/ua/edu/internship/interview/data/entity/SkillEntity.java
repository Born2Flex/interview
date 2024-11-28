package ua.edu.internship.interview.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Builder
@Document(collection = "skills")
public class SkillEntity {
    @Id
    @EqualsAndHashCode.Include
    @Field(name = "id")
    private ObjectId id;
    @Field(name = "name")
    private String name;
    @Field("parentId")
    private ObjectId parentId;
}
