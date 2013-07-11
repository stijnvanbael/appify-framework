package be.appify.framework.weather.service.wunderground;

import java.util.*;
import java.util.concurrent.*;

import org.slf4j.*;

import be.appify.framework.common.service.*;
import be.appify.framework.location.domain.Location;
import be.appify.framework.weather.domain.WeatherCondition;
import be.appify.framework.weather.service.WeatherService;

import com.google.api.client.http.HttpTransport;
import com.google.common.base.Function;
import com.google.common.cache.*;
import com.google.common.collect.Maps;

// TODO: replace %s parameters by named parameters
public class WundergroundWeatherService extends AbstractServiceClient implements WeatherService {
	private static final Logger LOGGER = LoggerFactory.getLogger(WundergroundWeatherService.class);
	private final Function<Hourly10DayMessage, List<WeatherCondition>> hourlyConverter;
	private final Function<Forecast10DayMessage, List<WeatherCondition>> dailyConverter;
	private String hourlyUrl = "http://api.wunderground.com/api/%s/hourly10day/q/%s.json";
	private String dailyUrl = "http://api.wunderground.com/api/%s/forecast10day/q/%s.json";
	private final Cache<String, List<WeatherCondition>> cache;
	private String apiKey;

	public WundergroundWeatherService(HttpTransport transport, String apiKey) {
		super(transport);
		this.apiKey = apiKey;
		hourlyConverter = new Hourly10DayMessageToForecastConverter();
		dailyConverter = new Forecast10DayMessageToForecastConverter();
		cache = CacheBuilder.newBuilder()
				.expireAfterWrite(1, TimeUnit.HOURS)
				.maximumSize(1000)
				.build();
	}

	public void setHourlyUrl(String hourlyUrl) {
		this.hourlyUrl = hourlyUrl;
	}

	public void setDailyUrl(String dailyUrl) {
		this.dailyUrl = dailyUrl;
	}

    public void setAPIKey(String apiKey) {
        this.apiKey = apiKey;
    }

	@Override
	public List<WeatherCondition> getHourlyForecastFor(final Location location) {
		try {
			return cache.get("H/" + location, new Callable<List<WeatherCondition>>() {
				@Override
				public List<WeatherCondition> call() throws Exception {
					return callService(Hourly10DayMessage.class, location, hourlyUrl, hourlyConverter);
				}
			});
		} catch (ExecutionException e) {
			processException(e);
			return Collections.emptyList();
		}
	}

	@Override
	public List<WeatherCondition> getDailyForecastFor(final Location location) {
		try {
			return cache.get("D/" + location, new Callable<List<WeatherCondition>>() {
				@Override
				public List<WeatherCondition> call() throws Exception {
					return callService(Forecast10DayMessage.class, location, dailyUrl, dailyConverter);
				}
			});
		} catch (ExecutionException e) {
			processException(e);
			return Collections.emptyList();
		}
	}

	public void clearCache() {
		cache.invalidateAll();
	}

	private <T extends WeatherMessage> List<WeatherCondition> callService(Class<T> messageClass, Location location, String url,
			Function<T, List<WeatherCondition>> converter) {
		Map<String, String> parameters = Maps.newHashMap();
		String requestUrl = String.format(url, apiKey, location.getLatitude() + "," + location.getLongitude());
		T message = callService(messageClass, requestUrl, parameters);
		Error error = message.getResponse().getError();
		checkForError(location, error);
		return converter.apply(message);
	}

	private void processException(ExecutionException e) {
		Throwable cause = e.getCause();
		if (cause instanceof java.lang.Error) {
			throw (java.lang.Error) cause;
		} else if (cause instanceof RuntimeException) {
			throw (RuntimeException) cause;
		} else {
			throw new RemoteException("Exception occured calling web service.", cause);
		}
	}

	private void checkForError(final Location location, Error error) {
		if (error != null) {
			if ("querynotfound".equals(error.getType())) {
				LOGGER.warn("No weather data found for " + location);
			} else {
				throw new RemoteException("Error getting weather for " + location + ": " + error.getDescription());
			}
		}
	}

}
