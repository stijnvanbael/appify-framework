package be.appify.framework.google.security.service;

public enum GoogleAPIScope {
	PROFILE("profile"),
	EMAIL("email");

	private String url;

	private GoogleAPIScope(String url) {
		this.url = url;
	}

	public String url() {
		return url;
	}

}
