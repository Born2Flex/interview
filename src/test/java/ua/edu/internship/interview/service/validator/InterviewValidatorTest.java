package ua.edu.internship.interview.service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.internship.interview.data.documents.InterviewDocument;
import ua.edu.internship.interview.data.enumeration.InterviewStatus;
import ua.edu.internship.interview.data.repository.InterviewRepository;
import ua.edu.internship.interview.service.utils.exceptions.InterviewCollisionException;
import ua.edu.internship.interview.service.utils.exceptions.InvalidStatusTransitionException;
import ua.edu.internship.interview.service.utils.exceptions.NoSuchEntityException;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static ua.edu.internship.interview.service.validator.InterviewValidator.TIME_WINDOW_OFFSET_IN_HOURS;

@ExtendWith(MockitoExtension.class)
class InterviewValidatorTest {
    @Mock
    private InterviewRepository interviewRepository;
    @Mock
    private UserValidator userValidator;
    @InjectMocks
    private InterviewValidator underTest;

    private LocalDateTime plannedTime;
    private LocalDateTime from;
    private LocalDateTime to;
    private InterviewDocument interviewDocument;

    @BeforeEach
    void setUp() {
        plannedTime = LocalDateTime.now();
        from = plannedTime.minusHours(TIME_WINDOW_OFFSET_IN_HOURS);
        to = plannedTime.plusHours(TIME_WINDOW_OFFSET_IN_HOURS);
        interviewDocument = InterviewDocument.builder()
                .interviewerId(1L)
                .candidateId(2L)
                .plannedTime(plannedTime)
                .build();
    }

    @Test
    @DisplayName("Should not throw exception when created interview is valid")
    void shouldNotThrowExceptionWhenCreatedInterviewIsValid() {
        // given
        doNothing().when(userValidator).validateUserExistsById(1L);
        doNothing().when(userValidator).validateUserExistsById(2L);
        when(interviewRepository.existsInterviewsInTimeWindow(1L, 2L, from, to)).thenReturn(false);

        // when
        // then
        assertDoesNotThrow(() -> underTest.validateInterviewCreation(interviewDocument));
    }

    @Test
    @DisplayName("Should throw NoSuchEntityException when interviewer doesn't exists")
    void shouldThrowExceptionWhenInterviewerDoesntExists() {
        // given
        doThrow(new NoSuchEntityException("User not found")).when(userValidator).validateUserExistsById(1L);

        // when
        // then
        assertThrows(NoSuchEntityException.class, () -> underTest.validateInterviewCreation(interviewDocument));
    }

    @Test
    @DisplayName("Should throw NoSuchEntityException when interviewer doesn't exists")
    void shouldThrowExceptionWhenCandidateDoesntExists() {
        // given
        doNothing().when(userValidator).validateUserExistsById(1L);
        doThrow(new NoSuchEntityException("User not found")).when(userValidator).validateUserExistsById(2L);

        // when
        // then
        assertThrows(NoSuchEntityException.class, () -> underTest.validateInterviewCreation(interviewDocument));
    }

    @Test
    @DisplayName("Should throw InterviewCollisionException when created interview causing conflicts")
    void shouldThrowExceptionWhenCreatedInterviewCausingConflicts() {
        // given
        doNothing().when(userValidator).validateUserExistsById(1L);
        doNothing().when(userValidator).validateUserExistsById(2L);
        when(interviewRepository.existsInterviewsInTimeWindow(1L, 2L, from, to)).thenReturn(true);

        // when
        // then
        assertThrows(InterviewCollisionException.class, () -> underTest.validateInterviewCreation(interviewDocument));
    }

    @Test
    @DisplayName("Should not throw exception when updated interview is valid")
    void shouldNotThrowExceptionWhenUpdatedInterviewIsValid() {
        // given
        when(interviewRepository.existsInterviewsInTimeWindow(1L, 2L, from, to)).thenReturn(false);

        // when
        // then
        assertDoesNotThrow(() -> underTest.validateInterviewUpdate(interviewDocument));
    }

    @Test
    @DisplayName("Should throw InterviewCollisionException when updated interview is invalid and causing conflicts")
    void shouldThrowExceptionWhenUpdatedInterviewIsInvalid() {
        // given
        when(interviewRepository.existsInterviewsInTimeWindow(1L, 2L, from, to)).thenReturn(true);

        // when
        // then
        assertThrows(InterviewCollisionException.class, () -> underTest.validateInterviewUpdate(interviewDocument));
    }

    @Test
    @DisplayName("Should not throw exception when interview not causing conflicts")
    void shouldNotThrowExceptionWhenInterviewNotCausingConflicts() {
        // given
        when(interviewRepository.existsInterviewsInTimeWindow(1L, 2L, from, to)).thenReturn(false);

        // when
        // then
        assertDoesNotThrow(() -> underTest.validateInterviewNotCausingConflicts(interviewDocument));
    }

    @Test
    @DisplayName("Should throw InterviewCollisionException when interview causing conflicts")
    void shouldThrowExceptionWhenInterviewCausingConflicts() {
        // given
        when(interviewRepository.existsInterviewsInTimeWindow(1L, 2L, from, to)).thenReturn(true);

        // when
        // then
        assertThrows(InterviewCollisionException.class, () -> underTest.validateInterviewNotCausingConflicts(interviewDocument));
    }

    @ParameterizedTest
    @EnumSource(InterviewStatus.class)
    @DisplayName("Should not throw exception when there is a valid interview status transition")
    void shouldReturnTrueForValidTransitions(InterviewStatus currentStatus) {
        for (InterviewStatus newStatus : currentStatus.getTransitions()) {
            assertDoesNotThrow(() -> underTest.validateInterviewStatusTransition(currentStatus, newStatus));
        }
    }

    @ParameterizedTest
    @EnumSource(InterviewStatus.class)
    @DisplayName("Should throw exception when trying to perform invalid interview status transition")
    void shouldReturnFalseForInvalidTransitions(InterviewStatus currentStatus) {
        for (InterviewStatus newStatus : InterviewStatus.values()) {
            if (!currentStatus.getTransitions().contains(newStatus)) {
                assertThrows(InvalidStatusTransitionException.class,
                        () -> underTest.validateInterviewStatusTransition(currentStatus, newStatus));
            }
        }
    }
}
