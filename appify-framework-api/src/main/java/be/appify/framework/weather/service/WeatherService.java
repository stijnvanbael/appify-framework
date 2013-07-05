package be.appify.framework.weather.service;

import java.util.List;

import be.appify.framework.location.domain.Location;
import be.appify.framework.weather.domain.WeatherCondition;

public interface WeatherService {
	List<WeatherCondition> getDailyForecastFor(Location location);

	List<WeatherCondition> getHourlyForecastFor(Location location);
}
