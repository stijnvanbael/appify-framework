package be.appify.framework.location.service.google;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import be.appify.framework.location.domain.Location;

import com.google.common.base.Function;

public class LocationMessageToLocationConverter implements Function<LocationMessage, Location> {

	@Override
	@Nullable
	public Location apply(@Nullable LocationMessage message) {
		if (message.getResults() == null || message.getResults().isEmpty()) {
			return null;
		}
		LocationResult result = message.getResults().get(0);
		String formattedAddress = result.getFormatted_address();
		GeographicLocation location = result.getGeometry().getLocation();
		BigDecimal latitude = location.getLat();
		BigDecimal longitude = location.getLng();
		return new Location(formattedAddress, latitude.doubleValue(), longitude.doubleValue());
	}

}
