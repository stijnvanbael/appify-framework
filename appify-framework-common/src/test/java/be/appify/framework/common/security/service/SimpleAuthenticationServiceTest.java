package be.appify.framework.common.security.service;

import be.appify.framework.common.security.domain.SimpleCredential;
import be.appify.framework.security.domain.Authentication;
import be.appify.framework.security.domain.Credential;
import be.appify.framework.security.domain.User;
import be.appify.framework.security.repository.AuthenticationRepository;
import be.appify.framework.security.repository.UserRepository;
import be.appify.framework.security.service.AuthenticationService;
import be.appify.framework.security.service.NotAuthenticatedException;
import com.google.common.collect.Sets;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SimpleAuthenticationServiceTest {
    private AuthenticationService<User, SimpleCredential<User>> authenticationService;

    @Mock
    private UserRepository<User> userRepository;
    @Mock
    private AuthenticationRepository<User> authenticationRepository;

    private SimpleCredential<User> credential;
    private User user;

    @Before
    public void before() {
        authenticationService = new SimpleAuthenticationService<>(userRepository, authenticationRepository);
        credential = new SimpleCredential<>("ridley.scott@gmail.com", "t1g3r");
        user = User.newBuilder()
                .firstName("Ridley")
                .lastName("Scott")
                .credentials(Sets.<Credential<?>>newHashSet(credential))
                .emailAddress("ridley.scott@gmail.com")
                .build();
        when(userRepository.findByEmailAddress("ridley.scott@gmail.com")).thenReturn(user);
    }

    @Test
    public void shouldAuthenticateUsingCredential() throws NotAuthenticatedException {
        Authentication<User> authentication = authenticationService.authenticate(credential, false);
        assertNotNull(authentication);
        assertEquals(user, authentication.getUser());
        assertFalse(authentication.isExpired());
        verify(authenticationRepository, never()).store(authentication);
    }

    @Test
    public void shouldStoreAuthenticationWhenKeepAuthenticatedIsTrue() throws NotAuthenticatedException {
        Authentication<User> authentication = authenticationService.authenticate(credential, true);
        assertNotNull(authentication);
        assertEquals(user, authentication.getUser());
        assertFalse(authentication.isExpired());
        verify(authenticationRepository).store(authentication);
    }

    @Test
    public void shouldCancelExistingAuthentication() throws NotAuthenticatedException {
        Authentication<User> existingAuthentication = new Authentication<>(user, Instant.now().plus(Duration.standardDays(365)));
        when(authenticationRepository.findByUser(user)).thenReturn(existingAuthentication);
        when(authenticationRepository.findByToken(existingAuthentication.getId())).thenReturn(existingAuthentication);

        Authentication<User> authentication = authenticationService.authenticate(credential, true);
        assertNotNull(authentication);
        assertEquals(user, authentication.getUser());
        assertFalse(authentication.isExpired());
        verify(authenticationRepository).delete(existingAuthentication);
        verify(authenticationRepository).store(authentication);
    }

    @Test(expected = NotAuthenticatedException.class)
    public void shouldThrowExceptionForUnknownUser() throws NotAuthenticatedException {
        credential = new SimpleCredential<>("unknown.user@gmail.com", "p4ssw0rd");
        authenticationService.authenticate(credential, false);
    }

    @Test(expected = NotAuthenticatedException.class)
    public void shouldThrowExceptionForWrongPassword() throws NotAuthenticatedException {
        credential = new SimpleCredential<>("ridley.scott@gmail.com", "wr0ngp4ssw0rd");
        authenticationService.authenticate(credential, false);
    }

}
