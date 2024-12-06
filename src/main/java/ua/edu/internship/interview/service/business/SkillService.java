package ua.edu.internship.interview.service.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.edu.internship.interview.data.repository.SkillRepository;
import ua.edu.internship.interview.service.dto.skill.SkillTreeDto;
import ua.edu.internship.interview.service.utils.exceptions.NoSuchEntityException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public List<SkillTreeDto> getAllSkillTrees() {
        List<SkillTreeDto> skills = skillRepository.findSkillTrees();
        log.info("Retrieved {} skill trees", skills.size());
        return skills;
    }

    public SkillTreeDto getSkillTreeById(String rootId) {
        SkillTreeDto rootSkill = skillRepository.findSkillTreeById(rootId)
                .orElseThrow(() -> new NoSuchEntityException("Skill not found"));
        log.info("Retrieved skill tree with root id: {}", rootId);
        return rootSkill;
    }
}
