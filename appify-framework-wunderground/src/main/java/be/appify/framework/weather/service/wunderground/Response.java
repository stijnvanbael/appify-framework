package be.appify.framework.weather.service.wunderground;

import javax.xml.bind.annotation.*;

import com.google.api.client.util.Key;

@XmlType
public class Response {
	@Key
	private Error error;

	@XmlElement
	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}
}
