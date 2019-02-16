package tech.codegarage.tidetwist.model;

import tech.codegarage.tidetwist.enumeration.OrderStatusType;
import tech.codegarage.tidetwist.enumeration.OrderStepperStatus;

import org.parceler.Parcel;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
@Parcel
public class OrderStatus {

    private String id = "";
    private String status = OrderStatusType.NONE.name();
    private String type = "";
    private String message_text = "";
    private String datetime = "";
    private String is_added = OrderStepperStatus.INACTIVE.toString();

    public OrderStatus() {
    }

    public OrderStatus(String id, String status, String type, String message_text, String datetime, String is_added) {
        this.id = id;
        this.status = status;
        this.type = type;
        this.message_text = message_text;
        this.datetime = datetime;
        this.is_added = is_added;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getIs_added() {
        return is_added;
    }

    public void setIs_added(String is_added) {
        this.is_added = is_added;
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", message_text='" + message_text + '\'' +
                ", datetime='" + datetime + '\'' +
                ", is_added='" + is_added + '\'' +
                '}';
    }
}