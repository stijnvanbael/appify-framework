package be.appify.framework.location.service.google;

import java.util.*;

import be.appify.framework.common.service.AbstractServiceClient;
import be.appify.framework.location.domain.Location;
import be.appify.framework.location.service.LocationService;

import com.google.api.client.http.HttpTransport;
import com.google.common.base.Function;
import com.google.common.collect.*;

// TODO: make this service a retrying service
public class GoogleLocationService extends AbstractServiceClient implements LocationService {
	private String autocompleteUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
	private String detailsUrl = "https://maps.googleapis.com/maps/api/place/textsearch/json";
	private final Function<LocationMessage, Location> locationMessageConverter;
	private final String apiKey;

	public GoogleLocationService(HttpTransport transport, String apiKey) {
		super(transport);
		this.apiKey = apiKey;
		locationMessageConverter = new LocationMessageToLocationConverter();
	}

	public void setAutocompleteUrl(String autocompleteUrl) {
		this.autocompleteUrl = autocompleteUrl;
	}

	public void setDetailsUrl(String detailsUrl) {
		this.detailsUrl = detailsUrl;
	}

	@Override
	public List<String> getLocations(String name) {
		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("key", apiKey);
		parameters.put("sensor", "false");
		parameters.put("input", name);
		PredictionMessage message = callService(PredictionMessage.class, autocompleteUrl, parameters);
		List<String> results = Lists.newArrayList();
		if (message.getPredictions() != null) {
			for (Prediction prediction : message.getPredictions()) {
				results.add(prediction.getDescription());
			}
		}
		return results;
	}

	@Override
	public Location getLocation(String name) {
		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("key", apiKey);
		parameters.put("sensor", "false");
		parameters.put("query", name);
		LocationMessage message = callService(LocationMessage.class, detailsUrl, parameters);
		return locationMessageConverter.apply(message);
	}

}
