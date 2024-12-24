package ua.edu.internship.interview.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ua.edu.internship.interview.data.documents.InterviewDocument;
import ua.edu.internship.interview.data.enumeration.InterviewStatus;
import ua.edu.internship.interview.data.repository.InterviewRepository;
import ua.edu.internship.interview.service.utils.exceptions.InterviewCollisionException;
import ua.edu.internship.interview.service.utils.exceptions.InvalidStatusTransitionException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterviewValidator {
    public static final int TIME_WINDOW_OFFSET_IN_HOURS = 1;
    private final InterviewRepository interviewRepository;
    private final UserValidator userValidator;

    public void validateInterviewCreation(InterviewDocument interview) {
        userValidator.validateUserExistsById(interview.getInterviewerId());
        userValidator.validateUserExistsById(interview.getCandidateId());
        validateInterviewNotCausingConflicts(interview);
    }

    public void validateInterviewUpdate(InterviewDocument interview) {
        validateInterviewNotCausingConflicts(interview);
    }

    public void validateInterviewNotCausingConflicts(InterviewDocument interview) {
        LocalDateTime plannedTime = interview.getPlannedTime();
        LocalDateTime from = plannedTime.minusHours(TIME_WINDOW_OFFSET_IN_HOURS);
        LocalDateTime to = plannedTime.plusHours(TIME_WINDOW_OFFSET_IN_HOURS);
        boolean existsInterviews = interviewRepository
                .existsInterviewsInTimeWindow(interview.getInterviewerId(), interview.getCandidateId(), from, to);
        if (existsInterviews) {
            String exceptionMessage =
                    String.format("Cannot create interview for interviewer with id '%s' and candidate with id '%s' " +
                                    "at %s. Interview conflicts with existing interviews.",
                            interview.getInterviewerId(), interview.getCandidateId(), plannedTime);
            throw new InterviewCollisionException(exceptionMessage);
        }
    }

    public void validateInterviewStatusTransition(InterviewStatus currentStatus, InterviewStatus newStatus) {
        if (!currentStatus.getTransitions().contains(newStatus)) {
            throw new InvalidStatusTransitionException("Invalid transition from " + currentStatus + " to " + newStatus);
        }
    }
}
