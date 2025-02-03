package com.example.userservice.Controller;

import com.example.userservice.Service.UserService;
import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import io.micrometer.core.annotation.Timed;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/")
public class UserController {
    private final Environment environment;

//    private final Environment environment;

//    @Value("${greeting.message}")
//    private String message;

    private Greeting greeting;
    private UserService userService;

    @GetMapping("/health-check")
    @Timed(value="users.status", longTask = true)
    public String status() {
        return String.format("It's Working in User Service"
                + "\n , port(local.server.port)=" + environment.getProperty("local.server.port")
                + "\n , port(server.port)=" + environment.getProperty("server.port")
                + "\n , token secret=" + environment.getProperty("token.secret")
                + "\n , token expiration time=" + environment.getProperty("token.expiration_time")
        );
    }
    @GetMapping("/welcome")
    @Timed(value="users.welcome", longTask = true)
    public String welcome(){
//        return environment.getProperty("greeting.message");
//        return message;
        return greeting.getMessage();
    }
    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = modelMapper.map(userDto, ResponseUser.class);
        return  ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }
    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers(){
        Iterable<UserEntity> userList = userService.getAllUsers();
        List<ResponseUser> responseUsers = new ArrayList<>();

        userList.forEach(v-> {
            responseUsers.add(new ModelMapper().map(v, ResponseUser.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(responseUsers);
    }

    @GetMapping(value = "/users/{userId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseUser> getUserById(@PathVariable("userId") String userId){
        UserDto userDto = userService.getUserByUserId(userId);
        ResponseUser responseUser = new ModelMapper().map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }

}
