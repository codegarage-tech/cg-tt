package tech.codegarage.tidetwist.model;

import java.util.ArrayList;
import java.util.List;

import tech.codegarage.tidetwist.enumeration.PaymentStatusType;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class ParamDoOrder {

    private String order_id = "";
    private String transaction_id = "";
    private String manufacturer_id = "";
    private String grand_total = "";
    private String app_user_id = "";
    private String delivery_name = "";
    private String delivery_address = "";
    private String delivery_email = "";
    private String delivery_phone = "";
    private String total_vat = "";
    private String shipping_cost = "";
    private String is_promotional_offer = "";
    private String promotional_discount_percentage = "";
    private String subtotal = "";
    private String payment_status = PaymentStatusType.NONE.toString();
    private String delivery_time = "";
    private List<ParamDoOrderItem> items = new ArrayList<>();

    public ParamDoOrder() {
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getManufacturer_id() {
        return manufacturer_id;
    }

    public void setManufacturer_id(String manufacturer_id) {
        this.manufacturer_id = manufacturer_id;
    }

    public String getGrand_total() {
        return grand_total;
    }

    public void setGrand_total(String grand_total) {
        this.grand_total = grand_total;
    }

    public String getApp_user_id() {
        return app_user_id;
    }

    public void setApp_user_id(String app_user_id) {
        this.app_user_id = app_user_id;
    }

    public String getDelivery_name() {
        return delivery_name;
    }

    public void setDelivery_name(String delivery_name) {
        this.delivery_name = delivery_name;
    }

    public String getDelivery_address() {
        return delivery_address;
    }

    public void setDelivery_address(String delivery_address) {
        this.delivery_address = delivery_address;
    }

    public String getDelivery_email() {
        return delivery_email;
    }

    public void setDelivery_email(String delivery_email) {
        this.delivery_email = delivery_email;
    }

    public String getDelivery_phone() {
        return delivery_phone;
    }

    public void setDelivery_phone(String delivery_phone) {
        this.delivery_phone = delivery_phone;
    }

    public String getTotal_vat() {
        return total_vat;
    }

    public void setTotal_vat(String total_vat) {
        this.total_vat = total_vat;
    }

    public String getShipping_cost() {
        return shipping_cost;
    }

    public void setShipping_cost(String shipping_cost) {
        this.shipping_cost = shipping_cost;
    }

    public String getIs_promotional_offer() {
        return is_promotional_offer;
    }

    public void setIs_promotional_offer(String is_promotional_offer) {
        this.is_promotional_offer = is_promotional_offer;
    }

    public String getPromotional_discount_percentage() {
        return promotional_discount_percentage;
    }

    public void setPromotional_discount_percentage(String promotional_discount_percentage) {
        this.promotional_discount_percentage = promotional_discount_percentage;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(String delivery_time) {
        this.delivery_time = delivery_time;
    }

    public List<ParamDoOrderItem> getItems() {
        return items;
    }

    public void setItems(List<ParamDoOrderItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "{" +
                "order_id='" + order_id + '\'' +
                ", transaction_id='" + transaction_id + '\'' +
                ", manufacturer_id='" + manufacturer_id + '\'' +
                ", grand_total='" + grand_total + '\'' +
                ", app_user_id='" + app_user_id + '\'' +
                ", delivery_name='" + delivery_name + '\'' +
                ", delivery_address='" + delivery_address + '\'' +
                ", delivery_email='" + delivery_email + '\'' +
                ", delivery_phone='" + delivery_phone + '\'' +
                ", total_vat='" + total_vat + '\'' +
                ", shipping_cost='" + shipping_cost + '\'' +
                ", is_promotional_offer='" + is_promotional_offer + '\'' +
                ", promotional_discount_percentage='" + promotional_discount_percentage + '\'' +
                ", subtotal='" + subtotal + '\'' +
                ", payment_status='" + payment_status + '\'' +
                ", delivery_time='" + delivery_time + '\'' +
                ", items=" + items +
                '}';
    }
}