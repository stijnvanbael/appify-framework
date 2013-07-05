package be.appify.framework.google.security.domain;

public class GoogleAccessToken {
	private final String token;

	public GoogleAccessToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}
}
