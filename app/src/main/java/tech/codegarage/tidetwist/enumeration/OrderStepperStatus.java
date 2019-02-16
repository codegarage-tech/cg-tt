package tech.codegarage.tidetwist.enumeration;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public enum OrderStepperStatus {

    COMPLETED("1"),
    INACTIVE("0");

    private final String orderStepperStatus;

    private OrderStepperStatus(String value) {
        orderStepperStatus = value;
    }

    public boolean equalsName(String otherName) {
        return orderStepperStatus.equals(otherName);
    }

    @Override
    public String toString() {
        return this.orderStepperStatus;
    }

    public static OrderStepperStatus getOrderStepperStatus(String value) {
        for (OrderStepperStatus stepperStatus : OrderStepperStatus.values()) {
            if (stepperStatus.toString().equalsIgnoreCase(value)) {
                return stepperStatus;
            }
        }
        return null;
    }
}