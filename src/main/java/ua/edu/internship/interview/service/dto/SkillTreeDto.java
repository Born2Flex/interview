package ua.edu.internship.interview.service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SkillTreeDto {
    @EqualsAndHashCode.Include
    private String id;
    private String name;
    private String parentId;
    private List<SkillTreeDto> children = new ArrayList<>();
}
