package be.appify.framework.location.service.google;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.api.client.util.Key;

@XmlType
public class Geometry {
	@Key
	private GeographicLocation location;

	@XmlElement
	public GeographicLocation getLocation() {
		return location;
	}

	public void setLocation(GeographicLocation location) {
		this.location = location;
	}

}
