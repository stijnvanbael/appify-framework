package be.appify.framework.google.security.web;

import com.google.sitebricks.At;
import com.google.sitebricks.Show;

@At("/google/oauth2/error")
@Show("GoogleAuthenticationError.html")
public class GoogleAuthenticationErrorPage {
	private String error;
	private String target;

	public void setError(String error) {
		this.error = error;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	// TODO: interpret error
}
