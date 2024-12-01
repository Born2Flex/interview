package ua.edu.internship.interview.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.internship.interview.service.business.UserSkillsService;
import ua.edu.internship.interview.service.dto.user.skill.UserSkillsDto;
import java.util.List;

@Tag(name = "User Skills and Questions", description = "User skills and questions management endpoints")
@RestController
@RequestMapping("/users/{userId}/skills")
@RequiredArgsConstructor
public class UserSkillController {
    private final UserSkillsService service;

    @GetMapping
    public UserSkillsDto getSkillsByUserId(@PathVariable String userId) {
        return service.getUserSkills(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserSkillsDto createUserSkills(@PathVariable String userId, @RequestBody List<String> skillIds) {
        return service.createUserSkills(userId, skillIds);
    }

    @PutMapping
    public UserSkillsDto updateUserSkills(@PathVariable String userId, @RequestBody List<String> skillIds) {
        return service.updateUserSkills(userId, skillIds);
    }
}
