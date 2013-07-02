package be.appify.framework.weather.service.wunderground;

import javax.xml.bind.annotation.XmlType;

import com.google.api.client.util.Key;

@XmlType
public class Temperature {
	@Key
	private String celsius;

	public String getCelsius() {
		return celsius;
	}

	public void setCelsius(String value) {
		this.celsius = value;
	}

}
