package be.appify.framework.google.security.service.token;

import java.util.Map;

import be.appify.framework.common.service.AbstractServiceClient;
import be.appify.framework.common.service.RemoteException;
import be.appify.framework.google.security.domain.GoogleAccessToken;

import com.google.api.client.http.HttpTransport;
import com.google.common.collect.Maps;

public class GoogleAccessTokenService extends AbstractServiceClient {

	private String accessTokenURL = "https://accounts.google.com/o/oauth2/token";
	private final String clientId;
	private final String clientSecret;
	private String redirectURI = "/google/oauth2/callback";

	public GoogleAccessTokenService(HttpTransport transport, String clientId, String clientSecret) {
		super(transport);
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		setRequestMethod(POST);
	}

	public void setAccessTokenURL(String accessTokenURL) {
		this.accessTokenURL = accessTokenURL;
	}

	public void setRedirectURI(String redirectURI) {
		this.redirectURI = redirectURI;
	}

	public GoogleAccessToken requestAccess(String code) throws RemoteException {
		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("code", code);
		parameters.put("client_id", clientId);
		parameters.put("client_secret", clientSecret);
		parameters.put("redirect_uri", redirectURI);
		parameters.put("grant_type", "authorization_code");
		AccessTokenMessage message = callService(AccessTokenMessage.class, accessTokenURL, parameters);
		return convert(message);
	}

	private GoogleAccessToken convert(AccessTokenMessage message) {
		if (message.getError() != null) {
			throw new RemoteException("Failed to obtain token. Error: " + message.getError());
		}
		return new GoogleAccessToken(message.getAccess_token());
	}
}
