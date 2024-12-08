package ua.edu.internship.interview.service.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.edu.internship.interview.data.documents.SkillDocument;
import ua.edu.internship.interview.data.documents.UserSkillsDocument;
import ua.edu.internship.interview.data.repository.SkillRepository;
import ua.edu.internship.interview.data.repository.UserSkillRepository;
import ua.edu.internship.interview.service.dto.user.skill.UserSkillsDto;
import ua.edu.internship.interview.service.mapper.UserSkillsMapper;
import ua.edu.internship.interview.service.utils.exceptions.InvalidInputException;
import ua.edu.internship.interview.service.utils.exceptions.NoSuchEntityException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSkillsService {
    private final UserSkillRepository userSkillRepository;
    private final SkillRepository skillRepository;
    private final UserSkillsMapper userSkillsMapper;

    public UserSkillsDto getUserSkills(String userId) {
        UserSkillsDocument userSkillsDocument = getSkillsByUserIdOrElseThrow(userId);
        log.info("Fetched {} skills for user with id: {}", userSkillsDocument.getSkills().size(), userId);
        return userSkillsMapper.toDto(userSkillsDocument);
    }

    public UserSkillsDto createUserSkills(String userId, List<String> skillIds) {
        log.info("Attempting to create skills document for user with id: {}", userId);
        validateUserSkillsNotExists(userId);
        List<SkillDocument> skills = getSkillsByIds(skillIds);
        UserSkillsDocument userSkillsDocument = UserSkillsDocument.builder().userId(userId).skills(skills).build();
        UserSkillsDocument savedUserSkills = userSkillRepository.save(userSkillsDocument);
        log.info("Created skills document with id: {}, for user with id: {}", savedUserSkills.getId(), userId);
        return userSkillsMapper.toDto(savedUserSkills);
    }

    public UserSkillsDto updateUserSkills(String userId, List<String> skillIds) {
        log.info("Attempting to update skills for user with id: {}", userId);
        UserSkillsDocument userSkillsDocument = getSkillsByUserIdOrElseThrow(userId);
        List<SkillDocument> skills = getSkillsByIds(skillIds);
        userSkillsDocument.setSkills(skills);
        UserSkillsDocument updatedUserSkills = userSkillRepository.save(userSkillsDocument);
        log.info("Updated skills for user with id: {}", userId);
        return userSkillsMapper.toDto(updatedUserSkills);
    }

    private void validateUserSkillsNotExists(String userId) {
        userSkillRepository.findByUserId(userId).ifPresent(this::throwInvalidInputException);
    }

    private void throwInvalidInputException(UserSkillsDocument document) {
        throw new InvalidInputException("User skills document already exists for this user");
    }

    private List<SkillDocument> getSkillsByIds(List<String> skillIds) {
        List<SkillDocument> skills = skillRepository.findAllById(skillIds);
        if (skills.size() != skillIds.size()) {
            throw new InvalidInputException("Provided invalid skill id");
        }
        return skills;
    }

    private UserSkillsDocument getSkillsByUserIdOrElseThrow(String userId) {
        return userSkillRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchEntityException("User skills not found"));
    }
}