package org.example.diplom.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable=false)
    @NotBlank
    private String login;

    @Column(nullable=false)
    @NotBlank
    private String password;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Session> sessions;

    public User() { }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public @NotBlank String getLogin() {
        return login;
    }

    public void setLogin(@NotBlank String login) {
        this.login = login;
    }

    public @NotBlank String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank String password) {
        this.password = password;
    }
}
