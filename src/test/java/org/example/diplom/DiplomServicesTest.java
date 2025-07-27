package org.example.diplom;

import org.example.diplom.entity.Session;
import org.example.diplom.entity.User;
import org.example.diplom.exception.BadCredentials;
import org.example.diplom.exception.NotFoundData;
import org.example.diplom.exception.Unauthorized;
import org.example.diplom.repository.SessionRepository;
import org.example.diplom.repository.UserRepository;
import org.example.diplom.service.SessionService;
import org.example.diplom.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DiplomServicesTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private UserService userService;
    @InjectMocks
    private SessionService sessionService;

    final private String login = "user1";
    final private String password = "password1";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testUserService(){
        User user = new User(login, password);
        when(userRepository.save(user)).thenReturn(user);

        User newUser = userRepository.save(user);

        assertNotNull(newUser);
        assertThat(newUser.getLogin()).isEqualTo(login);

        assertThrows(NotFoundData.class, () -> {
            userService.getUserByLogin("wrongLogin");
        });

        assertThrows(BadCredentials.class, () -> {
            userService.verifyUsersPassword(newUser, "wrongPassword");
        });
    }

    @Test
    void testSessionService(){
        User newUser = userRepository.save(new User(login, password));
        Session session = new Session(newUser);

        when(sessionRepository.save(session)).thenReturn(session);
        Session newSession = sessionRepository.save(session);

        assertNotNull(newSession);
        assertThat(newSession.getUser()).isEqualTo(newUser);

        assertThrows(Unauthorized.class, () -> {
            sessionService.findById(UUID.randomUUID().toString());
        });
    }
}
