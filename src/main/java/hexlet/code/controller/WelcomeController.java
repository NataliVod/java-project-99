package hexlet.code.controller;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@RestController
public class WelcomeController {

    @GetMapping(path = "/welcome")
    public String welcome() {
        return "Welcome to Spring!";
    }
}