package tech.codegarage.tidetwist.enumeration;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public enum KitchenType {

    NONE("")
    , TIME("Time")
    , AREA("Area")
    , CUISINE("Cuisine")
    , OFFER("Offer")
    , POPULAR("Popular")
    , FAST_DELIVERY("Fast Delivery")
    , FAVORITE("Favorite")
    , FOOD_NAME("Food Name")
    , KITCHEN_NAME("Kitchen Name");

    private final String kitchenTypeValue;

    private KitchenType(String value) {
        kitchenTypeValue = value;
    }

    public boolean equalsName(String otherName) {
        return kitchenTypeValue.equals(otherName);
    }

    @Override
    public String toString() {
        return this.kitchenTypeValue;
    }

    public static KitchenType getKitchenType(String value) {
        for (KitchenType kitchenType : KitchenType.values()) {
            if (kitchenType.toString().equalsIgnoreCase(value)) {
                return kitchenType;
            }
        }
        return null;
    }
}