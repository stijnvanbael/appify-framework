package be.appify.framework.google.security.service.token;

import com.google.api.client.util.Key;

public class AccessTokenMessage {
	@Key
	private String error;

	@Key
	private String access_token;

	@Key
	private Long expires_in;

	@Key
	private String token_type;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public Long getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(Long expires_in) {
		this.expires_in = expires_in;
	}

	public String getToken_type() {
		return token_type;
	}

	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

}
