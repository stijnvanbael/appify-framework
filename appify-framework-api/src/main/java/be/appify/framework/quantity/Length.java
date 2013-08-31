package be.appify.framework.quantity;

import com.google.common.base.Objects;

public class Length {
	private final double meters;

	private Length(double meters) {
		this.meters = meters;
	}

	public static Length millimeters(double millimeters) {
		return new Length(millimeters / 1000);
	}

	public static Length meters(double meters) {
		return new Length(meters);
	}

	public static Length kilometers(double kilometers) {
		return new Length(kilometers * 1000);
	}

	public double getMeters() {
		return meters;
	}

	public double getKilometers() {
		return meters / 1000;
	}

	public double getMillimeters() {
		return meters * 1000;
	}

    public Length multiply(double multiplier) {
        return new Length(meters * multiplier);
    }

	@Override
	public String toString() {
		if (meters < 1) {
			return (int) (meters * 1000) + " mm";
		} else if (meters < 1000) {
			return (int) meters + " m";
		} else {
			return (int) (meters / 1000) + " km";
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Length)) {
			return false;
		}
		Length other = (Length) obj;
		return Objects.equal((long) (this.meters * 10000), (long) (other.meters * 10000));
	}

	public Length add(Length distance) {
		return new Length(this.meters + distance.meters);
	}

	public int compareTo(Length other) {
		return Double.valueOf(meters).compareTo(other.meters);
	}

	public boolean longerThan(Length other) {
		return meters > other.meters;
	}

	public boolean shorterThan(Length other) {
		return meters < other.meters;
	}
}
