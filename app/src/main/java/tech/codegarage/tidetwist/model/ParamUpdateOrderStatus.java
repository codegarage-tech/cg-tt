package tech.codegarage.tidetwist.model;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class ParamUpdateOrderStatus {

    private String order_id = "";
    private String status_id = "";

    public ParamUpdateOrderStatus(String order_id, String status_id) {
        this.order_id = order_id;
        this.status_id = status_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }

    @Override
    public String toString() {
        return "{" +
                "order_id='" + order_id + '\'' +
                ", status_id='" + status_id + '\'' +
                '}';
    }
}
