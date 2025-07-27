package org.example.diplom.controller;

import jakarta.validation.Valid;
import org.example.diplom.entity.Session;
import org.example.diplom.entity.TokenMessage;
import org.example.diplom.entity.User;
import org.example.diplom.exception.BadCredentials;
import org.example.diplom.repository.SessionRepository;
import org.example.diplom.service.SessionService;
import org.example.diplom.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("cloud")
public class UsersController {
    private final SessionRepository sessionRepository;
    private final UserService userService;
    private final SessionService sessionService;

    public UsersController(SessionRepository sessionRepository,
                           UserService userService, SessionService sessionService) {
        this.sessionRepository = sessionRepository;
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @PostMapping(value = "signin", produces = "application/json")
    public User addUser(@RequestBody @Valid User user){
        return userService.saveUser(user);
    }

    @PostMapping(value = "login", produces = "application/json")
    public ResponseEntity<TokenMessage> login(@RequestBody @Valid User userData) {
        User user = userService.getUserByLogin(userData.getLogin());
        boolean validCredentials = userService.verifyUsersPassword(user, userData.getPassword());
        if (validCredentials) {
            String token = String.valueOf(sessionRepository.save(new Session(user)).getUuid());
            return new ResponseEntity<>(new TokenMessage(token), HttpStatus.OK);
        } else {
            throw new BadCredentials("Bad credentials");
        }
    }

    @PostMapping(value ="logout", produces = "application/json")
    public ResponseEntity<String> logout(@RequestHeader("auth-token") String authToken){
        sessionService.deleteByUuid(authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
