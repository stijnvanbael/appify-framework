package be.appify.framework.weather.service.wunderground;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.api.client.util.Key;

@XmlRootElement
public class Forecast10DayMessage extends WeatherMessage {
	@Key
	private Forecast forecast;

	@XmlElement
	public Forecast getForecast() {
		return forecast;
	}

	public void setForecast(Forecast forecast) {
		this.forecast = forecast;
	}

}
