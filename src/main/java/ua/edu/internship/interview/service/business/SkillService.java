package ua.edu.internship.interview.service.business;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import ua.edu.internship.interview.data.repository.SkillRepository;
import ua.edu.internship.interview.service.dto.SkillTreeDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.graphLookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final MongoTemplate mongoTemplate;
    private final SkillRepository skillRepository;

    public List<SkillTreeDto> getAllSkills() {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("parentId").is(null)),
                graphLookup("skills")
                        .startWith("$_id")
                        .connectFrom("_id")
                        .connectTo("parentId")
                        .as("children")
        );

        AggregationResults<SkillTreeDto> results = mongoTemplate.aggregate(aggregation, "skills", SkillTreeDto.class);
        List<SkillTreeDto> topLevelSkills = results.getMappedResults();

        for (SkillTreeDto topLevelSkill : topLevelSkills) {
            List<SkillTreeDto> formattedSkills = formatSkillTree(topLevelSkill.getChildren());
            topLevelSkill.setChildren(formattedSkills.stream()
                    .filter(s -> s.getParentId().equals(topLevelSkill.getId()))
                    .toList());
        }
        return topLevelSkills;
    }

    private List<SkillTreeDto> formatSkillTree(List<SkillTreeDto> skills) {
        Map<String, SkillTreeDto> skillsMap = skills.stream()
                .collect(Collectors.toMap(SkillTreeDto::getId, identity()));
        for (SkillTreeDto skill : skills) {
            if (skill.getParentId() != null) {
                SkillTreeDto parentSkill = skillsMap.get(skill.getParentId());
                if (parentSkill != null) {
                    parentSkill.getChildren().add(skill);
                }
            }
        }
        return skills;
    }
}
