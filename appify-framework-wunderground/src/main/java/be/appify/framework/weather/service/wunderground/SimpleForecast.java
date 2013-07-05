package be.appify.framework.weather.service.wunderground;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.google.api.client.util.Key;

public class SimpleForecast {
	@Key
	private List<ForecastDay> forecastday;

	@XmlElement
	public List<ForecastDay> getForecastday() {
		return forecastday;
	}

	public void setForecastday(List<ForecastDay> forecastday) {
		this.forecastday = forecastday;
	}
}
