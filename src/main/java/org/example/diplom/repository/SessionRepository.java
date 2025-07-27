package org.example.diplom.repository;

import jakarta.transaction.Transactional;
import org.example.diplom.entity.Session;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends CrudRepository<Session, Long> {
    Optional<Object> findByUuid(String id);

    @Transactional
    void deleteByUuid(String authToken);
}
