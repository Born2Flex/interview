package ua.edu.internship.interview.service.business;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ua.edu.internship.interview.data.repository.SkillRepository;
import ua.edu.internship.interview.data.repository.UserSkillRepository;
import ua.edu.internship.interview.service.BaseIntegrationTest;
import ua.edu.internship.interview.service.dto.user.skill.UserSkillsDto;
import ua.edu.internship.interview.service.mapper.UserSkillsMapper;
import ua.edu.internship.interview.service.utils.exceptions.InvalidInputException;
import ua.edu.internship.interview.service.utils.exceptions.NoSuchEntityException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.util.List;

@SpringBootTest
@Testcontainers
class UserSkillsServiceIntegrationTest extends BaseIntegrationTest {
    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7.0.0"));
    @Autowired
    private UserSkillsService userSkillsService;
    @Autowired
    private UserSkillRepository userSkillRepository;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private UserSkillsMapper userSkillsMapper;

    public UserSkillsServiceIntegrationTest(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "testdb");
    }

    @BeforeEach
    void setUp() throws IOException {
        executeQueryFromFile("insert_skills.json");
        executeQueryFromFile("insert_user_skills.json");
    }

    @AfterEach
    void tearDown() throws IOException {
        executeQueryFromFile("delete_user_skills.json");
        executeQueryFromFile("delete_skills.json");
    }

    @Test
    void testGetUserSkills() {
        String userId = "user123";

        UserSkillsDto userSkillsDto = userSkillsService.getUserSkills(userId);

        assertNotNull(userSkillsDto);
        assertEquals(userId, userSkillsDto.getUserId());
        assertEquals(3, userSkillsDto.getSkills().size());
    }

    @Test
    void testCreateUserSkills() {
        String userId = "user456";
        List<String> skillIds = List.of("skill1", "skill2", "skill3");

        UserSkillsDto createdUserSkills = userSkillsService.createUserSkills(userId, skillIds);

        assertNotNull(createdUserSkills);
        assertEquals(userId, createdUserSkills.getUserId());
        assertEquals(skillIds.size(), createdUserSkills.getSkills().size());
    }

    @Test
    void testCreateUserSkillsAlreadyExists() {
        String userId = "user123";
        List<String> skillIds = List.of("skill1", "skill2");

        assertThrows(InvalidInputException.class, () -> userSkillsService.createUserSkills(userId, skillIds));
    }

    @Test
    void testUpdateUserSkills() {
        String userId = "user123";
        List<String> skillIds = List.of("skill4", "skill5");

        UserSkillsDto updatedUserSkills = userSkillsService.updateUserSkills(userId, skillIds);

        assertNotNull(updatedUserSkills);
        assertEquals(userId, updatedUserSkills.getUserId());
        assertEquals(skillIds.size(), updatedUserSkills.getSkills().size());
    }

    @Test
    void testUpdateUserSkillsNotFound() {
        String userId = "nonExistingUser";
        List<String> skillIds = List.of("skill1", "skill2");

        assertThrows(NoSuchEntityException.class, () -> userSkillsService.updateUserSkills(userId, skillIds));
    }
}
