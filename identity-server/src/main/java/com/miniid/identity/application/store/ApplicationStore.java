package com.miniid.identity.application.store;

import com.miniid.identity.application.model.Application;

import java.util.Optional;
import java.util.UUID;

public interface ApplicationStore {

    Optional<Application> findById(UUID id);

    Optional<Application> findByClientId(String clientId);

    Optional<Application> findByName(String name);

    boolean clientIdExists(String clientId);

    boolean nameExists(String name);

    Application save(Application application);
}