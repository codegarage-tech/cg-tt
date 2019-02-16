package tech.codegarage.tidetwist.enumeration;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public enum OrderStatusType {

//        Log.d(TAG, TAG+">> OrderStatusType: "+ OrderStatusType.ORDER_PLACED.toString());
//        android.util.Log.d(TAG, TAG+">> OrderStatusType: "+ OrderStatusType.ORDER_PLACED.name());
//        Log.d(TAG, TAG+">> OrderStatusType: "+ OrderStatusType.valueOf(OrderStatusType.ORDER_PLACED.name()));
//        Log.d(TAG, TAG+">> OrderStatusType: "+ OrderStatusType.valueOf(OrderStatusType.ORDER_PLACED.name()).name());
//        Log.d(TAG, TAG+">> OrderStatusType: "+ OrderStatusType.valueOf(OrderStatusType.ORDER_PLACED.name()).toString());

    NONE(""),
    ORDER_PLACED("Order is placed"),
    ORDER_ACCEPTED("Order is accepted"),
    ORDER_CANCELED("Order is canceled"),
    COOKING_STARTED("Food preparation is started"),
    COOKING_FINISHED("Food is delivered to delivery man"),
    FOOD_PICKED("Delivery man has started moving with food"),
    FOOD_DELIVERED("Food is delivered to user");

    private final String orderStatusType;

    private OrderStatusType(String value) {
        orderStatusType = value;
    }

    public boolean equalsName(String otherName) {
        return orderStatusType.equals(otherName);
    }

    public String toString() {
        return this.orderStatusType;
    }
}