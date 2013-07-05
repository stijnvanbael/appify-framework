package be.appify.framework.quantity;

public class Speed {
	private final double kilometersPerHour;

	private Speed(double kilometersPerHour) {
		this.kilometersPerHour = kilometersPerHour;
	}

	public static Speed kilometersPerHour(double kilometersPerHour) {
		return new Speed(kilometersPerHour);
	}

	public double getKilometersPerHour() {
		return kilometersPerHour;
	}

	@Override
	public String toString() {
		return kilometersPerHour + " km/h";
	}

}
