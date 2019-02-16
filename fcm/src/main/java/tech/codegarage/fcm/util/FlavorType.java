package tech.codegarage.fcm.util;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public enum FlavorType {

    USER("user"),
    KITCHEN("kitchen"),
    DRIVER("driver");

    private final String flavorType;

    private FlavorType(String value) {
        flavorType = value;
    }

    public boolean equalsName(String otherName) {
        return flavorType.equals(otherName);
    }

    public String toString() {
        return this.flavorType;
    }
}