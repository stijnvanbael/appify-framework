package be.appify.framework.weather.service.wunderground;

import javax.xml.bind.annotation.XmlType;

import com.google.api.client.util.Key;

@XmlType
public class Forecast {
	@Key
	private SimpleForecast simpleforecast;

	public SimpleForecast getSimpleforecast() {
		return simpleforecast;
	}

	public void setSimpleforecast(SimpleForecast simpleforecast) {
		this.simpleforecast = simpleforecast;
	}

}
