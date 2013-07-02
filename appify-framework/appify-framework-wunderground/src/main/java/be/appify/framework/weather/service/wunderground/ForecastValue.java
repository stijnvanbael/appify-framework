package be.appify.framework.weather.service.wunderground;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.api.client.util.Key;

@XmlType
public class ForecastValue {
	@Key
	private String metric;

	@XmlElement
	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}
}
