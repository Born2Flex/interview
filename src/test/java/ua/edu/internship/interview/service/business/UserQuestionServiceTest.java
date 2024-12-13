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

    @BeforeEach
    void setUp() {
        skillDocument = new SkillDocument(new ObjectId("123456789123456789123456"), "Java", null);

        userQuestionDocument = new UserQuestionDocument();
        userQuestionDocument.setUserId(1L);
        userQuestionDocument.setText("abc");
        userQuestionDocument.setDifficulty(QuestionDifficulty.MEDIUM);
        userQuestionDocument.setType(QuestionType.HARD_SKILLS);

        SkillDto skillDto = new SkillDto(skillDocument.getId().toHexString(), "Java");
        userQuestionDto = new UserQuestionDto();
        userQuestionDto.setUserId(1L);
        userQuestionDto.setText("abc");
        userQuestionDto.setDifficulty(QuestionDifficulty.MEDIUM);
        userQuestionDto.setType(QuestionType.HARD_SKILLS);
        userQuestionDto.setSkill(skillDto);
    }

    @Test
    void getUserQuestions_shouldReturnUserQuestionDto_whenQuestionsExist() {
        Long userId = 1L;
        when(userQuestionRepository.findAllByUserId(userId)).thenReturn(List.of(userQuestionDocument));
        when(userQuestionMapper.toDto(List.of(userQuestionDocument))).thenReturn(List.of(userQuestionDto));

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
        UserQuestionUpdateDto updateDto = new UserQuestionUpdateDto();

        when(userQuestionRepository.findByIdAndUserId(new ObjectId(questionId), userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> underTest.updateUserQuestion(userId, questionId, updateDto));
        verify(userQuestionRepository).findByIdAndUserId(new ObjectId(questionId), userId);
    }

    @Test
    void updateUserQuestion_shouldUpdateAndReturnUserQuestionDto_whenQuestionExists() {
        Long userId = 1L;
        String questionId = "123456789123456789123456";
        ObjectId skillId = new ObjectId("654321987654321987654321");
        UserQuestionUpdateDto updateDto = new UserQuestionUpdateDto();
        updateDto.setSkillId(skillId.toString());
        updateDto.setText("Updated text");
        updateDto.setDifficulty(QuestionDifficulty.EASY);
        updateDto.setType(QuestionType.SOFT_SKILLS);

        when(userQuestionRepository.findByIdAndUserId(new ObjectId(questionId), userId)).thenReturn(Optional.of(userQuestionDocument));
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skillDocument));
        when(userQuestionMapper.updateDocument(userQuestionDocument, updateDto)).thenReturn(userQuestionDocument);
        when(userQuestionRepository.save(userQuestionDocument)).thenReturn(userQuestionDocument);
        when(userQuestionMapper.toDto(userQuestionDocument)).thenReturn(userQuestionDto);

        UserQuestionDto result = underTest.updateUserQuestion(userId, questionId, updateDto);

        assertNotNull(result);
        matchQuestionFields(userQuestionDto, result);
        verify(userQuestionRepository).findByIdAndUserId(new ObjectId(questionId), userId);
        verify(skillRepository).findById(skillId);
        verify(userQuestionMapper).updateDocument(userQuestionDocument, updateDto);
        verify(userQuestionRepository).findByIdAndUserId(new ObjectId(questionId), userId);
        verify(skillRepository).findById(skillId);
        verify(userQuestionMapper).updateDocument(userQuestionDocument, updateDto);
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
        when(userQuestionRepository.findAllByUserIdAndSkillId(userId, new ObjectId(skillId))).thenReturn(questions);
        when(userQuestionMapper.toDto(questions)).thenReturn(List.of(userQuestionDto));

        List<UserQuestionDto> result = underTest.getUserQuestionsBySkillId(userId, skillId);

        assertNotNull(result);
        assertEquals(1, result.size());
        matchQuestionFields(userQuestionDto, result.getFirst());
        verify(userQuestionRepository).findAllByUserIdAndSkillId(userId, new ObjectId(skillId));
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
