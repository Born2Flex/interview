package ua.edu.internship.interview.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ua.edu.internship.interview.service.client.UserServiceClient;
import ua.edu.internship.interview.service.utils.exceptions.NoSuchEntityException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {
    public final UserServiceClient userClient;

    public void validateUserExistsById(Long userId) {
        if (!userClient.existsById(userId)) {
            throw new NoSuchEntityException("User not found by id: " + userId);
        }
    }
}
