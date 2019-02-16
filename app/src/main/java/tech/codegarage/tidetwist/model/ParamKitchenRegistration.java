package tech.codegarage.tidetwist.model;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class ParamKitchenRegistration {

    private String manufacturer_id = "";
    private String name = "";
    private String address = "";
    private String zone_id = "";
    private String lat = "";
    private String lng = "";
    private String contact_person_email = "";
    private String password = "";
    private String contact_person_phone = "";
    private String fcm_token = "";

    public ParamKitchenRegistration(String manufacturer_id, String name, String address, String zone_id, String lat, String lng, String contact_person_email, String password, String contact_person_phone, String fcm_token) {
        this.manufacturer_id = manufacturer_id;
        this.name = name;
        this.address = address;
        this.zone_id = zone_id;
        this.lat = lat;
        this.lng = lng;
        this.contact_person_email = contact_person_email;
        this.password = password;
        this.contact_person_phone = contact_person_phone;
        this.fcm_token = fcm_token;
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

    public String getZone_id() {
        return zone_id;
    }

    public void setZone_id(String zone_id) {
        this.zone_id = zone_id;
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

    public String getContact_person_email() {
        return contact_person_email;
    }

    public void setContact_person_email(String contact_person_email) {
        this.contact_person_email = contact_person_email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContact_person_phone() {
        return contact_person_phone;
    }

    public void setContact_person_phone(String contact_person_phone) {
        this.contact_person_phone = contact_person_phone;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    @Override
    public String toString() {
        return "{" +
                "manufacturer_id='" + manufacturer_id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", zone_id='" + zone_id + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", contact_person_email='" + contact_person_email + '\'' +
                ", password='" + password + '\'' +
                ", contact_person_phone='" + contact_person_phone + '\'' +
                ", fcm_token='" + fcm_token + '\'' +
                '}';
    }
}