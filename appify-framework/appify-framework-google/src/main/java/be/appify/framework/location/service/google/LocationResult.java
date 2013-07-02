package be.appify.framework.location.service.google;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.api.client.util.Key;

@XmlType
public class LocationResult {
	@Key
	private String formatted_address;
	@Key
	private Geometry geometry;

	@XmlElement(name = "formatted_address")
	public String getFormatted_address() {
		return formatted_address;
	}

	public void setFormatted_address(String formattedAddress) {
		this.formatted_address = formattedAddress;
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

}
