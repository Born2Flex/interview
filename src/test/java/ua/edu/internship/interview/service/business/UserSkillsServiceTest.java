package ua.edu.internship.interview.service.business;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.internship.interview.data.documents.SkillDocument;
import ua.edu.internship.interview.data.documents.UserSkillsDocument;
import ua.edu.internship.interview.data.repository.SkillRepository;
import ua.edu.internship.interview.data.repository.UserSkillRepository;
import ua.edu.internship.interview.service.dto.skill.SkillDto;
import ua.edu.internship.interview.service.dto.user.skill.UserSkillsDto;
import ua.edu.internship.interview.service.mapper.UserSkillsMapper;
import ua.edu.internship.interview.service.utils.exceptions.InvalidInputException;
import ua.edu.internship.interview.service.utils.exceptions.NoSuchEntityException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserSkillsServiceTest {
    @Mock
    private UserSkillRepository userSkillRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillsMapper userSkillsMapper;
    @InjectMocks
    private UserSkillsService underTest;

    private List<SkillDocument> skillDocuments;
    private List<SkillDto> skillDTOs;
    private UserSkillsDocument userSkillsDocument;
    private UserSkillsDto userSkillsDto;

    @BeforeEach
    void setUp() {
        skillDocuments = List.of(buildSkillDoc("123456789123456789123456", "Java"),
                buildSkillDoc("123456789123456789123456", "Kotlin"));
        skillDTOs = List.of(buildSkillDto("1", "Java"), buildSkillDto("2", "Kotlin"));

        userSkillsDocument = new UserSkillsDocument();
        userSkillsDocument.setUserId("1");
        userSkillsDocument.setSkills(skillDocuments);

        userSkillsDto = new UserSkillsDto();
        userSkillsDto.setUserId("1");
        userSkillsDto.setSkills(skillDTOs);
    }

    @Test
    void getUserSkills_shouldReturnUserSkillsDto_whenSkillsExist() {
        String userId = "1";
        when(userSkillRepository.findByUserId(userId)).thenReturn(Optional.of(userSkillsDocument));
        when(userSkillsMapper.toDto(userSkillsDocument)).thenReturn(userSkillsDto);

        UserSkillsDto result = underTest.getUserSkills(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(2, result.getSkills().size());
        matchSkillFields(skillDTOs.getFirst(), result.getSkills().getFirst());
        matchSkillFields(skillDTOs.getLast(), result.getSkills().getLast());
        verify(userSkillRepository).findByUserId("1");
    }

    @Test
    void getUserSkills_shouldThrowNoSuchEntityException_whenUserSkillsNotFound() {
        String userId = "1";
        when(userSkillRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> underTest.getUserSkills(userId));
        verify(userSkillRepository).findByUserId(userId);
    }

    @Test
    void createUserSkills_shouldCreateUserSkillsDocument_whenUserSkillsDocumentNotExist() {
        String userId = "1";
        List<String> skillIds = List.of("1", "2");
        when(userSkillRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(skillRepository.findAllById(skillIds)).thenReturn(skillDocuments);
        when(userSkillRepository.save(any(UserSkillsDocument.class))).thenReturn(userSkillsDocument);
        when(userSkillsMapper.toDto(userSkillsDocument)).thenReturn(userSkillsDto);

        UserSkillsDto result = underTest.createUserSkills(userId, skillIds);

        assertNotNull(result);
        assertEquals("1", result.getUserId());
        assertEquals(2, result.getSkills().size());
        assertEquals("Java", result.getSkills().getFirst().getName());
        assertEquals("Kotlin", result.getSkills().getLast().getName());
        verify(userSkillRepository).save(userSkillsDocument);
    }

    @Test
    void createUserSkills_shouldThrowInvalidInputException_whenUserSkillsExist() {
        String userId = "1";
        List<String> skillIds = List.of(userId);
        when(userSkillRepository.findByUserId(userId)).thenReturn(Optional.of(userSkillsDocument));

        assertThrows(InvalidInputException.class, () -> underTest.createUserSkills(userId, skillIds));
        verify(userSkillRepository).findByUserId(userId);
    }

    @Test
    void updateUserSkills_shouldUpdateUserSkills_whenUserSkillsExist() {
        String userId = "1";
        List<String> skillIds = List.of("1", "3");
        skillDocuments = List.of(buildSkillDoc("123456789123456789123456", "Java"),
                buildSkillDoc("123456789123456789123456", "C++"));
        skillDTOs = List.of(buildSkillDto("123456789123456789123456", "Java"),
                buildSkillDto("123456789123456789123456", "Kotlin"));
        userSkillsDto.setSkills(skillDTOs);
        when(userSkillRepository.findByUserId(userId)).thenReturn(Optional.of(userSkillsDocument));
        when(skillRepository.findAllById(skillIds)).thenReturn(skillDocuments);
        when(userSkillRepository.save(userSkillsDocument)).thenReturn(userSkillsDocument);
        when(userSkillsMapper.toDto(userSkillsDocument)).thenReturn(userSkillsDto);

        UserSkillsDto result = underTest.updateUserSkills(userId, skillIds);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(2, result.getSkills().size());
        matchSkillFields(skillDTOs.getFirst(), result.getSkills().getFirst());
        matchSkillFields(skillDTOs.getLast(), result.getSkills().getLast());
        verify(userSkillRepository).save(userSkillsDocument);
    }

    @Test
    void updateUserSkills_shouldThrowNoSuchEntityException_whenUserSkillsNotFound() {
        String skillId = "1";
        List<String> skillIds = List.of(skillId);
        when(userSkillRepository.findByUserId(skillId)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> underTest.updateUserSkills(skillId, skillIds));
        verify(userSkillRepository).findByUserId(skillId);
    }

    @Test
    void createUserSkills_shouldThrowInvalidInputException_whenPassedInvalidSkillIds() {
        String userId = "user1";
        List<String> invalidSkillIds = List.of("999");
        when(skillRepository.findAllById(invalidSkillIds)).thenReturn(List.of());
        assertThrows(InvalidInputException.class, () -> underTest.createUserSkills(userId, invalidSkillIds));
        verify(skillRepository).findAllById(invalidSkillIds);
    }

    @Test
    void updateUserSkills_shouldThrowInvalidInputException_whenPassedInvalidSkillIds() {
        String userId = "user1";
        List<String> invalidSkillIds = List.of("999");
        when(userSkillRepository.findByUserId(userId)).thenReturn(Optional.of(userSkillsDocument));
        when(skillRepository.findAllById(invalidSkillIds)).thenReturn(List.of());
        assertThrows(InvalidInputException.class, () -> underTest.updateUserSkills(userId, invalidSkillIds));

        verify(userSkillRepository).findByUserId(userId);
        verify(skillRepository).findAllById(invalidSkillIds);
    }

    private void matchSkillFields(SkillDto expected, SkillDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    private SkillDto buildSkillDto(String id, String name) {
        return new SkillDto(id, name);
    }

    private SkillDocument buildSkillDoc(String id, String name) {
        return SkillDocument.builder().id(new ObjectId(id)).name(name).build();
    }
}
