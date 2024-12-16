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
import ua.edu.internship.interview.service.client.UserServiceClient;
import ua.edu.internship.interview.service.dto.skill.SkillDto;
import ua.edu.internship.interview.service.dto.user.UserDto;
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
    @Mock
    private UserServiceClient userClient;
    @InjectMocks
    private UserQuestionService underTest;

    private UserQuestionDocument userQuestionDocument;
    private UserQuestionDto userQuestionDto;
    private SkillDocument skillDocument;

    @BeforeEach
    void setUp() {
        skillDocument = SkillDocument.builder()
                .id(new ObjectId("123456789123456789123456"))
                .name("Java")
                .build();
        userQuestionDocument = UserQuestionDocument.builder()
                .userId(1L)
                .text("abc")
                .difficulty(QuestionDifficulty.MEDIUM)
                .type(QuestionType.HARD_SKILLS)
                .skill(skillDocument)
                .build();
        SkillDto skillDto = SkillDto.builder().id("123456789123456789123456").name("Java").build();
        userQuestionDto = UserQuestionDto.builder()
                .userId(1L)
                .text("abc")
                .difficulty(QuestionDifficulty.MEDIUM)
                .type(QuestionType.HARD_SKILLS)
                .skill(skillDto)
                .build();
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
        UserQuestionCreateDto createDto = UserQuestionCreateDto.builder()
                .text("abc")
                .skillId(skillId)
                .difficulty(QuestionDifficulty.MEDIUM)
                .type(QuestionType.HARD_SKILLS)
                .build();
        when(userClient.getById(userId)).thenReturn(Optional.of(new UserDto()));
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

        when(userClient.getById(userId)).thenReturn(Optional.of(new UserDto()));
        when(skillRepository.findById(objectId)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> underTest.createUserQuestion(userId, questionDto));
        verify(skillRepository).findById(objectId);
    }

    @Test
    void updateUserQuestion_shouldThrowNoSuchEntityException_whenQuestionNotFound() {
        Long userId = 1L;
        UserQuestionUpdateDto updateDto = new UserQuestionUpdateDto();
        String questionId = "123456789123456789123456";

        when(userClient.getById(userId)).thenReturn(Optional.of(new UserDto()));
        when(userQuestionRepository.findByIdAndUserId(new ObjectId(questionId), userId))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> underTest.updateUserQuestion(userId, questionId, updateDto));
        verify(userQuestionRepository).findByIdAndUserId(new ObjectId(questionId), userId);
    }

    @Test
    void updateUserQuestion_shouldUpdateAndReturnUserQuestionDto_whenQuestionExists() {
        Long userId = 1L;
        String questionId = "123456789123456789123456";
        String skillId = "654321987654321987654321";
        ObjectId questionObjectId = new ObjectId(questionId);
        ObjectId skillObjectId = new ObjectId(skillId);
        SkillDocument updatedSkillDocument = SkillDocument.builder().id(skillObjectId).name("Haskell").build();
        UserQuestionUpdateDto updateDto = createUserQuestionUpdateDto(
                "Updated text", skillId, QuestionDifficulty.EASY, QuestionType.SOFT_SKILLS);
        UserQuestionDocument updatedDocument = createUserQuestionDocument(
                questionObjectId, userId, "Updated text", QuestionDifficulty.EASY,
                QuestionType.SOFT_SKILLS, updatedSkillDocument);
        when(userClient.getById(userId)).thenReturn(Optional.of(new UserDto()));
        when(userQuestionRepository.findByIdAndUserId(questionObjectId, userId)).thenReturn(Optional.of(userQuestionDocument));
        when(skillRepository.findById(skillObjectId)).thenReturn(Optional.of(updatedSkillDocument));
        when(userQuestionMapper.updateDocument(userQuestionDocument, updateDto)).thenReturn(updatedDocument);
        when(userQuestionRepository.save(updatedDocument)).thenReturn(updatedDocument);
        when(userQuestionMapper.toDto(updatedDocument)).thenReturn(userQuestionDto);

        UserQuestionDto result = underTest.updateUserQuestion(userId, questionId, updateDto);

        assertNotNull(result);
        matchQuestionFields(userQuestionDto, result);
        verify(userQuestionRepository).findByIdAndUserId(questionObjectId, userId);
        verify(skillRepository).findById(skillObjectId);
        verify(userQuestionMapper).updateDocument(userQuestionDocument, updateDto);
        verify(userQuestionRepository).save(updatedDocument);
        verify(userQuestionMapper).toDto(updatedDocument);
    }


    @Test
    void deleteUserQuestion_shouldDeleteQuestion_whenQuestionExists() {
        Long userId = 1L;
        String questionId = "123456789123456789123456";
        when(userClient.getById(userId)).thenReturn(Optional.of(new UserDto()));
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

    @Test
    void createUserQuestion_shouldThrowNoSuchEntityException_whenUserNotFound() {
        Long userId = 1L;
        UserQuestionCreateDto questionDto = new UserQuestionCreateDto();
        when(userClient.getById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> underTest.createUserQuestion(userId, questionDto));

        verify(userClient).getById(userId);
    }

    @Test
    void updateUserQuestion_shouldThrowNoSuchEntityException_whenUserNotFound() {
        Long userId = 1L;
        UserQuestionUpdateDto questionDto = new UserQuestionUpdateDto();
        when(userClient.getById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> underTest.updateUserQuestion(userId, "1",questionDto));

        verify(userClient).getById(userId);
    }

    @Test
    void deleteUserQuestion_shouldThrowNoSuchEntityException_whenUserNotFound() {
        Long userId = 1L;
        String questionId = "123";
        when(userClient.getById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> underTest.deleteUserQuestion(userId, questionId));

        verify(userClient).getById(userId);
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

    private UserQuestionUpdateDto createUserQuestionUpdateDto(String text, String skillId, QuestionDifficulty difficulty,
                                                              QuestionType type) {
        return UserQuestionUpdateDto.builder()
                .text(text)
                .skillId(skillId)
                .difficulty(difficulty)
                .type(type).build();
    }

    private UserQuestionDocument createUserQuestionDocument(ObjectId id, Long userId, String text,
                                                            QuestionDifficulty difficulty, QuestionType type,
                                                            SkillDocument skill) {
        return UserQuestionDocument.builder()
                .id(id)
                .userId(userId)
                .text(text)
                .skill(skill)
                .difficulty(difficulty)
                .type(type).build();
    }
}
