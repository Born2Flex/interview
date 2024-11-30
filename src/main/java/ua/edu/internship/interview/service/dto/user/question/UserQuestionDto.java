package ua.edu.internship.interview.service.dto.user.question;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ua.edu.internship.interview.service.dto.skill.SkillDto;

@Data
@EqualsAndHashCode(callSuper=true)
public class UserQuestionDto extends UserQuestionBaseDto {
    private String id;
    private String userId;
    private SkillDto skill;
}
