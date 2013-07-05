package be.appify.framework.security.service;

import be.appify.framework.security.domain.Authentication;
import be.appify.framework.security.domain.Credential;
import be.appify.framework.security.domain.User;

public interface AuthenticationService<U extends User, C extends Credential<U>> {
	public Authentication<U> authenticate(C credential, boolean keepAuthenticated) throws NotAuthenticatedException;

	public Authentication<U> autoAuthenticate() throws NotAuthenticatedException;

	public String getSignInURL(String target);

	public String getSignOutURL();

	public void cancel(Authentication<U> authentication);
}
