package ua.edu.internship.interview.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ua.edu.internship.interview.service.dto.user.UserDto;
import java.util.Optional;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserServiceClient {
    @GetMapping("/users/{id}")
    Optional<UserDto> getById(@PathVariable Long id);
}
