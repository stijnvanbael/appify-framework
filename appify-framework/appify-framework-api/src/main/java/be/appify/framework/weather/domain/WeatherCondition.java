package be.appify.framework.weather.domain;

import javax.validation.constraints.*;

import org.joda.time.Interval;

import be.appify.framework.domain.ReflectionBuilder;
import be.appify.framework.quantity.*;

public class WeatherCondition {
	private Interval validity;
	private Temperature minTemperature;
	private Temperature maxTemperature;
	private double cloudCoverage;
	private Length precipitation;
	private Speed windSpeed;
	private WeatherConditionType conditionType;

	private WeatherCondition() {
	}

	public Interval getValidity() {
		return validity;
	}

	public Temperature getMinTemperature() {
		return minTemperature;
	}

	public Temperature getMaxTemperature() {
		return maxTemperature;
	}

	public double getCloudCoverage() {
		return cloudCoverage;
	}

	public Length getPrecipitation() {
		return precipitation;
	}

	public Speed getWindSpeed() {
		return windSpeed;
	}

	public WeatherConditionType getConditionType() {
		return conditionType;
	}

	@Override
	public String toString() {
		return "WeatherCondition [validity=" + validity + ", minimumTemperature=" + minTemperature
				+ ", maximumTemperature=" + maxTemperature + ", cloudCoverage=" + cloudCoverage
				+ ", precipitation=" + precipitation + ", wind=" + windSpeed + ", conditionType=" + conditionType + "]";
	}

	public static Builder on(Interval validity) {
		return new Builder().validity(validity);
	}

	public static class Builder extends ReflectionBuilder<WeatherCondition, Builder> {
		protected Builder() {
			super(WeatherCondition.class);
		}

		public Builder conditionType(@NotNull WeatherConditionType conditionType) {
			return set("conditionType", conditionType);
		}

		public Builder windSpeed(@NotNull Speed windSpeed) {
			return set("windSpeed", windSpeed);
		}

		public Builder precipitation(@NotNull Length precipitation) {
			return set("precipitation", precipitation);
		}

		public Builder cloudCoverage(@NotNull @Min(0) @Max(1) double cloudCoverage) {
			return set("cloudCoverage", cloudCoverage);
		}

		public Builder validity(@NotNull Interval validity) {
			return set("validity", validity);
		}

		public Builder minTemperature(@NotNull Temperature minTemperature) {
			return set("minTemperature", minTemperature);
		}

		public Builder maxTemperature(@NotNull Temperature maxTemperature) {
			return set("maxTemperature", maxTemperature);
		}
	}
}
