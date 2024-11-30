package ua.edu.internship.interview.service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SkillTreeDto extends SkillBaseDto {
    private String parentId;
    private List<SkillTreeDto> children = new ArrayList<>();
}
