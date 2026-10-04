package com.miniid.identity.application.service;

import com.miniid.identity.application.model.Application;
import com.miniid.identity.application.model.ApplicationRedirectUri;
import com.miniid.identity.application.store.ApplicationRedirectUriStore;
import com.miniid.identity.application.store.ApplicationStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ApplicationServiceTest {

    private ApplicationStore applicationStore;
    private ApplicationService applicationService;
    private ApplicationRedirectUriStore redirectUriStore;
@BeforeEach
void setUp() {

    applicationStore =
            mock(ApplicationStore.class);

    redirectUriStore =
            mock(ApplicationRedirectUriStore.class);

    applicationService =
            new ApplicationService(
                    applicationStore,
                    redirectUriStore
            );
}

    @Test
    void shouldCreateApplication() {

        when(applicationStore.nameExists("My Web App"))
                .thenReturn(false);

        when(applicationStore.clientIdExists(anyString()))
                .thenReturn(false);

        when(applicationStore.save(any(Application.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Application result =
                applicationService.createApplication("My Web App");

        assertNotNull(result);
        assertEquals("My Web App", result.getName());
        assertNotNull(result.getClientId());
        assertFalse(result.getClientId().isBlank());
        assertTrue(result.isEnabled());

        verify(applicationStore)
                .nameExists("My Web App");

        verify(applicationStore)
                .clientIdExists(result.getClientId());

        verify(applicationStore)
                .save(any(Application.class));
    }

    @Test
    void shouldRejectDuplicateApplicationName() {

        when(applicationStore.nameExists("My Web App"))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> applicationService
                                .createApplication("My Web App")
                );

        assertEquals(
                "Application name already exists",
                exception.getMessage()
        );

        verify(applicationStore)
                .nameExists("My Web App");

        verify(applicationStore, never())
                .save(any());
    }

    @Test
    void shouldRejectBlankApplicationName() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> applicationService
                                .createApplication(" ")
                );

        assertEquals(
                "Application name is required",
                exception.getMessage()
        );

        verifyNoInteractions(applicationStore);
    }

    @Test
    void shouldRejectApplicationNameLongerThan150Characters() {

        String name = "a".repeat(151);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> applicationService
                                .createApplication(name)
                );

        assertEquals(
                "Application name must not exceed 150 characters",
                exception.getMessage()
        );

        verifyNoInteractions(applicationStore);
    }

    @Test
    void shouldFindApplicationById() throws Exception {

        UUID applicationId = UUID.randomUUID();

        Application application =
                new Application(
                        "My Web App",
                        "client-id"
                );

        setApplicationId(
                application,
                applicationId
        );

        when(applicationStore.findById(applicationId))
                .thenReturn(Optional.of(application));

        Optional<Application> result =
                applicationService.findById(applicationId);

        assertTrue(result.isPresent());
        assertEquals(
                applicationId,
                result.get().getId()
        );

        verify(applicationStore)
                .findById(applicationId);
    }

    @Test
    void shouldFindApplicationByClientId() {

        Application application =
                new Application(
                        "My Web App",
                        "client-id"
                );

        when(applicationStore.findByClientId("client-id"))
                .thenReturn(Optional.of(application));

        Optional<Application> result =
                applicationService
                        .findByClientId("client-id");

        assertTrue(result.isPresent());
        assertEquals(
                "client-id",
                result.get().getClientId()
        );

        verify(applicationStore)
                .findByClientId("client-id");
    }

    @Test
    void shouldDisableApplication() throws Exception {

        UUID applicationId = UUID.randomUUID();

        Application application =
                new Application(
                        "My Web App",
                        "client-id"
                );

        setApplicationId(
                application,
                applicationId
        );

        when(applicationStore.findById(applicationId))
                .thenReturn(Optional.of(application));

        when(applicationStore.save(application))
                .thenReturn(application);

        Application result =
                applicationService
                        .disableApplication(applicationId);

        assertFalse(result.isEnabled());

        verify(applicationStore)
                .findById(applicationId);

        verify(applicationStore)
                .save(application);
    }

    @Test
    void shouldEnableApplication() throws Exception {

        UUID applicationId = UUID.randomUUID();

        Application application =
                new Application(
                        "My Web App",
                        "client-id"
                );

        setApplicationId(
                application,
                applicationId
        );

        application.disable();

        when(applicationStore.findById(applicationId))
                .thenReturn(Optional.of(application));

        when(applicationStore.save(application))
                .thenReturn(application);

        Application result =
                applicationService
                        .enableApplication(applicationId);

        assertTrue(result.isEnabled());

        verify(applicationStore)
                .findById(applicationId);

        verify(applicationStore)
                .save(application);
    }

    @Test
    void shouldFailWhenManagingUnknownApplication() {

        UUID applicationId = UUID.randomUUID();

        when(applicationStore.findById(applicationId))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> applicationService
                                .disableApplication(applicationId)
                );

        assertEquals(
                "Application not found",
                exception.getMessage()
        );

        verify(applicationStore)
                .findById(applicationId);

        verify(applicationStore, never())
                .save(any());
    }

    private void setApplicationId(
            Application application,
            UUID applicationId) throws Exception {

        Field idField =
                Application.class.getDeclaredField("id");

        idField.setAccessible(true);
        idField.set(application, applicationId);
    }
    @Test
void shouldAddRedirectUri() throws Exception {

    UUID applicationId = UUID.randomUUID();

    Application application =
            new Application("My Web App", "client-id");

    setApplicationId(application, applicationId);

    String redirectUri =
            "https://example.com/callback";

    when(applicationStore.findById(applicationId))
            .thenReturn(Optional.of(application));

    when(redirectUriStore.exists(
            applicationId,
            redirectUri))
            .thenReturn(false);

    when(redirectUriStore.save(
            any(ApplicationRedirectUri.class)))
            .thenAnswer(invocation ->
                    invocation.getArgument(0));

    ApplicationRedirectUri result =
            applicationService.addRedirectUri(
                    applicationId,
                    redirectUri
            );

    assertEquals(
            applicationId,
            result.getApplicationId()
    );

    assertEquals(
            redirectUri,
            result.getRedirectUri()
    );

    verify(redirectUriStore)
            .exists(applicationId, redirectUri);

    verify(redirectUriStore)
            .save(any(ApplicationRedirectUri.class));
}

@Test
void shouldRejectDuplicateRedirectUri() throws Exception {

    UUID applicationId = UUID.randomUUID();

    Application application =
            new Application("My Web App", "client-id");

    setApplicationId(application, applicationId);

    String redirectUri =
            "https://example.com/callback";

    when(applicationStore.findById(applicationId))
            .thenReturn(Optional.of(application));

    when(redirectUriStore.exists(
            applicationId,
            redirectUri))
            .thenReturn(true);

    IllegalArgumentException exception =
            assertThrows(
                    IllegalArgumentException.class,
                    () -> applicationService.addRedirectUri(
                            applicationId,
                            redirectUri
                    )
            );

    assertEquals(
            "Redirect URI already registered",
            exception.getMessage()
    );

    verify(redirectUriStore, never())
            .save(any());
}

@Test
void shouldRejectRelativeRedirectUri() throws Exception {

    UUID applicationId = UUID.randomUUID();

    Application application =
            new Application("My Web App", "client-id");

    setApplicationId(application, applicationId);

    when(applicationStore.findById(applicationId))
            .thenReturn(Optional.of(application));

    IllegalArgumentException exception =
            assertThrows(
                    IllegalArgumentException.class,
                    () -> applicationService.addRedirectUri(
                            applicationId,
                            "/callback"
                    )
            );

    assertEquals(
            "Redirect URI must be absolute",
            exception.getMessage()
    );

    verify(redirectUriStore, never())
            .save(any());
}

@Test
void shouldRejectRedirectUriWithFragment() throws Exception {

    UUID applicationId = UUID.randomUUID();

    Application application =
            new Application("My Web App", "client-id");

    setApplicationId(application, applicationId);

    when(applicationStore.findById(applicationId))
            .thenReturn(Optional.of(application));

    IllegalArgumentException exception =
            assertThrows(
                    IllegalArgumentException.class,
                    () -> applicationService.addRedirectUri(
                            applicationId,
                            "https://example.com/callback#fragment"
                    )
            );

    assertEquals(
            "Redirect URI must not contain a fragment",
            exception.getMessage()
    );

    verify(redirectUriStore, never())
            .save(any());
}

@Test
void shouldCheckExactRedirectUriRegistration() {

    UUID applicationId = UUID.randomUUID();

    String registeredUri =
            "https://example.com/callback";

    when(redirectUriStore.exists(
            applicationId,
            registeredUri))
            .thenReturn(true);

    assertTrue(
            applicationService.isRedirectUriRegistered(
                    applicationId,
                    registeredUri
            )
    );

    assertFalse(
            applicationService.isRedirectUriRegistered(
                    applicationId,
                    "https://example.com/callback/evil"
            )
    );

    verify(redirectUriStore)
            .exists(
                    applicationId,
                    registeredUri
            );

    verify(redirectUriStore)
            .exists(
                    applicationId,
                    "https://example.com/callback/evil"
            );
}

@Test
void shouldGetRegisteredRedirectUris() throws Exception {

    UUID applicationId = UUID.randomUUID();

    Application application =
            new Application("My Web App", "client-id");

    setApplicationId(application, applicationId);

    ApplicationRedirectUri first =
            new ApplicationRedirectUri(
                    applicationId,
                    "https://example.com/callback"
            );

    ApplicationRedirectUri second =
            new ApplicationRedirectUri(
                    applicationId,
                    "https://example.com/login/callback"
            );

    when(applicationStore.findById(applicationId))
            .thenReturn(Optional.of(application));

    when(redirectUriStore.findByApplicationId(applicationId))
            .thenReturn(List.of(first, second));

    List<ApplicationRedirectUri> result =
            applicationService.getRedirectUris(
                    applicationId
            );

    assertEquals(2, result.size());

    assertEquals(
            "https://example.com/callback",
            result.get(0).getRedirectUri()
    );

    assertEquals(
            "https://example.com/login/callback",
            result.get(1).getRedirectUri()
    );
}

@Test
void shouldRemoveRegisteredRedirectUri() throws Exception {

    UUID applicationId = UUID.randomUUID();

    Application application =
            new Application("My Web App", "client-id");

    setApplicationId(application, applicationId);

    String redirectUri =
            "https://example.com/callback";

    when(applicationStore.findById(applicationId))
            .thenReturn(Optional.of(application));

    when(redirectUriStore.exists(
            applicationId,
            redirectUri))
            .thenReturn(true);

    applicationService.removeRedirectUri(
            applicationId,
            redirectUri
    );

    verify(redirectUriStore)
            .delete(
                    applicationId,
                    redirectUri
            );
}
}