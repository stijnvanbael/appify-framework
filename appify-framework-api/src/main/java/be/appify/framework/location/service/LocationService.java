package be.appify.framework.location.service;

import java.util.List;

import be.appify.framework.location.domain.Location;

public interface LocationService {
	List<String> getLocations(String name);

	Location getLocation(String name);
}
