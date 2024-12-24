package ua.edu.internship.interview.service.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ua.edu.internship.interview.data.documents.InterviewDocument;
import ua.edu.internship.interview.data.documents.InterviewQuestionDocument;
import ua.edu.internship.interview.data.documents.UserQuestionDocument;
import ua.edu.internship.interview.data.enumeration.InterviewStatus;
import ua.edu.internship.interview.data.repository.InterviewRepository;
import ua.edu.internship.interview.data.repository.UserQuestionRepository;
import ua.edu.internship.interview.service.dto.interview.InterviewCreateDto;
import ua.edu.internship.interview.service.dto.interview.InterviewDto;
import ua.edu.internship.interview.service.dto.interview.InterviewUpdateDto;
import ua.edu.internship.interview.service.dto.interview.question.InterviewQuestionCreateDto;
import ua.edu.internship.interview.service.dto.interview.question.InterviewQuestionDto;
import ua.edu.internship.interview.service.dto.interview.question.InterviewQuestionUpdateDto;
import ua.edu.internship.interview.service.mapper.InterviewMapper;
import ua.edu.internship.interview.service.mapper.InterviewQuestionMapper;
import ua.edu.internship.interview.service.utils.exceptions.NoSuchEntityException;
import ua.edu.internship.interview.service.validator.InterviewValidator;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewService {
    private final InterviewRepository interviewRepository;
    private final UserQuestionRepository userQuestionRepository;
    private final InterviewMapper interviewMapper;
    private final InterviewQuestionMapper questionMapper;
    private final InterviewValidator validator;

    public List<InterviewDto> getAllInterviews() {
        List<InterviewDocument> interviews = interviewRepository.findAll();
        log.info("Retrieved {} interviews from database", interviews.size());
        return interviewMapper.toDto(interviews);
    }

    public InterviewDto getInterviewById(String id) {
        InterviewDocument interview = getInterviewByIdOrElseThrow(id);
        log.info("Retrieved interview with id: {}", id);
        return interviewMapper.toDto(interview);
    }

    public InterviewDto createInterview(InterviewCreateDto dto) {
        log.info("Attempting to create new interview");
        InterviewDocument interviewDocument = interviewMapper.toDocument(dto);
        validator.validateInterviewCreation(interviewDocument);
        interviewDocument.setStatus(InterviewStatus.PLANNED);
        InterviewDocument savedInterview = interviewRepository.save(interviewDocument);
        log.info("Created new interview with id: {}", savedInterview.getId());
        return interviewMapper.toDto(savedInterview);
    }

    public InterviewDto updateInterview(String id, InterviewUpdateDto dto) {
        log.info("Attempting to update interview with id: {}", id);
        InterviewDocument interviewDocument = getInterviewByIdOrElseThrow(id);
        interviewDocument = interviewMapper.updateDocument(interviewDocument, dto);
        validator.validateInterviewUpdate(interviewDocument);
        InterviewDocument updatedInterview = interviewRepository.save(interviewDocument);
        log.info("Updated interview with id: {}", updatedInterview.getId());
        return interviewMapper.toDto(updatedInterview);
    }

    public void deleteInterviewById(String interviewId) {
        log.info("Attempting to delete interview with id: {}", interviewId);
        interviewRepository.deleteById(new ObjectId(interviewId));
        log.info("Interview with id: {} deleted successfully", interviewId);
    }

    public InterviewDto updateInterviewStatus(String interviewId, InterviewStatus newStatus) {
        log.info("Attempting to update status for interview with id: {}", interviewId);
        InterviewDocument interviewDocument = getInterviewByIdOrElseThrow(interviewId);
        InterviewDocument updatedInterviewDocument = updateInterviewStatusAndTime(interviewDocument, newStatus);
        InterviewDocument savedInterviewDocument = interviewRepository.save(updatedInterviewDocument);
        log.info("Updated status for interview with id: {}", savedInterviewDocument.getId());
        return interviewMapper.toDto(savedInterviewDocument);
    }

    private InterviewDocument updateInterviewStatusAndTime(InterviewDocument interviewDocument, InterviewStatus newStatus) {
        validator.validateInterviewStatusTransition(interviewDocument.getStatus(), newStatus);
        interviewDocument.setStatus(newStatus);
        if (newStatus == InterviewStatus.ACTIVE) {
            interviewDocument.setStartTime(LocalDateTime.now());
        } else if (newStatus == InterviewStatus.COMPLETED) {
            interviewDocument.setEndTime(LocalDateTime.now());
        }
        return interviewDocument;
    }

    public InterviewDto updateInterviewFeedback(String interviewId, String feedback) {
        log.info("Attempting to update feedback for interview with id: {}", interviewId);
        InterviewDocument interviewDocument = getInterviewByIdOrElseThrow(interviewId);
        interviewDocument.setFeedback(feedback);
        InterviewDocument updatedInterview = interviewRepository.save(interviewDocument);
        log.info("Updated feedback for interview with id: {}", updatedInterview.getId());
        return interviewMapper.toDto(updatedInterview);
    }

    public InterviewQuestionDto createInterviewQuestion(String interviewId, InterviewQuestionCreateDto dto) {
        log.info("Attempting to create interview question for interview with id: {}", interviewId);
        InterviewDocument interviewDocument = getInterviewByIdOrElseThrow(interviewId);
        InterviewQuestionDocument questionDocument = createInterviewQuestionDocumentFromDto(dto);
        interviewDocument.addQuestion(questionDocument);
        interviewRepository.save(interviewDocument);
        log.info("Created interview question for interview with id: {}", interviewDocument.getId());
        return questionMapper.toDto(questionDocument);
    }

    private InterviewQuestionDocument createInterviewQuestionDocumentFromDto(InterviewQuestionCreateDto dto) {
        return InterviewQuestionDocument.builder()
                .question(getUserQuestionByIdOrElseThrow(dto.getUserQuestionId()))
                .build();
    }

    private UserQuestionDocument getUserQuestionByIdOrElseThrow(String id) {
        return userQuestionRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new NoSuchEntityException("User Question not found by id: " + id));
    }

    public InterviewQuestionDto updateInterviewQuestion(String interviewId, String questionId,
                                                        InterviewQuestionUpdateDto dto) {
        log.info("Attempting to update interview question for interview with id: {}", interviewId);
        InterviewDocument interviewDocument = getInterviewByIdOrElseThrow(interviewId);
        InterviewQuestionDocument questionDocument = getInterviewQuestionByIdOrElseThrow(questionId, interviewDocument);
        questionDocument = questionMapper.updateDocument(questionDocument, dto);
        interviewRepository.save(interviewDocument);
        log.info("Updated interview question for interview with id: {}", interviewDocument.getId());
        return questionMapper.toDto(questionDocument);
    }

    private InterviewQuestionDocument getInterviewQuestionByIdOrElseThrow(String questionId,
                                                                          InterviewDocument interviewDocument) {
        ObjectId questionObjectId = new ObjectId(questionId);
        return interviewDocument.getQuestions().stream()
                .filter(q -> q.getId().equals(questionObjectId))
                .findFirst()
                .orElseThrow(() -> new NoSuchEntityException("Interview Question not found by id: " + questionId));
    }

    public void deleteInterviewQuestionById(String interviewId, String questionId) {
        log.info("Attempting to delete interview question for interview with id: {}", interviewId);
        InterviewDocument interviewDocument = getInterviewByIdOrElseThrow(interviewId);
        ObjectId questionObjectId = new ObjectId(questionId);
        interviewDocument.getQuestions().removeIf(q -> q.getId().equals(questionObjectId));
        interviewRepository.save(interviewDocument);
        log.info("Deleted interview question for interview with id: {}", interviewId);
    }

    private InterviewDocument getInterviewByIdOrElseThrow(String id) {
        return interviewRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new NoSuchEntityException("Interview not found by id: " + id));
    }
}
