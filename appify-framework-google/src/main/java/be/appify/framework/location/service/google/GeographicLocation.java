package be.appify.framework.location.service.google;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.api.client.util.Key;

@XmlType
public class GeographicLocation {
	@Key
	private BigDecimal lat;
	@Key
	private BigDecimal lng;

	@XmlElement
	public BigDecimal getLat() {
		return lat;
	}

	public void setLat(BigDecimal latitude) {
		this.lat = latitude;
	}

	@XmlElement
	public BigDecimal getLng() {
		return lng;
	}

	public void setLng(BigDecimal longitude) {
		this.lng = longitude;
	}

}
