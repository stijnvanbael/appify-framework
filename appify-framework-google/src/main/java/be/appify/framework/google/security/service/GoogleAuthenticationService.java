package be.appify.framework.google.security.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;

import be.appify.framework.common.service.RemoteException;
import be.appify.framework.domain.ReflectionBuilder;
import be.appify.framework.google.security.domain.GoogleAccessToken;
import be.appify.framework.google.security.domain.GoogleCredential;
import be.appify.framework.google.security.domain.GoogleProfile;
import be.appify.framework.google.security.repository.GoogleCredentialRepository;
import be.appify.framework.google.security.service.profile.GoogleProfileService;
import be.appify.framework.google.security.service.token.GoogleAccessTokenService;
import be.appify.framework.security.domain.Authentication;
import be.appify.framework.security.domain.User;
import be.appify.framework.security.domain.UserFactory;
import be.appify.framework.security.repository.UserRepository;
import be.appify.framework.security.service.AbstractAuthenticationService;
import be.appify.framework.security.service.NotAuthenticatedException;
import be.appify.framework.util.TypeBuilder;

public class GoogleAuthenticationService<U extends User>
		extends AbstractAuthenticationService<U, GoogleCredential<U>> {
	private static final String CREDENTIAL_TOKEN = "credentialToken";

	private static final String UTF_8 = "UTF-8";
	private static final String AUTHENTICATION_URL = "https://accounts.google.com/o/oauth2/auth?"
			+ "response_type=code&"
			+ "client_id=%1$s&"
			+ "redirect_uri=%2$s&"
			+ "scope=%3$s&"
			+ "state=%4$s";

	private String clientId;
	private String redirectURI = "/google/oauth2/callback";
	private GoogleAPIScope[] scopes;
	private GoogleCredentialRepository<U> credentialRepository;
	private boolean signUpEnabled = false;

	private GoogleAccessTokenService accessTokenService;
	private GoogleProfileService profileService;

	private UserRepository<U> userRepository;
	private UserFactory<U> userFactory;

	protected GoogleAuthenticationService() {
	}

	public void setRedirectURI(String redirectURI) {
		this.redirectURI = redirectURI;
	}

	public void setSignUpEnabled(boolean signUpEnabled) {
		this.signUpEnabled = signUpEnabled;
	}

	@Override
	public Authentication<U> authenticate(GoogleCredential<U> credential, boolean keepAuthenticated) throws NotAuthenticatedException {
		return autoAuthenticate(credential.getCode());
	}

	@Override
	public Authentication<U> autoAuthenticate() throws NotAuthenticatedException {
		String code = findCookie(CREDENTIAL_TOKEN);
		if (StringUtils.isNotBlank(code)) {
			return autoAuthenticate(code);
		}
		throw new NotAuthenticatedException("No authentication token found");
	}

	private Authentication<U> autoAuthenticate(String code) throws NotAuthenticatedException {
		GoogleCredential<U> credential = credentialRepository.findByCode(code);
		U user = null;
		if (credential != null) {
			user = credential.getUser();
		} else {
			if (signUpEnabled) {
				user = createUser(credential);
			} else {
				throw new NotAuthenticatedException("Invalid authentication code: " + code);
			}
		}
		return new Authentication<U>(user, Instant.now().plus(getTimeAuthenticationValid()));
	}

	private U createUser(GoogleCredential<U> credential) throws NotAuthenticatedException {
		GoogleAccessToken accessToken;
		GoogleProfile profile;
		try {
			accessToken = accessTokenService.requestAccess(credential.getCode());
		} catch (RemoteException e) {
			throw new NotAuthenticatedException("Failed to obtain token for code: " + credential.getCode(), e);
		}
		try {
			profile = profileService.getProfile(accessToken);
		} catch (RemoteException e) {
			throw new NotAuthenticatedException("Failed to obtain user profile for code: " + credential.getCode(), e);
		}
		U user = userFactory.createUser(profile.getFirstName(), profile.getLastName(), profile.getEmailAddress());
		user.addCredential(credential);
		userRepository.store(user);
		return user;
	}

	@Override
	public String getSignInURL(String target) {
		try {
			return String.format(AUTHENTICATION_URL,
					URLEncoder.encode(clientId, UTF_8),
					URLEncoder.encode(redirectURI, UTF_8),
					URLEncoder.encode(asString(scopes), UTF_8),
					URLEncoder.encode(target, UTF_8));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private String asString(GoogleAPIScope[] scopes) {
		StringBuilder builder = new StringBuilder();
		for (GoogleAPIScope scope : scopes) {
			if (builder.length() > 0) {
				builder.append(" ");
			}
			builder.append(scope.url());
		}
		return null;
	}

	@Override
	public String getSignOutURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancel(Authentication<U> authentication) {
		// TODO Auto-generated method stub

	}

	public static <U extends User> Builder<U> create() {
		return new Builder<U>();
	}

	public static class Builder<U extends User> extends ReflectionBuilder<GoogleAuthenticationService<U>, Builder<U>> {
		protected Builder() {
			super(new TypeBuilder<GoogleAuthenticationService<U>>() {
			}.build());
		}

		public Builder<U> credentialRepository(@NotNull GoogleCredentialRepository<U> credentialRepository) {
			return set("credentialRepository", credentialRepository);
		}

		public Builder<U> userRepository(@NotNull UserRepository<U> userRepository) {
			return set("userRepository", userRepository);
		}

		public Builder<U> userFactory(@NotNull UserFactory<U> userFactory) {
			return set("userFactory", userFactory);
		}

		public Builder<U> clientId(@NotNull String clientId) {
			return set("clientId", clientId);
		}

		public Builder<U> scopes(@NotNull GoogleAPIScope... scopes) {
			return set("scopes", scopes);
		}

		public Builder<U> signUpEnabled(boolean signUpEnabled) {
			return set("signUpEnabled", signUpEnabled);
		}

		public Builder<U> redirectURI(String redirectURI) {
			return set("redirectURI", redirectURI);
		}
	}

}
