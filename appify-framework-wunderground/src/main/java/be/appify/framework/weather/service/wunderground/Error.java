package be.appify.framework.weather.service.wunderground;

import javax.xml.bind.annotation.*;

import com.google.api.client.util.Key;

@XmlType
public class Error {
	@Key
	private String type;
	@Key
	private String description;

	@XmlElement
	public String getDescription() {
		return description;
	}

	@XmlElement
	public String getType() {
		return type;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setType(String type) {
		this.type = type;
	}
}
