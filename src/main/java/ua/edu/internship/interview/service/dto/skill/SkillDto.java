package ua.edu.internship.interview.service.dto.skill;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SkillDto extends SkillBaseDto {
    public SkillDto(String id, String name) {
        super(id, name);
    }
}
