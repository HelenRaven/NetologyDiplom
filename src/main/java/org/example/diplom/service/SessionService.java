package org.example.diplom.service;

import org.example.diplom.entity.Session;
import org.example.diplom.exception.ServerError;
import org.example.diplom.exception.Unauthorized;
import org.example.diplom.repository.SessionRepository;
import org.springframework.stereotype.Service;


@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session findById(String id){
        return (Session) sessionRepository.findByUuid(id).orElseThrow(() -> new Unauthorized("Session not found"));
    }

    public void deleteByUuid(String uuid){
        try {
            sessionRepository.deleteByUuid(uuid);
        } catch (Exception e) {
            throw new ServerError(e.getMessage());
        }
    }
}
