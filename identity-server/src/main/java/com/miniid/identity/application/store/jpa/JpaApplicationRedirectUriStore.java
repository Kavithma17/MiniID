package com.miniid.identity.application.store.jpa;

import com.miniid.identity.application.model.ApplicationRedirectUri;
import com.miniid.identity.application.repository.ApplicationRedirectUriRepository;
import com.miniid.identity.application.store.ApplicationRedirectUriStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaApplicationRedirectUriStore
        implements ApplicationRedirectUriStore {

    private final ApplicationRedirectUriRepository repository;

    public JpaApplicationRedirectUriStore(
            ApplicationRedirectUriRepository repository) {

        this.repository = repository;
    }

    @Override
    public List<ApplicationRedirectUri> findByApplicationId(
            UUID applicationId) {

        return repository.findByApplicationId(applicationId);
    }

    @Override
    public Optional<ApplicationRedirectUri>
    findByApplicationIdAndRedirectUri(
            UUID applicationId,
            String redirectUri) {

        return repository
                .findByApplicationIdAndRedirectUri(
                        applicationId,
                        redirectUri
                );
    }

    @Override
    public boolean exists(
            UUID applicationId,
            String redirectUri) {

        return repository
                .existsByApplicationIdAndRedirectUri(
                        applicationId,
                        redirectUri
                );
    }

    @Override
    public ApplicationRedirectUri save(
            ApplicationRedirectUri redirectUri) {

        return repository.save(redirectUri);
    }

    @Override
    public void delete(
            UUID applicationId,
            String redirectUri) {

        repository.deleteByApplicationIdAndRedirectUri(
                applicationId,
                redirectUri
        );
    }
}