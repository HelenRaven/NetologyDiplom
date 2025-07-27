package org.example.diplom.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue
    private long id;

    @Column(name = "uuid", columnDefinition = "VARCHAR(255)", updatable = false, nullable = false)
    private String uuid;

    public Session() { }

    public Session(User user) {
        this.user = user;
        this.uuid = UUID.randomUUID().toString();
    }

    public String getUuid() {
        return uuid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
