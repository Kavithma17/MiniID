package com.miniid.identity.application.store.jpa;

import com.miniid.identity.application.model.Application;
import com.miniid.identity.application.repository.ApplicationRepository;
import com.miniid.identity.application.store.ApplicationStore;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class JpaApplicationStore implements ApplicationStore {

    private final ApplicationRepository applicationRepository;

    public JpaApplicationStore(
            ApplicationRepository applicationRepository) {

        this.applicationRepository = applicationRepository;
    }

    @Override
    public Optional<Application> findById(UUID id) {
        return applicationRepository.findById(id);
    }

    @Override
    public Optional<Application> findByClientId(String clientId) {
        return applicationRepository.findByClientId(clientId);
    }

    @Override
    public Optional<Application> findByName(String name) {
        return applicationRepository.findByName(name);
    }

    @Override
    public boolean clientIdExists(String clientId) {
        return applicationRepository.existsByClientId(clientId);
    }

    @Override
    public boolean nameExists(String name) {
        return applicationRepository.existsByName(name);
    }

    @Override
    public Application save(Application application) {
        return applicationRepository.save(application);
    }
}