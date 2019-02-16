package tech.codegarage.tidetwist.model;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
@Parcel
public class DoOrder {

    private String order_id = "";
    private String manufacturer_id = "";
    private String grand_total = "";
    private String app_user_id = "";
    private String delivery_name = "";
    private String delivery_address = "";
    private String delivery_email = "";
    private String delivery_phone = "";
    private String total_vat = "";
    private String order_status = "";
    private String order_datetime = "";
    private String is_verified = "";
    private String assigned_drivers = "";
    private String shipping_cost = "";
    private String subtotal = "";

    public DoOrder() {
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
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

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getOrder_datetime() {
        return order_datetime;
    }

    public void setOrder_datetime(String order_datetime) {
        this.order_datetime = order_datetime;
    }

    public String getIs_verified() {
        return is_verified;
    }

    public void setIs_verified(String is_verified) {
        this.is_verified = is_verified;
    }

    public String getAssigned_drivers() {
        return assigned_drivers;
    }

    public void setAssigned_drivers(String assigned_drivers) {
        this.assigned_drivers = assigned_drivers;
    }

    public String getShipping_cost() {
        return shipping_cost;
    }

    public void setShipping_cost(String shipping_cost) {
        this.shipping_cost = shipping_cost;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "{" +
                "order_id='" + order_id + '\'' +
                ", manufacturer_id='" + manufacturer_id + '\'' +
                ", grand_total='" + grand_total + '\'' +
                ", app_user_id='" + app_user_id + '\'' +
                ", delivery_name='" + delivery_name + '\'' +
                ", delivery_address='" + delivery_address + '\'' +
                ", delivery_email='" + delivery_email + '\'' +
                ", delivery_phone='" + delivery_phone + '\'' +
                ", total_vat='" + total_vat + '\'' +
                ", order_status='" + order_status + '\'' +
                ", order_datetime='" + order_datetime + '\'' +
                ", is_verified='" + is_verified + '\'' +
                ", assigned_drivers='" + assigned_drivers + '\'' +
                ", shipping_cost='" + shipping_cost + '\'' +
                ", subtotal='" + subtotal + '\'' +
                '}';
    }
}