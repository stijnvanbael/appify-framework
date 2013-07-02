package be.appify.framework.google.security.domain;

public class GoogleProfile {
	private final String firstName;
	private final String lastName;
	private final String emailAddress;

	public GoogleProfile(String firstName, String lastName, String emailAddress) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
}
