package ua.edu.internship.interview.service.business;

import com.jupiter.tools.spring.test.mongo.annotation.MongoDataSet;
import com.jupiter.tools.spring.test.mongo.junit5.meta.annotation.MongoDbIntegrationTest;
import org.bson.types.ObjectId;
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
import ua.edu.internship.interview.data.documents.SkillDocument;
import ua.edu.internship.interview.data.repository.SkillRepository;
import ua.edu.internship.interview.service.dto.skill.SkillTreeDto;
import ua.edu.internship.interview.service.utils.exceptions.NoSuchEntityException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;

@SpringBootTest
@MongoDbIntegrationTest
@Testcontainers
class SkillServiceIntegrationTest {
    @Container
    private static final MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:7.0.0"));
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private SkillService skillService;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "testdb");
    }

    @Test
    void testGetAllSkillTrees() {
        List<SkillTreeDto> skillTrees = skillService.getAllSkillTrees();

        assertNotNull(skillTrees);
        assertEquals(1, skillTrees.size());
        assertEquals("Programming", skillTrees.getFirst().getName());
    }

    @Test
    void testGetSkillTreeByIdNotFound() {
        String invalidId = new ObjectId().toHexString();

        assertThrows(NoSuchEntityException.class, () -> skillService.getSkillTreeById(invalidId));
    }

    @Test
    @MongoDataSet(value = "/", cleanAfter = true)
    void testGetSkillTreeByIdFound() {
        ObjectId id = new ObjectId();
        SkillDocument skillDocument = SkillDocument.builder()
                .id(id)
                .name("Database Architecture")
                .build();
        skillRepository.save(skillDocument);

        SkillTreeDto retrievedSkillTree = skillService.getSkillTreeById(id.toHexString());

        assertNotNull(retrievedSkillTree);
        assertEquals(id.toHexString(), retrievedSkillTree.getId());
        assertEquals("Database Architecture", retrievedSkillTree.getName());
        skillRepository.deleteById(id.toHexString());
    }
}
