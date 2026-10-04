package com.miniid.identity.application.repository;

import com.miniid.identity.application.model.ApplicationRedirectUri;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRedirectUriRepository
        extends JpaRepository<ApplicationRedirectUri, UUID> {

    List<ApplicationRedirectUri> findByApplicationId(
            UUID applicationId
    );

    Optional<ApplicationRedirectUri> findByApplicationIdAndRedirectUri(
            UUID applicationId,
            String redirectUri
    );

    boolean existsByApplicationIdAndRedirectUri(
            UUID applicationId,
            String redirectUri
    );

    void deleteByApplicationIdAndRedirectUri(
            UUID applicationId,
            String redirectUri
    );
}