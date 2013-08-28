package be.appify.framework.quantity;

import com.google.common.base.Objects;

public class Mass {
    private final double kilograms;

    private Mass(double kilograms) {
        this.kilograms = kilograms;
    }

    public static Mass grams(double grams) {
        return new Mass(grams / 1000);
    }

    public static Mass kilograms(double kilograms) {
        return new Mass(kilograms);
    }

    public static Mass tonnes(double tonnes) {
        return new Mass(tonnes * 1000);
    }

    public double getKilograms() {
        return kilograms;
    }

    public double getTonnes() {
        return kilograms / 1000;
    }

    public double getGrams() {
        return kilograms * 1000;
    }

    @Override
    public String toString() {
        if (kilograms < 1) {
            return (int) (kilograms * 1000) + " g";
        } else if (kilograms < 1000) {
            return (int) kilograms + " kg";
        } else {
            return (int) (kilograms / 1000) + " t";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Mass)) {
            return false;
        }
        Mass other = (Mass) obj;
        return Objects.equal((long) (this.kilograms * 10000), (long) (other.kilograms * 10000));
    }

    public Mass add(Mass distance) {
        return new Mass(this.kilograms + distance.kilograms);
    }

    public int compareTo(Mass other) {
        return Double.valueOf(kilograms).compareTo(other.kilograms);
    }

    public boolean moreThan(Mass other) {
        return kilograms > other.kilograms;
    }

    public boolean lessThan(Mass other) {
        return kilograms < other.kilograms;
    }
}
