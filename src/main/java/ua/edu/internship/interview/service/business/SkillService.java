package ua.edu.internship.interview.service.business;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import ua.edu.internship.interview.data.repository.SkillRepository;
import ua.edu.internship.interview.service.dto.SkillTreeDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final MongoTemplate mongoTemplate;
    private final SkillRepository skillRepository;

    public List<SkillTreeDto> getAllSkills() {
        List<SkillTreeDto> skills = skillRepository.findSkillTree();
        return skills;
    }
}
