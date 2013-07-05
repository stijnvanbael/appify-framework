package be.appify.framework.quantity;

public class Temperature {
	private static final double DIFFERENCE_KELVIN_DEGREES_CELCIUS = 272.85;
	private final double kelvin;

	private Temperature(double kelvin) {
		super();
		this.kelvin = kelvin;
	}

	public static Temperature kelvin(double kelvin) {
		return new Temperature(kelvin);
	}

	public static Temperature degreesCelcius(double degreesCelcius) {
		return new Temperature(degreesCelcius + DIFFERENCE_KELVIN_DEGREES_CELCIUS);
	}

	public double getKelvin() {
		return kelvin;
	}

	public double getDegreesCelcius() {
		return kelvin - DIFFERENCE_KELVIN_DEGREES_CELCIUS;
	}

	@Override
	public String toString() {
		return getDegreesCelcius() + " °C";
	}
}
