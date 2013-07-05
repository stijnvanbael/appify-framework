package be.appify.framework.location.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import be.appify.framework.domain.ReflectionBuilder;
import be.appify.framework.quantity.Length;

import com.google.common.base.Objects;

@Embeddable
public class Location {
	private static final int METER_MULTIPLIER = 1609;
	private static final double EARTH_RADIUS = 3958.75;

	@Column(length = 250)
	private String name;
	@Column(length = 25)
	private double latitude;
	@Column(length = 25)
	private double longitude;

	@SuppressWarnings("unused")
	private Location() {
	}

	public Location(String name, double latitude, double longitude) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Location(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Length distanceTo(Location other) {
		double dLat = Math.toRadians(other.latitude - latitude);
		double dLng = Math.toRadians(other.longitude - longitude);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
				Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(longitude)) *
				Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = EARTH_RADIUS * c;

		return Length.meters(dist * METER_MULTIPLIER);
	}

	public String getName() {
		return name;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getDescription() {
		return name != null ? name : latitude + ", " + longitude;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Location)) {
			return false;
		}
		Location other = (Location) obj;
		return Math.abs(this.latitude - other.latitude) < 0.0001 && Math.abs(this.longitude - other.longitude) < 0.0001;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(latitude, longitude);
	}

	@Override
	public String toString() {
		return "[" + getDescription() + "]";
	}

	public static class Builder extends ReflectionBuilder<Location, Builder> {

		public Builder() {
			super(Location.class);
		}

		public Builder name(@NotNull @Size(min = 1, max = 250) String name) {
			return set("name", name);
		}

		public Builder latitude(double latitude) {
			return set("latitude", latitude);
		}

		public Builder longitude(double longitude) {
			return set("longitude", longitude);
		}
	}

}