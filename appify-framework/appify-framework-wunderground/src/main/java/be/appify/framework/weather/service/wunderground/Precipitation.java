package be.appify.framework.weather.service.wunderground;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.api.client.util.Key;

@XmlType
public class Precipitation {
	@Key
	private BigDecimal mm;

	@XmlElement
	public BigDecimal getMm() {
		return mm;
	}

	public void setMm(BigDecimal quantity) {
		this.mm = quantity;
	}
}
