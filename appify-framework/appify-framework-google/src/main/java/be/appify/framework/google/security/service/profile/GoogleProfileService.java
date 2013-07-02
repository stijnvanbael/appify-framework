package be.appify.framework.google.security.service.profile;

import java.util.Map;

import be.appify.framework.common.service.AbstractServiceClient;
import be.appify.framework.google.security.domain.GoogleAccessToken;
import be.appify.framework.google.security.domain.GoogleProfile;

import com.google.api.client.http.HttpTransport;
import com.google.common.collect.Maps;

public class GoogleProfileService extends AbstractServiceClient {
	private String profileUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

	public GoogleProfileService(HttpTransport transport) {
		super(transport);
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public GoogleProfile getProfile(GoogleAccessToken accessToken) {
		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("access_token", accessToken.getToken());
		GoogleProfileMessage profileMessage = callService(GoogleProfileMessage.class, profileUrl, parameters);
		return convert(profileMessage);
	}

	private GoogleProfile convert(GoogleProfileMessage profileMessage) {
		return new GoogleProfile(
				profileMessage.getGiven_name(),
				profileMessage.getFamily_name(),
				profileMessage.getEmail());
	}

}
