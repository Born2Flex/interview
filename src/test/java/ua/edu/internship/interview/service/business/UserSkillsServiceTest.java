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
import ua.edu.internship.interview.service.client.UserServiceClient;
import ua.edu.internship.interview.service.dto.skill.SkillDto;
import ua.edu.internship.interview.service.dto.user.UserDto;
import ua.edu.internship.interview.service.dto.user.skill.UserSkillsDto;
import ua.edu.internship.interview.service.mapper.UserSkillsMapper;
import ua.edu.internship.interview.service.utils.exceptions.InvalidInputException;
import ua.edu.internship.interview.service.utils.exceptions.NoSuchEntityException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import java.util.Collections;
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
    @Mock
    private UserServiceClient userClient;
    @InjectMocks
    private UserSkillsService underTest;

    private List<SkillDocument> skillDocuments;
    private List<SkillDto> skillDTOs;
    private UserSkillsDocument userSkillsDocument;
    private UserSkillsDto userSkillsDto;

    @BeforeEach
    void setUp() {
        skillDocuments = List.of(buildSkillDoc("123456789123456789123451", "Java"),
                buildSkillDoc("123456789123456789123452", "Kotlin"));
        skillDTOs = List.of(buildSkillDto("123456789123456789123451", "Java"),
                buildSkillDto("123456789123456789123452", "Kotlin"));
        userSkillsDocument = UserSkillsDocument.builder().userId(1L).skills(skillDocuments).build();
        userSkillsDto = UserSkillsDto.builder().userId(1L).skills(skillDTOs).build();
    }

    @Test
    void getUserSkills_shouldReturnUserSkillsDto_whenSkillsExist() {
        Long userId = 1L;
        when(userSkillRepository.findByUserId(userId)).thenReturn(Optional.of(userSkillsDocument));
        when(userSkillsMapper.toDto(userSkillsDocument)).thenReturn(userSkillsDto);

        UserSkillsDto result = underTest.getUserSkills(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(skillDTOs.size(), result.getSkills().size());
        matchSkillFields(skillDTOs.getFirst(), result.getSkills().getFirst());
        matchSkillFields(skillDTOs.getLast(), result.getSkills().getLast());
        verify(userSkillRepository).findByUserId(userId);
    }

    @Test
    void getUserSkills_shouldThrowNoSuchEntityException_whenUserSkillsNotFound() {
        Long userId = 999L;
        when(userSkillRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> underTest.getUserSkills(userId));
        verify(userSkillRepository).findByUserId(userId);
    }

    @Test
    void createUserSkills_shouldCreateUserSkillsDocument_whenUserSkillsDocumentNotExist() {
        Long userId = 1L;
        String skillId1 = "123456789123456789123451";
        String skillId2 = "123456789123456789123452";
        List<ObjectId> skillObjectIds = List.of(new ObjectId(skillId1), new ObjectId(skillId2));
        UserSkillsDocument createdDocument = UserSkillsDocument.builder().userId(userId).skills(skillDocuments).build();

        when(userClient.getById(userId)).thenReturn(Optional.of(new UserDto()));
        when(userSkillRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(skillRepository.findAllById(skillObjectIds)).thenReturn(skillDocuments);
        when(userSkillRepository.save(createdDocument)).thenReturn(userSkillsDocument);
        when(userSkillsMapper.toDto(userSkillsDocument)).thenReturn(userSkillsDto);

        UserSkillsDto result = underTest.createUserSkills(userId, List.of(skillId1, skillId2));

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(skillObjectIds.size(), result.getSkills().size());
        matchSkillFields(skillDTOs.getFirst(), result.getSkills().getFirst());
        matchSkillFields(skillDTOs.getLast(), result.getSkills().getLast());
        verify(userClient).getById(userId);
        verify(userSkillRepository).save(any(UserSkillsDocument.class));
        verify(skillRepository).findAllById(skillObjectIds);
        verify(userSkillsMapper).toDto(userSkillsDocument);
    }

    @Test
    void createUserSkills_shouldThrowInvalidInputException_whenUserSkillsExist() {
        Long userId = 1L;
        List<String> list = Collections.emptyList();

        when(userClient.getById(userId)).thenReturn(Optional.of(new UserDto()));
        when(userSkillRepository.findByUserId(userId)).thenReturn(Optional.of(userSkillsDocument));

        assertThrows(InvalidInputException.class, () -> underTest.createUserSkills(userId, list));
        verify(userClient).getById(userId);
        verify(userSkillRepository).findByUserId(userId);
    }

    @Test
    void updateUserSkills_shouldUpdateUserSkills_whenUserSkillsExist() {
        Long userId = 1L;
        String skillId1 = "123456789123456789123451";
        String skillId2 = "123456789123456789123453";
        List<String> skillIds = List.of(skillId1, skillId2);
        List<ObjectId> skillObjectIds = List.of(new ObjectId(skillId1), new ObjectId(skillId2));
        skillDocuments = List.of(buildSkillDoc("123456789123456789123451", "Java"),
                buildSkillDoc("123456789123456789123453", "C++"));
        skillDTOs = List.of(buildSkillDto("123456789123456789123451", "Java"),
                buildSkillDto("123456789123456789123453", "Kotlin"));
        userSkillsDto.setSkills(skillDTOs);
        UserSkillsDocument updatedDocument = UserSkillsDocument.builder()
                .id(userSkillsDocument.getId())
                .userId(userId)
                .skills(skillDocuments)
                .build();

        when(userClient.getById(userId)).thenReturn(Optional.of(new UserDto()));
        when(userSkillRepository.findByUserId(userId)).thenReturn(Optional.of(userSkillsDocument));
        when(skillRepository.findAllById(skillObjectIds)).thenReturn(skillDocuments);
        when(userSkillRepository.save(updatedDocument)).thenReturn(updatedDocument);
        when(userSkillsMapper.toDto(updatedDocument)).thenReturn(userSkillsDto);

        UserSkillsDto result = underTest.updateUserSkills(userId, skillIds);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(2, result.getSkills().size());
        matchSkillFields(skillDTOs.getFirst(), result.getSkills().getFirst());
        matchSkillFields(skillDTOs.getLast(), result.getSkills().getLast());
        verify(userClient).getById(userId);
        verify(userSkillRepository).findByUserId(userId);
        verify(skillRepository).findAllById(skillObjectIds);
        verify(userSkillRepository).save(userSkillsDocument);
        verify(userSkillsMapper).toDto(userSkillsDocument);
    }

    @Test
    void updateUserSkills_shouldThrowNoSuchEntityException_whenUserSkillsNotFound() {
        Long userId = 1L;
        List<String> emptyList = Collections.emptyList();
        when(userClient.getById(userId)).thenReturn(Optional.of(new UserDto()));
        when(userSkillRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> underTest.updateUserSkills(userId, emptyList));
        verify(userSkillRepository).findByUserId(userId);
    }

    @Test
    void createUserSkills_shouldThrowInvalidInputException_whenPassedInvalidSkillIds() {
        Long userId = 1L;
        String skillId = "123456789123456789123456";
        ObjectId skillObjectId = new ObjectId(skillId);
        List<String> invalidSkillIds = List.of(skillId);
        when(userClient.getById(userId)).thenReturn(Optional.of(new UserDto()));
        when(skillRepository.findAllById(List.of(skillObjectId))).thenReturn(List.of());

        assertThrows(InvalidInputException.class, () -> underTest.createUserSkills(userId, invalidSkillIds));
        verify(skillRepository).findAllById(List.of(skillObjectId));
    }

    @Test
    void updateUserSkills_shouldThrowInvalidInputException_whenPassedInvalidSkillIds() {
        Long userId = 1L;
        String skillId = "123456789123456789123456";
        ObjectId skillObjectId = new ObjectId(skillId);
        List<String> invalidSkillIds = List.of(skillId);
        when(userClient.getById(userId)).thenReturn(Optional.of(new UserDto()));
        when(userSkillRepository.findByUserId(userId)).thenReturn(Optional.of(userSkillsDocument));
        when(skillRepository.findAllById(List.of(skillObjectId))).thenReturn(List.of());

        assertThrows(InvalidInputException.class, () -> underTest.updateUserSkills(userId, invalidSkillIds));
        verify(userSkillRepository).findByUserId(userId);
        verify(skillRepository).findAllById(List.of(skillObjectId));
    }

    @Test
    void createUserSkills_shouldThrowNoSuchEntityException_whenUserNotFound() {
        Long userId = 1L;
        List<String> skillIds = List.of("123456789123456789123456");
        when(userClient.getById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> underTest.createUserSkills(userId, skillIds));

        verify(userClient).getById(userId);
    }

    @Test
    void updateUserSkills_shouldThrowNoSuchEntityException_whenUserNotFound() {
        Long userId = 1L;
        List<String> skillIds = List.of("123456789123456789123456");
        when(userClient.getById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> underTest.updateUserSkills(userId, skillIds));

        verify(userClient).getById(userId);
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
