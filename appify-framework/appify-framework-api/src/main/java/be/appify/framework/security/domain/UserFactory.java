package be.appify.framework.security.domain;

public interface UserFactory<U extends User> {
	public U createUser(String firstName, String lastName, String emailAddress);

}
