package be.appify.framework.common.security.service;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;

import be.appify.framework.common.security.domain.SimpleCredential;
import be.appify.framework.security.domain.Authentication;
import be.appify.framework.security.domain.User;
import be.appify.framework.security.repository.AuthenticationRepository;
import be.appify.framework.security.repository.UserRepository;
import be.appify.framework.security.service.AbstractAuthenticationService;
import be.appify.framework.security.service.NotAuthenticatedException;

public class SimpleAuthenticationService<U extends User> extends AbstractAuthenticationService<U, SimpleCredential<U>> {

	private static final String AUTHENTICATION_TOKEN = "authenticationToken";
	private final UserRepository<? super U> userRepository;
	private final AuthenticationRepository<U> authenticationRepository;

	@Inject
	public SimpleAuthenticationService(UserRepository<? super U> userRepository, AuthenticationRepository<U> authenticationRepository) {
		this.userRepository = userRepository;
		this.authenticationRepository = authenticationRepository;
	}

	@Override
	public Authentication<U> authenticate(SimpleCredential<U> credential, boolean keepAuthenticated) throws NotAuthenticatedException {
		@SuppressWarnings("unchecked")
		U user = (U) userRepository.findByEmailAddress(credential.getUsername());
		if (user == null) {
			throw new NotAuthenticatedException("Failed to authenticate user with e-mail address: " + credential.getUsername());
		}
		@SuppressWarnings("unchecked")
		SimpleCredential<U> actualCredential = user.getCredential(SimpleCredential.class);
		if (actualCredential == null || !actualCredential.checkPassword(credential)) {
			throw new NotAuthenticatedException("Failed to authenticate user with e-mail address: " + credential.getUsername());
		}
		Authentication<U> authentication = new Authentication<U>(user, Instant.now().plus(getTimeAuthenticationValid()));
		if (keepAuthenticated) {
			authenticationRepository.store(authentication);
			setCookie(AUTHENTICATION_TOKEN, authentication.getId(), (int) (getTimeAuthenticationValid().getMillis() / 1000));
		}
		return authentication;
	}

	@Override
	public Authentication<U> autoAuthenticate() throws NotAuthenticatedException {
		String authenticationToken = findCookie(AUTHENTICATION_TOKEN);
		if (StringUtils.isNotBlank(authenticationToken)) {
			Authentication<U> authentication = authenticationRepository.findByToken(authenticationToken);
			if (authentication != null) {
				if (!authentication.isExpired()) {
					return authentication;
				}
				cancel(authentication);
				throw new NotAuthenticatedException("Authentication for token " + authenticationToken + " expired on " + authentication.getExpiresOn());
			}
			throw new NotAuthenticatedException("Invalid authentication token: " + authenticationToken);
		}
		throw new NotAuthenticatedException("No authentication token found");
	}

	@Override
	public void cancel(Authentication<U> authentication) {
		authentication = authenticationRepository.findByToken(authentication.getId());
		if (authentication != null) {
			authenticationRepository.delete(authentication);
			deleteCookie(AUTHENTICATION_TOKEN);
		}
	}

	@Override
	public String getSignInURL(String target) {
		return "/sign-in?target=" + target;
	}

	@Override
	public String getSignOutURL() {
		return "/authenticated/sign-out";
	}
}
