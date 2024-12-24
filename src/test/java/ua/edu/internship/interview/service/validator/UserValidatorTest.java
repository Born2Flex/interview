package ua.edu.internship.interview.service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.internship.interview.service.client.UserServiceClient;
import ua.edu.internship.interview.service.utils.exceptions.NoSuchEntityException;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {
    @Mock
    private UserServiceClient userClient;
    @InjectMocks
    private UserValidator userValidator;

    @Test
    void validateUserExistsById_withValidUserId_shouldNotThrowException() {
        // given
        Long validUserId = 1L;
        when(userClient.existsById(validUserId)).thenReturn(true);

        // when
        // then
        assertDoesNotThrow(() -> userValidator.validateUserExistsById(validUserId));
    }

    @Test
    void validateUserExistsById_withInvalidUserId_shouldThrowNoSuchEntityException() {
        // given
        Long validUserId = 2L;
        when(userClient.existsById(validUserId)).thenReturn(false);

        // when
        // then
        assertThrows(NoSuchEntityException.class, () -> userValidator.validateUserExistsById(validUserId));
    }
}
