package com.miniid.identity.application.service;

import com.miniid.identity.application.model.Application;
import com.miniid.identity.application.model.ApplicationRedirectUri;
import com.miniid.identity.application.store.ApplicationRedirectUriStore;
import com.miniid.identity.application.store.ApplicationStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationService {

    private static final int CLIENT_ID_BYTES = 24;

    private final ApplicationStore applicationStore;
    private final ApplicationRedirectUriStore redirectUriStore;
    private final SecureRandom secureRandom;

    public ApplicationService(
            ApplicationStore applicationStore,
            ApplicationRedirectUriStore redirectUriStore) {

        this.applicationStore = applicationStore;
        this.redirectUriStore = redirectUriStore;
        this.secureRandom = new SecureRandom();
    }

    @Transactional
    public Application createApplication(String name) {

        validateApplicationName(name);

        if (applicationStore.nameExists(name)) {
            throw new IllegalArgumentException(
                    "Application name already exists"
            );
        }

        String clientId = generateUniqueClientId();

        Application application =
                new Application(name, clientId);

        return applicationStore.save(application);
    }

    public Optional<Application> findById(UUID applicationId) {

        if (applicationId == null) {
            return Optional.empty();
        }

        return applicationStore.findById(applicationId);
    }

    public Optional<Application> findByClientId(String clientId) {

        if (clientId == null || clientId.isBlank()) {
            return Optional.empty();
        }

        return applicationStore.findByClientId(clientId);
    }

    @Transactional
    public Application enableApplication(UUID applicationId) {

        Application application =
                getRequiredApplication(applicationId);

        application.enable();

        return applicationStore.save(application);
    }

    @Transactional
    public Application disableApplication(UUID applicationId) {

        Application application =
                getRequiredApplication(applicationId);

        application.disable();

        return applicationStore.save(application);
    }

    @Transactional
    public ApplicationRedirectUri addRedirectUri(
            UUID applicationId,
            String redirectUri) {

        getRequiredApplication(applicationId);
        validateRedirectUri(redirectUri);

        if (redirectUriStore.exists(
                applicationId,
                redirectUri)) {

            throw new IllegalArgumentException(
                    "Redirect URI already registered"
            );
        }

        ApplicationRedirectUri registeredUri =
                new ApplicationRedirectUri(
                        applicationId,
                        redirectUri
                );

        return redirectUriStore.save(registeredUri);
    }

    public List<ApplicationRedirectUri> getRedirectUris(
            UUID applicationId) {

        getRequiredApplication(applicationId);

        return redirectUriStore
                .findByApplicationId(applicationId);
    }

    public boolean isRedirectUriRegistered(
            UUID applicationId,
            String redirectUri) {

        if (applicationId == null
                || redirectUri == null
                || redirectUri.isBlank()) {

            return false;
        }

        return redirectUriStore.exists(
                applicationId,
                redirectUri
        );
    }

    @Transactional
    public void removeRedirectUri(
            UUID applicationId,
            String redirectUri) {

        getRequiredApplication(applicationId);
        validateRedirectUri(redirectUri);

        if (!redirectUriStore.exists(
                applicationId,
                redirectUri)) {

            throw new IllegalArgumentException(
                    "Redirect URI is not registered"
            );
        }

        redirectUriStore.delete(
                applicationId,
                redirectUri
        );
    }

    private Application getRequiredApplication(
            UUID applicationId) {

        if (applicationId == null) {
            throw new IllegalArgumentException(
                    "Application ID is required"
            );
        }

        return applicationStore
                .findById(applicationId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Application not found"
                        )
                );
    }

    private void validateApplicationName(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Application name is required"
            );
        }

        if (name.length() > 150) {
            throw new IllegalArgumentException(
                    "Application name must not exceed 150 characters"
            );
        }
    }

    private void validateRedirectUri(String redirectUri) {

        if (redirectUri == null
                || redirectUri.isBlank()) {

            throw new IllegalArgumentException(
                    "Redirect URI is required"
            );
        }

        if (redirectUri.length() > 2048) {
            throw new IllegalArgumentException(
                    "Redirect URI must not exceed 2048 characters"
            );
        }

        try {
            URI uri = new URI(redirectUri);

            if (!uri.isAbsolute()) {
                throw new IllegalArgumentException(
                        "Redirect URI must be absolute"
                );
            }

            String scheme = uri.getScheme();

            if (!"https".equalsIgnoreCase(scheme)
                    && !"http".equalsIgnoreCase(scheme)) {

                throw new IllegalArgumentException(
                        "Redirect URI must use HTTP or HTTPS"
                );
            }

            if (uri.getHost() == null) {
                throw new IllegalArgumentException(
                        "Redirect URI must contain a host"
                );
            }

            if (uri.getFragment() != null) {
                throw new IllegalArgumentException(
                        "Redirect URI must not contain a fragment"
                );
            }

        } catch (URISyntaxException exception) {

            throw new IllegalArgumentException(
                    "Redirect URI is invalid"
            );
        }
    }

    private String generateUniqueClientId() {

        String clientId;

        do {
            byte[] randomBytes =
                    new byte[CLIENT_ID_BYTES];

            secureRandom.nextBytes(randomBytes);

            clientId = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(randomBytes);

        } while (
                applicationStore
                        .clientIdExists(clientId)
        );

        return clientId;
    }
}