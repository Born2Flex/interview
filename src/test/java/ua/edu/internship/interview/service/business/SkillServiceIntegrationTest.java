package ua.edu.internship.interview.service.business;

import org.bson.types.ObjectId;
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
import ua.edu.internship.interview.service.dto.skill.SkillTreeDto;
import ua.edu.internship.interview.service.utils.exceptions.NoSuchEntityException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@SpringBootTest
@Testcontainers
class SkillServiceIntegrationTest {
    @Container
    private static final MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:7.0.0"));
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SkillService skillService;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "testdb");
    }

    @BeforeEach
    void setUp() throws IOException {
        executeQueryFromFile("insert_skills.json");
    }

    @AfterEach
    void tearDown() throws IOException {
        executeQueryFromFile("delete_skills.json");
    }

    @Test
    void testGetAllSkillTrees() {
        List<SkillTreeDto> skillTrees = skillService.getAllSkillTrees();

        assertNotNull(skillTrees);
        assertEquals(3, skillTrees.size());
        assertEquals("Programming", skillTrees.getFirst().getName());
        assertEquals("Web Development", skillTrees.get(1).getName());
        assertEquals("Database Architecture", skillTrees.getLast().getName());
    }

    @Test
    void testGetSkillTreeByIdNotFound() {
        String invalidId = new ObjectId().toHexString();

        assertThrows(NoSuchEntityException.class, () -> skillService.getSkillTreeById(invalidId));
    }

    @Test
    void testGetSkillTreeByIdFound() {
        String objectId = "67483c5c8e8573107761acf3";
        SkillTreeDto retrievedSkillTree = skillService.getSkillTreeById(objectId);

        assertNotNull(retrievedSkillTree);
        assertEquals(objectId, retrievedSkillTree.getId());
        assertEquals("Database Architecture", retrievedSkillTree.getName());
    }

    private static final String FILE_PREFIX = "src/test/resources/data/";

    private void executeQueryFromFile(String fileName) throws IOException {
        String query = new String(Files.readAllBytes(Paths.get(FILE_PREFIX + fileName)));
        mongoTemplate.executeCommand(query);
    }
}
