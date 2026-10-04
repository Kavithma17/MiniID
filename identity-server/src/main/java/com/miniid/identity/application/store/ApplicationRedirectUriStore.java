package com.miniid.identity.application.store;

import com.miniid.identity.application.model.ApplicationRedirectUri;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRedirectUriStore {

    List<ApplicationRedirectUri> findByApplicationId(
            UUID applicationId
    );

    Optional<ApplicationRedirectUri>
    findByApplicationIdAndRedirectUri(
            UUID applicationId,
            String redirectUri
    );

    boolean exists(
            UUID applicationId,
            String redirectUri
    );

    ApplicationRedirectUri save(
            ApplicationRedirectUri redirectUri
    );

    void delete(
            UUID applicationId,
            String redirectUri
    );
}