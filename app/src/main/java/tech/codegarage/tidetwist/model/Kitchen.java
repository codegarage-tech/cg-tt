package tech.codegarage.tidetwist.model;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
@Parcel
public class Kitchen {

    private String manufacturer_id = "";
    private String name = "";
    private String address = "";
    private String country_id = "";
    private String zone_id = "";
    private String cuisine = "";
    private String min_order_value = "";
    private String est_delivery_time = "";
    private String opening_schedule = "";
    private List<Image> image = new ArrayList<>();
    private String sort_order = "";
    private String is_active = "";
    private String contact_person_name = "";
    private String contact_person_email = "";
    private String contact_person_phone = "";
    private String password = "";
    private String lat = "";
    private String lng = "";
    private String is_approved = "";
    private String preparation_time = "";
    private String end_time = "";
    private String open_time = "";
    private String is_available = "";
    private String distance = "";
    private String is_opened = "";
    private String next_open_time = "";
    private String average_rating = "";
    private List<Cuisine> items = new ArrayList<>();

    public Kitchen() {
    }

    public String getManufacturer_id() {
        return manufacturer_id;
    }

    public void setManufacturer_id(String manufacturer_id) {
        this.manufacturer_id = manufacturer_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getZone_id() {
        return zone_id;
    }

    public void setZone_id(String zone_id) {
        this.zone_id = zone_id;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getMin_order_value() {
        return min_order_value;
    }

    public void setMin_order_value(String min_order_value) {
        this.min_order_value = min_order_value;
    }

    public String getEst_delivery_time() {
        return est_delivery_time;
    }

    public void setEst_delivery_time(String est_delivery_time) {
        this.est_delivery_time = est_delivery_time;
    }

    public String getOpening_schedule() {
        return opening_schedule;
    }

    public void setOpening_schedule(String opening_schedule) {
        this.opening_schedule = opening_schedule;
    }

    public List<Image> getImage() {
        return image;
    }

    public void setImage(List<Image> image) {
        this.image = image;
    }

    public String getSort_order() {
        return sort_order;
    }

    public void setSort_order(String sort_order) {
        this.sort_order = sort_order;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getContact_person_name() {
        return contact_person_name;
    }

    public void setContact_person_name(String contact_person_name) {
        this.contact_person_name = contact_person_name;
    }

    public String getContact_person_email() {
        return contact_person_email;
    }

    public void setContact_person_email(String contact_person_email) {
        this.contact_person_email = contact_person_email;
    }

    public String getContact_person_phone() {
        return contact_person_phone;
    }

    public void setContact_person_phone(String contact_person_phone) {
        this.contact_person_phone = contact_person_phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getIs_approved() {
        return is_approved;
    }

    public void setIs_approved(String is_approved) {
        this.is_approved = is_approved;
    }

    public String getPreparation_time() {
        return preparation_time;
    }

    public void setPreparation_time(String preparation_time) {
        this.preparation_time = preparation_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getOpen_time() {
        return open_time;
    }

    public void setOpen_time(String open_time) {
        this.open_time = open_time;
    }

    public String getIs_available() {
        return is_available;
    }

    public void setIs_available(String is_available) {
        this.is_available = is_available;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getIs_opened() {
        return is_opened;
    }

    public void setIs_opened(String is_opened) {
        this.is_opened = is_opened;
    }

    public String getNext_open_time() {
        return next_open_time;
    }

    public void setNext_open_time(String next_open_time) {
        this.next_open_time = next_open_time;
    }

    public List<Cuisine> getItems() {
        return items;
    }

    public void setItems(List<Cuisine> items) {
        this.items = items;
    }

    public String getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(String average_rating) {
        this.average_rating = average_rating;
    }

    @Override
    public String toString() {
        return "{" +
                "manufacturer_id='" + manufacturer_id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", country_id='" + country_id + '\'' +
                ", zone_id='" + zone_id + '\'' +
                ", cuisine='" + cuisine + '\'' +
                ", min_order_value='" + min_order_value + '\'' +
                ", est_delivery_time='" + est_delivery_time + '\'' +
                ", opening_schedule='" + opening_schedule + '\'' +
                ", image='" + image + '\'' +
                ", sort_order='" + sort_order + '\'' +
                ", is_active='" + is_active + '\'' +
                ", contact_person_name='" + contact_person_name + '\'' +
                ", contact_person_email='" + contact_person_email + '\'' +
                ", contact_person_phone='" + contact_person_phone + '\'' +
                ", password='" + password + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", is_approved='" + is_approved + '\'' +
                ", preparation_time='" + preparation_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", open_time='" + open_time + '\'' +
                ", is_available='" + is_available + '\'' +
                ", distance='" + distance + '\'' +
                ", is_opened='" + is_opened + '\'' +
                ", next_open_time='" + next_open_time + '\'' +
                ", items=" + items +
                '}';
    }
}
