package com.example.firstservice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/first-service")
@Slf4j
@AllArgsConstructor
public class FirstServiceController {

    private final Environment environment;

    @RequestMapping("/welcome")
    public String Welcome() {
        return "Welcome to First Service";

    }
    @GetMapping("/message")
    public String message(@RequestHeader("first-request")String header){
        log.info(header);
        return "Hello World in First Service";
    }
    @GetMapping("/check")
    public String check(HttpServletRequest request) {
        log.info("Server port= {}",request.getServerPort());
        return String.format("Hi there, This is a message from First Service on Port %s" ,environment.getProperty("local.server.port"));
    }
}
