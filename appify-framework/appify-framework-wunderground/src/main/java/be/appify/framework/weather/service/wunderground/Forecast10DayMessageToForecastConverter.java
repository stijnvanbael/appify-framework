package be.appify.framework.weather.service.wunderground;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.appify.framework.quantity.Length;
import be.appify.framework.quantity.Speed;
import be.appify.framework.quantity.Temperature;
import be.appify.framework.weather.domain.WeatherCondition;
import be.appify.framework.weather.domain.WeatherConditionType;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Forecast10DayMessageToForecastConverter implements Function<Forecast10DayMessage, List<WeatherCondition>> {

	private static final Map<String, WeatherConditionType> CONDITIONS = Maps.newHashMap();
	private static final Logger LOGGER = LoggerFactory.getLogger(Forecast10DayMessageToForecastConverter.class);

	static {
		CONDITIONS.put("chanceflurries", WeatherConditionType.CHANCE_OF_FLURRIES);
		CONDITIONS.put("chancerain", WeatherConditionType.CHANCE_OF_RAIN);
		CONDITIONS.put("chancesleet", WeatherConditionType.CHANCE_OF_SLEET);
		CONDITIONS.put("chancesnow", WeatherConditionType.CHANCE_OF_SNOW);
		CONDITIONS.put("chancetstorms", WeatherConditionType.CHANCE_OF_THUNDERSTORMS);
		CONDITIONS.put("clear", WeatherConditionType.CLEAR);
		CONDITIONS.put("cloudy", WeatherConditionType.CLOUDY);
		CONDITIONS.put("flurries", WeatherConditionType.FLURRIES);
		CONDITIONS.put("fog", WeatherConditionType.FOG);
		CONDITIONS.put("hazy", WeatherConditionType.HAZY);
		CONDITIONS.put("mostlycloudy", WeatherConditionType.MOSTLY_CLOUDY);
		CONDITIONS.put("mostlysunny", WeatherConditionType.MOSTLY_SUNNY);
		CONDITIONS.put("partlycloudy", WeatherConditionType.PARTLY_CLOUDY);
		CONDITIONS.put("partlysunny", WeatherConditionType.PARTLY_CLOUDY);
		CONDITIONS.put("sleet", WeatherConditionType.SLEET);
		CONDITIONS.put("rain", WeatherConditionType.RAIN);
		CONDITIONS.put("snow", WeatherConditionType.SNOW);
		CONDITIONS.put("sunny", WeatherConditionType.CLEAR);
		CONDITIONS.put("tstorms", WeatherConditionType.THUNDERSTORMS);
		CONDITIONS.put("unknown", WeatherConditionType.UNKNOWN);
	}

	@Override
	public List<WeatherCondition> apply(Forecast10DayMessage from) {
		Forecast forecast = from.getForecast();
		List<WeatherCondition> forecasts = Lists.newArrayList();
		if (forecast != null) {
			SimpleForecast simpleForecast = forecast.getSimpleforecast();
			if (simpleForecast != null) {
				for (ForecastDay day : simpleForecast.getForecastday()) {
					forecasts.add(toWeatherCondition(day));
				}
			}
		}
		return forecasts;
	}

	private WeatherCondition toWeatherCondition(ForecastDay day) {
		Interval validity = toInterval(day.getDate());
		double minimumTemperature = Double.parseDouble(day.getLow().getCelsius());
		double maximumTemperature = Double.parseDouble(day.getHigh().getCelsius());
		WeatherConditionType conditionType = getCondition(day.getIcon());
		double cloudCoverage = getCloudCoverage(day.getSkyicon());
		double precipitation = day.getQpf_day().getMm().doubleValue();
		double windSpeed = day.getAvewind().getKph();
		return WeatherCondition.on(validity)
				.cloudCoverage(cloudCoverage)
				.conditionType(conditionType)
				.maxTemperature(Temperature.degreesCelcius(maximumTemperature))
				.minTemperature(Temperature.degreesCelcius(minimumTemperature))
				.precipitation(Length.millimeters(precipitation))
				.windSpeed(Speed.kilometersPerHour(windSpeed))
				.build();
	}

	private double getCloudCoverage(String sky) {
		switch (CONDITIONS.get(sky)) {
		case CLEAR:
			return 0.0;
		case MOSTLY_SUNNY:
			return 0.2;
		case PARTLY_CLOUDY:
			return 0.4;
		case CLOUDY:
			return 0.6;
		case MOSTLY_CLOUDY:
			return 0.8;
		case OVERCAST:
			return 1.0;
		default:
			return 0.5;
		}
	}

	private WeatherConditionType getCondition(String condition) {
		WeatherConditionType result = CONDITIONS.get(condition);
		if (result == null) {
			result = WeatherConditionType.UNKNOWN;
			LOGGER.warn("Unknown condition: " + condition);
		}
		return result;
	}

	private Interval toInterval(Date date) {
		DateTime start = new DateTime(date.getYear(), date.getMonth(), date.getDay(), 0, 0);
		DateTime end = new DateTime(date.getYear(), date.getMonth(), date.getDay(), 23, 59, 59, 999);
		return new Interval(start, end);
	}

}
