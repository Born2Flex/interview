package ua.edu.internship.interview.service.utils.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Setter
public abstract class BaseException extends RuntimeException {
    private HttpStatus code;

    protected BaseException(String message, HttpStatus code) {
        super(message);
        this.code = code;
    }
}
