package ua.edu.internship.interview.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloWorldController {

    @GetMapping("/hello")
    public String hello(@RequestParam(name = "name", defaultValue = "User") String name) {
        return "Hello, " + name + "!";
    }
}
