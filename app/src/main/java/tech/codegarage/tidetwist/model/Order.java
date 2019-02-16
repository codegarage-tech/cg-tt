package tech.codegarage.tidetwist.model;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import tech.codegarage.tidetwist.enumeration.OrderStatusType;
import tech.codegarage.tidetwist.enumeration.PaymentStatusType;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
@Parcel
public class Order {

    private String order_id = "";
    private String order_number = "";
    private String manufacturer_id = "";
    private String grand_total = "";
    private String app_user_id = "";
    private String app_user_image = "";
    private String delivery_name = "";
    private String delivery_address = "";
    private String delivery_email = "";
    private String delivery_phone = "";
    private String delivery_time = "";
    private String total_vat = "";
    private String order_status = "";
    private String order_datetime = "";
    private String is_verified = "";
    private String assigned_drivers = "";
    private String shipping_cost = "";
    private String subtotal = "";
    private String is_promotional_offer = "";
    private String promotional_discount_percentage = "";
    private String payment_status = PaymentStatusType.NONE.toString();
    private String transaction_id = "";
    private String manufacturer_name = "";
    private String address = "";
    private String manufacturer_image = "";
    private String manufacturer_cuisine = "";
    private String manufacturer_average_rating = "";
    private String manufacturer_phone = "";
    private String status = OrderStatusType.NONE.toString();
    private String fcm_token = "";
    private String drivers_first_name = "";
    private String drivers_last_name = "";
    private String drivers_image = "";
    private String drivers_phone = "";
    private String driver_fcm_token = "";
    private String kitchen_fcm_token = "";
    private List<OrderStatus> statusLists = new ArrayList<>();
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
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

    public String getDelivery_phone() {
        return delivery_phone;
    }

    public void setDelivery_phone(String delivery_phone) {
        this.delivery_phone = delivery_phone;
    }

    public String getDelivery_email() {
        return delivery_email;
    }

    public void setDelivery_email(String delivery_email) {
        this.delivery_email = delivery_email;
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

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getManufacturer_name() {
        return manufacturer_name;
    }

    public void setManufacturer_name(String manufacturer_name) {
        this.manufacturer_name = manufacturer_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getManufacturer_image() {
        return manufacturer_image;
    }

    public void setManufacturer_image(String manufacturer_image) {
        this.manufacturer_image = manufacturer_image;
    }

    public String getManufacturer_cuisine() {
        return manufacturer_cuisine;
    }

    public void setManufacturer_cuisine(String manufacturer_cuisine) {
        this.manufacturer_cuisine = manufacturer_cuisine;
    }

    public String getManufacturer_average_rating() {
        return manufacturer_average_rating;
    }

    public void setManufacturer_average_rating(String manufacturer_average_rating) {
        this.manufacturer_average_rating = manufacturer_average_rating;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    public String getDriver_fcm_token() {
        return driver_fcm_token;
    }

    public void setDriver_fcm_token(String driver_fcm_token) {
        this.driver_fcm_token = driver_fcm_token;
    }

    public String getKitchen_fcm_token() {
        return kitchen_fcm_token;
    }

    public void setKitchen_fcm_token(String kitchen_fcm_token) {
        this.kitchen_fcm_token = kitchen_fcm_token;
    }

    public List<OrderStatus> getStatusLists() {
        return statusLists;
    }

    public void setStatusLists(List<OrderStatus> statusLists) {
        this.statusLists = statusLists;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getApp_user_image() {
        return app_user_image;
    }

    public void setApp_user_image(String app_user_image) {
        this.app_user_image = app_user_image;
    }

    public String getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(String delivery_time) {
        this.delivery_time = delivery_time;
    }

    public String getManufacturer_phone() {
        return manufacturer_phone;
    }

    public void setManufacturer_phone(String manufacturer_phone) {
        this.manufacturer_phone = manufacturer_phone;
    }

    public String getDrivers_first_name() {
        return drivers_first_name;
    }

    public void setDrivers_first_name(String drivers_first_name) {
        this.drivers_first_name = drivers_first_name;
    }

    public String getDrivers_last_name() {
        return drivers_last_name;
    }

    public void setDrivers_last_name(String drivers_last_name) {
        this.drivers_last_name = drivers_last_name;
    }

    public String getDrivers_image() {
        return drivers_image;
    }

    public void setDrivers_image(String drivers_image) {
        this.drivers_image = drivers_image;
    }

    public String getDrivers_phone() {
        return drivers_phone;
    }

    public void setDrivers_phone(String drivers_phone) {
        this.drivers_phone = drivers_phone;
    }

    @Override
    public String toString() {
        return "{" +
                "order_id='" + order_id + '\'' +
                ", order_number='" + order_number + '\'' +
                ", manufacturer_id='" + manufacturer_id + '\'' +
                ", grand_total='" + grand_total + '\'' +
                ", app_user_id='" + app_user_id + '\'' +
                ", app_user_image='" + app_user_image + '\'' +
                ", delivery_name='" + delivery_name + '\'' +
                ", delivery_address='" + delivery_address + '\'' +
                ", delivery_email='" + delivery_email + '\'' +
                ", delivery_phone='" + delivery_phone + '\'' +
                ", delivery_time='" + delivery_time + '\'' +
                ", total_vat='" + total_vat + '\'' +
                ", order_status='" + order_status + '\'' +
                ", order_datetime='" + order_datetime + '\'' +
                ", is_verified='" + is_verified + '\'' +
                ", assigned_drivers='" + assigned_drivers + '\'' +
                ", shipping_cost='" + shipping_cost + '\'' +
                ", subtotal='" + subtotal + '\'' +
                ", is_promotional_offer='" + is_promotional_offer + '\'' +
                ", promotional_discount_percentage='" + promotional_discount_percentage + '\'' +
                ", payment_status='" + payment_status + '\'' +
                ", transaction_id='" + transaction_id + '\'' +
                ", manufacturer_name='" + manufacturer_name + '\'' +
                ", address='" + address + '\'' +
                ", manufacturer_image='" + manufacturer_image + '\'' +
                ", manufacturer_cuisine='" + manufacturer_cuisine + '\'' +
                ", manufacturer_average_rating='" + manufacturer_average_rating + '\'' +
                ", manufacturer_phone='" + manufacturer_phone + '\'' +
                ", status='" + status + '\'' +
                ", fcm_token='" + fcm_token + '\'' +
                ", drivers_first_name='" + drivers_first_name + '\'' +
                ", drivers_last_name='" + drivers_last_name + '\'' +
                ", drivers_image='" + drivers_image + '\'' +
                ", drivers_phone='" + drivers_phone + '\'' +
                ", driver_fcm_token='" + driver_fcm_token + '\'' +
                ", kitchen_fcm_token='" + kitchen_fcm_token + '\'' +
                ", statusLists=" + statusLists +
                ", items=" + items +
                '}';
    }
}
