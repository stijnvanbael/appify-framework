package be.appify.framework.weather.service.wunderground;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.api.client.util.Key;

@XmlRootElement
public class Hourly10DayMessage extends WeatherMessage {
	@Key
	private List<HourlyForecast> hourly_forecast;

	@XmlElement
	public List<HourlyForecast> getHourly_forecast() {
		return hourly_forecast;
	}

	public void setHourly_forecast(List<HourlyForecast> hourly_forecast) {
		this.hourly_forecast = hourly_forecast;
	}

}
