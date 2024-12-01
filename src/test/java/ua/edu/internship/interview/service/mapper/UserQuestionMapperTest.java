package ua.edu.internship.interview.service.mapper;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.internship.interview.data.documents.UserQuestionDocument;
import ua.edu.internship.interview.data.enumeration.QuestionDifficulty;
import ua.edu.internship.interview.data.enumeration.QuestionType;
import ua.edu.internship.interview.service.dto.user.question.UserQuestionCreateDto;
import ua.edu.internship.interview.service.dto.user.question.UserQuestionDto;
import ua.edu.internship.interview.service.dto.user.question.UserQuestionUpdateDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserQuestionMapperTest {
    @Mock
    private BaseMapper baseMapper; // Mock BaseMapper
    @Mock
    private SkillMapper skillMapper;
    @InjectMocks
    private UserQuestionMapperImpl userQuestionMapper;

    @Test
    void toDto_shouldMapUserQuestionDocumentToUserQuestionDto() {
        UserQuestionDocument userQuestionDocument = new UserQuestionDocument();
        ObjectId id = new ObjectId("123456789123456789123456");
        userQuestionDocument.setId(id);
        userQuestionDocument.setUserId("1");
        userQuestionDocument.setText("Sample question text");
        userQuestionDocument.setDifficulty(QuestionDifficulty.HARD);
        userQuestionDocument.setType(QuestionType.SOFT_SKILLS);
        when(baseMapper.map(id)).thenReturn("123456789123456789123456");

        UserQuestionDto result = userQuestionMapper.toDto(userQuestionDocument);

        assertNotNull(result);
        assertEquals(userQuestionDocument.getId(), new ObjectId(result.getId()));
        assertEquals(userQuestionDocument.getUserId(), result.getUserId());
        assertEquals(userQuestionDocument.getText(), result.getText());
        assertEquals(userQuestionDocument.getDifficulty(), result.getDifficulty());
        assertEquals(userQuestionDocument.getType(), result.getType());
    }

    @Test
    void toDto_shouldMapListOfUserQuestionDocumentsToListOfUserQuestionDtos() {
        UserQuestionDocument userQuestionDocument = new UserQuestionDocument();
        userQuestionDocument.setId(new ObjectId("123456789123456789123456"));
        userQuestionDocument.setUserId("1");
        userQuestionDocument.setText("Sample question text");
        userQuestionDocument.setDifficulty(QuestionDifficulty.HARD);
        userQuestionDocument.setType(QuestionType.SOFT_SKILLS);
        List<UserQuestionDocument> documents = List.of(userQuestionDocument);

        List<UserQuestionDto> result = userQuestionMapper.toDto(documents);

        assertNotNull(result);
        assertEquals(1, result.size());
        UserQuestionDto resultDto = result.getFirst();
        assertEquals(userQuestionDocument.getUserId(), resultDto.getUserId());
        assertEquals(userQuestionDocument.getText(), resultDto.getText());
        assertEquals(userQuestionDocument.getDifficulty(), resultDto.getDifficulty());
        assertEquals(userQuestionDocument.getType(), resultDto.getType());
    }

    @Test
    void toDocument_shouldMapUserQuestionCreateDtoToUserQuestionDocument() {
        UserQuestionCreateDto userQuestionCreateDto = new UserQuestionCreateDto();
        userQuestionCreateDto.setText("Sample create question text");
        userQuestionCreateDto.setSkillId("654321987654321987654321");
        userQuestionCreateDto.setDifficulty(QuestionDifficulty.MEDIUM);
        userQuestionCreateDto.setType(QuestionType.HARD_SKILLS);

        UserQuestionDocument result = userQuestionMapper.toDocument("1", userQuestionCreateDto);

        assertNotNull(result);
        assertEquals("1", result.getUserId());
        assertEquals(userQuestionCreateDto.getText(), result.getText());
        assertEquals(userQuestionCreateDto.getDifficulty(), result.getDifficulty());
        assertEquals(userQuestionCreateDto.getType(), result.getType());
    }

    @Test
    void updateDocument_shouldUpdateUserQuestionDocumentWithUserQuestionUpdateDto() {
        UserQuestionUpdateDto userQuestionUpdateDto = new UserQuestionUpdateDto();
        userQuestionUpdateDto.setText("Updated question text");
        userQuestionUpdateDto.setDifficulty(QuestionDifficulty.EASY);
        userQuestionUpdateDto.setType(QuestionType.SOFT_SKILLS);

        UserQuestionDocument userQuestionDocument = new UserQuestionDocument();
        userQuestionDocument.setId(new ObjectId("123456789123456789123456"));
        userQuestionDocument.setText("Sample question text");
        userQuestionDocument.setDifficulty(QuestionDifficulty.HARD);
        userQuestionDocument.setType(QuestionType.SOFT_SKILLS);

        userQuestionMapper.updateDocument(userQuestionUpdateDto, userQuestionDocument);

        assertNotNull(userQuestionDocument);
        assertEquals(userQuestionUpdateDto.getText(), userQuestionDocument.getText());
        assertEquals(userQuestionUpdateDto.getDifficulty(), userQuestionDocument.getDifficulty());
        assertEquals(userQuestionUpdateDto.getType(), userQuestionDocument.getType());
    }
}
