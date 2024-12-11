package ua.edu.internship.interview.service.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ua.edu.internship.interview.data.documents.SkillDocument;
import ua.edu.internship.interview.data.documents.UserQuestionDocument;
import ua.edu.internship.interview.data.repository.SkillRepository;
import ua.edu.internship.interview.data.repository.UserQuestionRepository;
import ua.edu.internship.interview.service.dto.user.question.UserQuestionCreateDto;
import ua.edu.internship.interview.service.dto.user.question.UserQuestionDto;
import ua.edu.internship.interview.service.dto.user.question.UserQuestionUpdateDto;
import ua.edu.internship.interview.service.mapper.UserQuestionMapper;
import ua.edu.internship.interview.service.utils.exceptions.NoSuchEntityException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQuestionService {
    private final UserQuestionRepository userQuestionRepository;
    private final SkillRepository skillRepository;
    private final UserQuestionMapper userQuestionMapper;

    public List<UserQuestionDto> getUserQuestions(String userId) {
        List<UserQuestionDocument> questions = userQuestionRepository.findAllByUserId(userId);
        log.info("Retrieved {} questions for user with id: {}", questions.size(), userId);
        return userQuestionMapper.toDto(questions);
    }

    public UserQuestionDto createUserQuestion(String userId, UserQuestionCreateDto questionDto) {
        log.info("Attempting to create new question for user with id: {}", userId);
        UserQuestionDocument questionDocument = userQuestionMapper.toDocument(userId, questionDto);
        SkillDocument skill = getSkillByIdOrElseThrow(questionDto.getSkillId());
        questionDocument.setSkill(skill);
        UserQuestionDocument savedQuestion = userQuestionRepository.save(questionDocument);
        log.info("Created new question with id: {} for user with id: {}", savedQuestion.getId(), userId);
        return userQuestionMapper.toDto(savedQuestion);
    }

    public UserQuestionDto updateUserQuestion(String userId, String questionId, UserQuestionUpdateDto questionDto) {
        log.info("Attempting to update question with id: {} for user with id: {}", questionId, userId);
        UserQuestionDocument questionDocument = getQuestionByUserIdOrElseThrow(userId, questionId);
        userQuestionMapper.updateDocument(questionDto, questionDocument);
        UserQuestionDocument updatedQuestion = userQuestionRepository.save(questionDocument);
        log.info("Updated question with id: {} for user with id: {}", updatedQuestion.getId(), userId);
        return userQuestionMapper.toDto(updatedQuestion);
    }

    public void deleteUserQuestion(String userId, String questionId) {
        log.info("Attempting to delete question with id: {}, for user with id: {}", questionId, userId);
        userQuestionRepository.deleteByUserIdAndSkill_Id(userId, new ObjectId(questionId));
        log.info("Question with id: {} deleted successfully, for user with id: {}", questionId, userId);
    }

    public List<UserQuestionDto> getUserQuestionsBySkillId(String userId, String skillId) {
        List<UserQuestionDocument> questions = userQuestionRepository
                .findAllByUserIdAndSkill_Id(userId, new ObjectId(skillId));
        log.info("Retrieved {} questions for skill with id: {} for user with id: {}", questions.size(), skillId, userId);
        return userQuestionMapper.toDto(questions);
    }

    private SkillDocument getSkillByIdOrElseThrow(String skillId) {
        return skillRepository.findById(skillId).orElseThrow(() -> new NoSuchEntityException("Skill not found"));
    }

    private UserQuestionDocument getQuestionByUserIdOrElseThrow(String userId, String questionId) {
        return userQuestionRepository.findByUserIdAndId(userId, new ObjectId(questionId))
                .orElseThrow(() -> new NoSuchEntityException("Question not found"));
    }
}
