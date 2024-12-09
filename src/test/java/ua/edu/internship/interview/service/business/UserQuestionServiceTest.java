package ua.edu.internship.interview.service.business;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.internship.interview.data.documents.SkillDocument;
import ua.edu.internship.interview.data.documents.UserQuestionDocument;
import ua.edu.internship.interview.data.enumeration.QuestionDifficulty;
import ua.edu.internship.interview.data.enumeration.QuestionType;
import ua.edu.internship.interview.data.repository.SkillRepository;
import ua.edu.internship.interview.data.repository.UserQuestionRepository;
import ua.edu.internship.interview.service.dto.skill.SkillDto;
import ua.edu.internship.interview.service.dto.user.question.UserQuestionCreateDto;
import ua.edu.internship.interview.service.dto.user.question.UserQuestionDto;
import ua.edu.internship.interview.service.dto.user.question.UserQuestionUpdateDto;
import ua.edu.internship.interview.service.mapper.UserQuestionMapper;
import ua.edu.internship.interview.service.utils.exceptions.NoSuchEntityException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserQuestionServiceTest {
    @Mock
    private UserQuestionRepository userQuestionRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserQuestionMapper userQuestionMapper;
    @InjectMocks
    private UserQuestionService underTest;

    private UserQuestionDocument userQuestionDocument;
    private UserQuestionDto userQuestionDto;
    private SkillDocument skillDocument;
    private SkillDto skillDto;

    @BeforeEach
    void setUp() {
        skillDocument = new SkillDocument(new ObjectId("123456789123456789123456"), "Java", null);
        userQuestionDocument = new UserQuestionDocument();
        userQuestionDocument.setUserId("1");
        userQuestionDocument.setText("abc");
        userQuestionDocument.setDifficulty(QuestionDifficulty.MEDIUM);
        userQuestionDocument.setType(QuestionType.HARD_SKILLS);
        userQuestionDocument.setSkill(skillDocument);

        skillDto = new SkillDto("123456789123456789123456", "Java");
        userQuestionDto = new UserQuestionDto();
        userQuestionDto.setUserId("1");
        userQuestionDto.setText("abc");
        userQuestionDto.setDifficulty(QuestionDifficulty.MEDIUM);
        userQuestionDto.setType(QuestionType.HARD_SKILLS);
        userQuestionDto.setSkill(skillDto);
        userQuestionDto.setSkillId("123456789123456789123456");
    }

    @Test
    void getUserQuestions_shouldReturnUserQuestionDto_whenQuestionsExist() {
        String userId = "1";
        List<UserQuestionDocument> userQuestionDocuments = List.of(userQuestionDocument);
        when(userQuestionRepository.findAllByUserId(userId)).thenReturn(userQuestionDocuments);
        when(userQuestionMapper.toDto(userQuestionDocuments)).thenReturn(List.of(userQuestionDto));

        List<UserQuestionDto> result = underTest.getUserQuestions(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        matchQuestionFields(userQuestionDto, result.getFirst());
        verify(userQuestionRepository).findAllByUserId(userId);
    }

    @Test
    void createUserQuestion_shouldCreateAndReturnUserQuestionDto() {
        String userId = "1";
        String skillId = "123456789123456789123456";
        UserQuestionCreateDto createDto = new UserQuestionCreateDto();
        createDto.setSkillId(skillId);
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skillDocument));
        when(userQuestionMapper.toDocument(userId, createDto)).thenReturn(userQuestionDocument);
        when(userQuestionRepository.save(userQuestionDocument)).thenReturn(userQuestionDocument);
        when(userQuestionMapper.toDto(userQuestionDocument)).thenReturn(userQuestionDto);

        UserQuestionDto result = underTest.createUserQuestion(userId, createDto);

        assertNotNull(result);
        matchQuestionFields(userQuestionDto, result);
        verify(userQuestionRepository).save(userQuestionDocument);
        verify(skillRepository).findById("123456789123456789123456");
        verify(userQuestionMapper).toDocument(userId, createDto);
        verify(userQuestionRepository).save(userQuestionDocument);
        verify(userQuestionMapper).toDto(userQuestionDocument);
    }

    @Test
    void createUserQuestion_shouldThrowNoSuchEntityException_whenSkillNotFound() {
        String userId = "1";
        UserQuestionCreateDto questionDto = new UserQuestionCreateDto();
        questionDto.setSkillId("999");
        when(skillRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> underTest.createUserQuestion(userId, questionDto));
        verify(skillRepository).findById("999");
    }

    @Test
    void updateUserQuestion_shouldThrowNoSuchEntityException_whenQuestionNotFound() {
        String userId = "1";
        String questionId = "123456789123456789123456";
        UserQuestionUpdateDto questionDto = new UserQuestionUpdateDto();
        when(userQuestionRepository.findByUserIdAndId(userId, new ObjectId(questionId))).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> underTest.updateUserQuestion(userId, questionId, questionDto));
        verify(userQuestionRepository).findByUserIdAndId(userId, new ObjectId(questionId));
    }

    @Test
    void deleteUserQuestion_shouldDeleteQuestion_whenQuestionExists() {
        String userId = "1";
        String questionId = "123456789123456789123456";
        doNothing().when(userQuestionRepository).deleteByUserIdAndSkill_Id(userId, new ObjectId(questionId));

        underTest.deleteUserQuestion(userId, questionId);

        verify(userQuestionRepository).deleteByUserIdAndSkill_Id(userId, new ObjectId(questionId));
    }

    @Test
    void getUserQuestionsBySkillId_shouldReturnUserQuestionDto() {
        String userId = "1";
        String skillId = "123456789123456789123456";
        List<UserQuestionDocument> questions = List.of(userQuestionDocument);
        when(userQuestionRepository.findAllByUserIdAndSkill_Id(userId, new ObjectId(skillId))).thenReturn(questions);
        when(userQuestionMapper.toDto(questions)).thenReturn(List.of(userQuestionDto));

        List<UserQuestionDto> result = underTest.getUserQuestionsBySkillId(userId, skillId);

        assertNotNull(result);
        assertEquals(1, result.size());
        matchQuestionFields(userQuestionDto, result.getFirst());
        verify(userQuestionRepository).findAllByUserIdAndSkill_Id(userId, new ObjectId(skillId));
    }

    private void matchQuestionFields(UserQuestionDto expected, UserQuestionDto actual) {
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getText(), actual.getText());
        assertEquals(expected.getDifficulty(), actual.getDifficulty());
        assertEquals(expected.getType(), actual.getType());
        matchSkillFields(actual.getSkill(), expected.getSkill());
    }

    private void matchSkillFields(SkillDto expected, SkillDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }
}
