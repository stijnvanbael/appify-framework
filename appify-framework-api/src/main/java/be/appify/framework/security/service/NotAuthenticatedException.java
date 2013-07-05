package be.appify.framework.security.service;

@SuppressWarnings("serial")
public class NotAuthenticatedException extends Exception {

	public NotAuthenticatedException(String message) {
		super(message);
	}

	public NotAuthenticatedException(String message, Throwable cause) {
		super(message, cause);
	}

}
