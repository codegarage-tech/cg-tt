package tech.codegarage.tidetwist.model;

import org.parceler.Parcel;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
@Parcel
public class FcmOrderNotification {

    private Order order ;

    public FcmOrderNotification() {
    }

    public FcmOrderNotification(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "{" +
                "order=" + order +
                '}';
    }
}