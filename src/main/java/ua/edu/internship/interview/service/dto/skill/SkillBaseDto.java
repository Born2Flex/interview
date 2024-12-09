package ua.edu.internship.interview.service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SkillBaseDto {
    @EqualsAndHashCode.Include
    private String id;
    private String name;
}
