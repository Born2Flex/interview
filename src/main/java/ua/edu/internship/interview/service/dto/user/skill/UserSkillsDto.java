package ua.edu.internship.interview.service.dto.user.skill;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.internship.interview.service.dto.skill.SkillDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSkillsDto {
    private String id;
    private String userId;
    private List<SkillDto> skills;
}
