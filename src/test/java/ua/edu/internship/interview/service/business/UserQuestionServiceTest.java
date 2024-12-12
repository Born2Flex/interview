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
        ObjectId skillId = new ObjectId("123456789123456789123456");
        skillDocument = new SkillDocument(skillId, "Java", null);
        userQuestionDocument = createUserQuestionDocument(skillDocument);
        skillDto = new SkillDto(skillId.toHexString(), "Java");
        userQuestionDto = createUserQuestionDto(skillDto, skillId.toHexString());
    }

    private UserQuestionDocument createUserQuestionDocument(SkillDocument skillDocument) {
        UserQuestionDocument document = new UserQuestionDocument();
        document.setUserId(1L);
        document.setText("abc");
        document.setDifficulty(QuestionDifficulty.MEDIUM);
        document.setType(QuestionType.HARD_SKILLS);
        document.setSkill(skillDocument);
        return document;
    }

    private UserQuestionDto createUserQuestionDto(SkillDto skillDto, String skillId) {
        UserQuestionDto dto = new UserQuestionDto();
        dto.setUserId(1L);
        dto.setText("abc");
        dto.setDifficulty(QuestionDifficulty.MEDIUM);
        dto.setType(QuestionType.HARD_SKILLS);
        dto.setSkill(skillDto);
        dto.setSkillId(skillId);
        return dto;
    }

    @Test
    void getUserQuestions_shouldReturnUserQuestionDto_whenQuestionsExist() {
        Long userId = 1L;
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
        Long userId = 1L;
        String skillId = "123456789123456789123456";
        ObjectId objectId = new ObjectId(skillId);
        UserQuestionCreateDto createDto = new UserQuestionCreateDto();
        createDto.setSkillId(skillId);
        when(skillRepository.findById(objectId)).thenReturn(Optional.of(skillDocument));
        when(userQuestionMapper.toDocument(userId, createDto)).thenReturn(userQuestionDocument);
        when(userQuestionRepository.save(userQuestionDocument)).thenReturn(userQuestionDocument);
        when(userQuestionMapper.toDto(userQuestionDocument)).thenReturn(userQuestionDto);

        UserQuestionDto result = underTest.createUserQuestion(userId, createDto);

        assertNotNull(result);
        matchQuestionFields(userQuestionDto, result);
        verify(userQuestionRepository).save(userQuestionDocument);
        verify(skillRepository).findById(objectId);
        verify(userQuestionMapper).toDocument(userId, createDto);
        verify(userQuestionRepository).save(userQuestionDocument);
        verify(userQuestionMapper).toDto(userQuestionDocument);
    }

    @Test
    void createUserQuestion_shouldThrowNoSuchEntityException_whenSkillNotFound() {
        Long userId = 1L;
        String skillId = "123456789123456789123456";
        ObjectId objectId = new ObjectId(skillId);
        UserQuestionCreateDto questionDto = new UserQuestionCreateDto();
        questionDto.setSkillId(skillId);
        when(skillRepository.findById(objectId)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> underTest.createUserQuestion(userId, questionDto));
        verify(skillRepository).findById(objectId);
    }

    @Test
    void updateUserQuestion_shouldThrowNoSuchEntityException_whenQuestionNotFound() {
        Long userId = 1L;
        String questionId = "123456789123456789123456";
        UserQuestionUpdateDto questionDto = new UserQuestionUpdateDto();
        when(userQuestionRepository.findByUserIdAndId(userId, new ObjectId(questionId))).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> underTest.updateUserQuestion(userId, questionId, questionDto));
        verify(userQuestionRepository).findByUserIdAndId(userId, new ObjectId(questionId));
    }

    @Test
    void updateUserQuestion_shouldUpdateAndReturnUserQuestionDto_whenQuestionExists() {
        Long userId = 1L;
        String questionId = "123456789123456789123456";
        UserQuestionUpdateDto updateDto = new UserQuestionUpdateDto();
        when(userQuestionRepository.findByUserIdAndId(userId, new ObjectId(questionId))).thenReturn(Optional.of(userQuestionDocument));
        when(userQuestionRepository.save(userQuestionDocument)).thenReturn(userQuestionDocument);
        when(userQuestionMapper.toDto(userQuestionDocument)).thenReturn(userQuestionDto);

        UserQuestionDto result = underTest.updateUserQuestion(userId, questionId, updateDto);

        assertNotNull(result);
        matchQuestionFields(userQuestionDto, result);
        verify(userQuestionRepository).findByUserIdAndId(userId, new ObjectId(questionId));
        verify(userQuestionRepository).save(userQuestionDocument);
        verify(userQuestionMapper).toDto(userQuestionDocument);
    }

    @Test
    void deleteUserQuestion_shouldDeleteQuestion_whenQuestionExists() {
        Long userId = 1L;
        String questionId = "123456789123456789123456";
        doNothing().when(userQuestionRepository).deleteByUserIdAndSkill_Id(userId, new ObjectId(questionId));

        underTest.deleteUserQuestion(userId, questionId);

        verify(userQuestionRepository).deleteByUserIdAndSkill_Id(userId, new ObjectId(questionId));
    }

    @Test
    void getUserQuestionsBySkillId_shouldReturnUserQuestionDto() {
        Long userId = 1L;
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
