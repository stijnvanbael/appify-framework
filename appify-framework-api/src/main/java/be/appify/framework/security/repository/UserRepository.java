package be.appify.framework.security.repository;

import be.appify.framework.security.domain.User;

public interface UserRepository<U extends User> {

	U findByEmailAddress(String emailAddress);

	void store(U user);

}
