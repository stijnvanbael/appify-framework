package be.appify.framework.google.security.web;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.appify.framework.google.security.domain.GoogleCredential;
import be.appify.framework.google.security.service.GoogleAuthenticationService;
import be.appify.framework.security.domain.User;
import be.appify.framework.security.service.NotAuthenticatedException;

import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;

@At("/google/oauth2/callback")
@Service
public class GoogleAuthenticationCallbackServiceProvider<U extends User> {
	private static final Logger LOGGER = LoggerFactory.getLogger(GoogleAuthenticationCallbackServiceProvider.class);
	private final GoogleAuthenticationService<U> authenticationService;
	private String code;
	private String error;
	private String state;

	@Inject
	public GoogleAuthenticationCallbackServiceProvider(GoogleAuthenticationService<U> authenticationService) {
		this.authenticationService = authenticationService;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setError(String error) {
		this.error = error;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Get
	public Reply<?> callback() {
		if (error != null) {
			return Reply.saying().redirect("/google/oauth2/error?error=" + error + "&target=" + state);
		}
		GoogleCredential<U> credential = new GoogleCredential<U>(code);
		try {
			authenticationService.authenticate(credential, true);
		} catch (NotAuthenticatedException e) {
			LOGGER.warn(e.getMessage());
			return Reply.saying().redirect("/google/oauth2/error?error=not_authenticated&target=" + state);
		}
		return Reply.saying().redirect(state);
	}
}
