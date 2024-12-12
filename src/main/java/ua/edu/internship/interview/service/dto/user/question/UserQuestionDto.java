package ua.edu.internship.interview.service.dto.user.question;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.edu.internship.interview.service.dto.skill.SkillDto;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserQuestionDto extends UserQuestionBaseDto {
    private String id;
    private Long userId;
    private SkillDto skill;
}
