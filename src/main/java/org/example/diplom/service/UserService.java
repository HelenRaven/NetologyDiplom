package org.example.diplom.service;

import org.example.diplom.entity.User;
import org.example.diplom.exception.BadCredentials;
import org.example.diplom.exception.NotFoundData;
import org.example.diplom.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByLogin(String login) {
        User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new NotFoundData(String.format("User with login %s not found", login));
        }
        return user;
    }

    public boolean verifyUsersPassword(User user, String password){
        if (!Objects.equals(user.getPassword(), password)) {
            throw new BadCredentials("Wrong password");
        }
        return true;
    }

    public User saveUser(User user){
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new BadCredentials(e.getMessage());
        }
    }
}
