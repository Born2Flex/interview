package ua.edu.internship.interview.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.internship.interview.service.business.SkillService;
import ua.edu.internship.interview.service.dto.skill.SkillTreeDto;
import java.util.List;

@Tag(name = "Skills", description = "Skills management endpoints")
@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @GetMapping
    public List<SkillTreeDto> getSkills() {
        return skillService.getAllSkillTrees();
    }

    @GetMapping("/{id}")
    public SkillTreeDto getSkillById(@PathVariable String id) {
        return skillService.getSkillTreeById(id);
    }
}
