package ua.edu.internship.interview.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.internship.interview.service.business.UserQuestionService;
import ua.edu.internship.interview.service.dto.user.question.UserQuestionCreateDto;
import ua.edu.internship.interview.service.dto.user.question.UserQuestionDto;
import ua.edu.internship.interview.service.dto.user.question.UserQuestionUpdateDto;
import java.util.List;

@Tag(name = "User Skills and Questions", description = "User skills and questions management endpoints")
@RestController
@RequestMapping("/users/{userId}/questions")
@RequiredArgsConstructor
public class UserQuestionController {
    private final UserQuestionService service;

    @GetMapping
    public List<UserQuestionDto> getUserQuestions(@PathVariable String userId) {
        return service.getUserQuestions(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserQuestionDto createUserQuestion(@PathVariable String userId, @RequestBody UserQuestionCreateDto dto) {
        return service.createUserQuestion(userId, dto);
    }

    @PutMapping("/{questionId}")
    public UserQuestionDto updateUserQuestion(@PathVariable String userId, @PathVariable String questionId,
                                              @RequestBody UserQuestionUpdateDto dto) {
        return service.updateUserQuestion(userId, questionId, dto);
    }

    @DeleteMapping("/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserQuestion(@PathVariable String userId, @PathVariable String questionId) {
        service.deleteUserQuestion(userId, questionId);
    }

    @GetMapping("/skill/{skillId}")
    public List<UserQuestionDto> getUserQuestionsBySkill(@PathVariable String userId, @PathVariable String skillId) {
        return service.getUserQuestionsBySkillId(userId, skillId);
    }
}
