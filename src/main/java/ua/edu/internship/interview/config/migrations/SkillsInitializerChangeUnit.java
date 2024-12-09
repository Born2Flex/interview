package ua.edu.internship.interview.config.migrations;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import ua.edu.internship.interview.data.documents.SkillDocument;
import ua.edu.internship.interview.data.repository.SkillRepository;
import java.util.List;

@ChangeUnit(id = "initialize-skills", order = "1")
@RequiredArgsConstructor
public class SkillsInitializerChangeUnit {
    private final SkillRepository skillRepository;

    @Execution
    public void initializeSkillsHierarchy() {
        SkillDocument root = skillRepository.save(SkillDocument.builder().name("Programming").build());

        List<ObjectId> secondLevelSkills = saveChildSkills(root.getId(), List.of(
                "Web Development", "Mobile Development", "Game Development", "Programming Languages"
        ));

        saveChildSkills(secondLevelSkills.get(0), List.of(
                "Frontend Development", "Backend Development", "Full-stack Development", "UI/UX Design"
        ));

        saveChildSkills(secondLevelSkills.get(1), List.of(
                "iOS Development", "Android Development", "Cross-platform Development"
        ));

        saveChildSkills(secondLevelSkills.get(2), List.of(
                "Unity Development", "Unreal Engine Development", "Mobile Game Development"
        ));

        saveChildSkills(secondLevelSkills.get(3), List.of(
                "Java", "Python", "C++", "JavaScript", "Ruby", "Swift", "Kotlin", "PHP",
                "TypeScript", "Go", "Rust", "Perl", "C#"
        ));
    }

    private List<ObjectId> saveChildSkills(ObjectId parentId, List<String> skillNames) {
        List<SkillDocument> skills = skillNames.stream()
                .map(name -> createSkillWithParentId(name, parentId))
                .toList();
        return saveSkillsAndGetIds(skills);
    }

    private SkillDocument createSkillWithParentId(String name, ObjectId parentId) {
        return SkillDocument.builder().name(name).parentId(parentId).build();
    }

    private List<ObjectId> saveSkillsAndGetIds(List<SkillDocument> skills) {
        List<SkillDocument> savedSkills = skillRepository.saveAll(skills);
        return savedSkills.stream()
                .map(SkillDocument::getId)
                .toList();
    }

    @RollbackExecution
    public void rollback() {
        skillRepository.deleteAll();
    }
}
