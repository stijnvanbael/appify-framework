package be.appify.framework.common.service;

public class RemoteException extends RuntimeException {

	private static final long serialVersionUID = 7716591932572208010L;

	public RemoteException(String message, Throwable cause) {
		super(message, cause);
	}

	public RemoteException(String message) {
		super(message);
	}

}