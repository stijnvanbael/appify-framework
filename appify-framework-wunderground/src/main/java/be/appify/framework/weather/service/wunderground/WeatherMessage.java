package be.appify.framework.weather.service.wunderground;

import javax.xml.bind.annotation.XmlElement;

import com.google.api.client.util.Key;

public class WeatherMessage {
	@Key
	private Response response;

	@XmlElement
	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}
}
