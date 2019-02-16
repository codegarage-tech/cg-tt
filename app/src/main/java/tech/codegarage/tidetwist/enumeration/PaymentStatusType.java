package tech.codegarage.tidetwist.enumeration;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public enum PaymentStatusType {

    NONE(""),
    CASH_ON_DELIVERY("1"),
    PAID("2"),
    REFUNDED("3"),
    REQUEST_FOR_REFUND("4");

    private final String paymentStatusValue;

    private PaymentStatusType(String value) {
        paymentStatusValue = value;
    }

    public boolean equalsName(String otherName) {
        return paymentStatusValue.equals(otherName);
    }

    @Override
    public String toString() {
        return this.paymentStatusValue;
    }

    public static PaymentStatusType getPaymentStatus(String value) {
        for (PaymentStatusType stepperStatus : PaymentStatusType.values()) {
            if (stepperStatus.toString().equalsIgnoreCase(value)) {
                return stepperStatus;
            }
        }
        return null;
    }
}