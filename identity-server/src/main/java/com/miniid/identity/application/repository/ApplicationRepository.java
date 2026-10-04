package com.miniid.identity.application.repository;

import com.miniid.identity.application.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository
        extends JpaRepository<Application, UUID> {

    Optional<Application> findByClientId(String clientId);

    Optional<Application> findByName(String name);

    boolean existsByClientId(String clientId);

    boolean existsByName(String name);
}