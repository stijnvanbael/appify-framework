package be.appify.framework.security.service;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.Duration;
import org.joda.time.ReadableDuration;

import be.appify.framework.security.domain.Credential;
import be.appify.framework.security.domain.User;

public abstract class AbstractAuthenticationService<U extends User, C extends Credential<U>> implements AuthenticationService<U, C> {
	private Provider<HttpServletRequest> requestProvider;
	private Provider<HttpServletResponse> responseProvider;
	private ReadableDuration timeAuthenticationValid = Duration.standardDays(365);

	public void setTimeAuthenticationValid(ReadableDuration timeAuthenticationValid) {
		this.timeAuthenticationValid = timeAuthenticationValid;
	}

	public ReadableDuration getTimeAuthenticationValid() {
		return timeAuthenticationValid;
	}

	protected final String findCookie(String name) {
		Cookie[] cookies = requestProvider.get().getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	protected final void setCookie(String name, String value, int maxAgeInSeconds) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(maxAgeInSeconds);
		responseProvider.get().addCookie(cookie);
	}

	protected final void deleteCookie(String name) {
		for (Cookie cookie : requestProvider.get().getCookies()) {
			if (name.equals(cookie.getName())) {
				cookie.setValue("");
				cookie.setMaxAge(0);
				responseProvider.get().addCookie(cookie);
				return;
			}
		}
	}

	@Inject
	public void setRequestProvider(Provider<HttpServletRequest> requestProvider) {
		this.requestProvider = requestProvider;
	}

	@Inject
	public void setResponseProvider(Provider<HttpServletResponse> responseProvider) {
		this.responseProvider = responseProvider;
	}
}
