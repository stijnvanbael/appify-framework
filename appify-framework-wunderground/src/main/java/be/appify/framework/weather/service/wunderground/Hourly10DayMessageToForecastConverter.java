package be.appify.framework.weather.service.wunderground;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.joda.time.*;
import org.slf4j.*;

import be.appify.framework.quantity.*;
import be.appify.framework.quantity.Temperature;
import be.appify.framework.weather.domain.*;

import com.google.common.base.Function;
import com.google.common.collect.*;

public class Hourly10DayMessageToForecastConverter implements Function<Hourly10DayMessage, List<WeatherCondition>> {
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
	public List<WeatherCondition> apply(Hourly10DayMessage from) {
		List<WeatherCondition> forecasts = Lists.newArrayList();
		if (from.getHourly_forecast() != null) {
			for (HourlyForecast forecast : from.getHourly_forecast()) {
				forecasts.add(toWeatherCondition(forecast));
			}
		}
		return forecasts;
	}

	private WeatherCondition toWeatherCondition(HourlyForecast forecast) {
		Interval validity = toInterval(forecast.getFCTTIME());
		double minimumTemperature = Double.parseDouble(forecast.getTemp().getMetric());
		double maximumTemperature = Double.parseDouble(forecast.getTemp().getMetric());
		WeatherConditionType conditionType = getCondition(forecast.getIcon());
		double cloudCoverage = getCloudCoverage(forecast.getSky());
		double precipitation = getPrecipitation(forecast.getQpf().getMetric());
		double windSpeed = Double.parseDouble(forecast.getWspd().getMetric());
		return WeatherCondition.on(validity)
				.cloudCoverage(cloudCoverage)
				.conditionType(conditionType)
				.maxTemperature(Temperature.degreesCelcius(maximumTemperature))
				.minTemperature(Temperature.degreesCelcius(minimumTemperature))
				.precipitation(Length.millimeters(precipitation))
				.windSpeed(Speed.kilometersPerHour(windSpeed))
				.build();
	}

	private double getPrecipitation(String precipitation) {
		double result = 0.0;
		if (StringUtils.isNotBlank(precipitation)) {
			result = Double.parseDouble(precipitation);
		}
		return result;
	}

	private double getCloudCoverage(String sky) {
		int skyValue = Integer.parseInt(sky);
		return (double) skyValue / 100;
	}

	private WeatherConditionType getCondition(String condition) {
		WeatherConditionType result = CONDITIONS.get(condition);
		if (result == null) {
			result = WeatherConditionType.UNKNOWN;
			LOGGER.warn("Unknown condition: " + condition);
		}
		return result;
	}

	private Interval toInterval(ForecastTime time) {
		LocalDateTime start = new LocalDateTime(
				Integer.parseInt(time.getYear()),
				Integer.parseInt(time.getMon()),
				Integer.parseInt(time.getMday()),
				Integer.parseInt(time.getHour()),
				0, 0, 0);
		LocalDateTime end = new LocalDateTime(
				Integer.parseInt(time.getYear()),
				Integer.parseInt(time.getMon()),
				Integer.parseInt(time.getMday()),
				Integer.parseInt(time.getHour()),
				59, 59, 999);
		return new Interval(start.toDateTime(), end.toDateTime());
	}

}
