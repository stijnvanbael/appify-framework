package be.appify.framework.security.repository;

import be.appify.framework.security.domain.Authentication;
import be.appify.framework.security.domain.User;

public interface AuthenticationRepository<U extends User> {
	Authentication<U> findByToken(String authenticationToken);

	void store(Authentication<U> authentication);

	void delete(Authentication<U> authentication);
}
