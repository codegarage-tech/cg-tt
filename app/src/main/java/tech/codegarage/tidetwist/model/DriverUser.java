package tech.codegarage.tidetwist.model;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class DriverUser {

    private String id = "";
    private String first_name = "";
    private String last_name = "";
    private String email = "";
    private String phone = "";
    private String password = "";
    private String image = "";
    private String is_engaged = "";
    private String lat = "";
    private String lng = "";
    private String address = "";
    private String fcm_token = "";
    private String is_available = "";

    public DriverUser() {
    }

    public DriverUser(String id, String first_name, String last_name, String email, String phone, String password, String image, String is_engaged, String lat, String lng, String address, String fcm_token, String is_available) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.image = image;
        this.is_engaged = is_engaged;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.fcm_token = fcm_token;
        this.is_available = is_available;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIs_engaged() {
        return is_engaged;
    }

    public void setIs_engaged(String is_engaged) {
        this.is_engaged = is_engaged;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    public String getIs_available() {
        return is_available;
    }

    public void setIs_available(String is_available) {
        this.is_available = is_available;
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", image='" + image + '\'' +
                ", is_engaged='" + is_engaged + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", address='" + address + '\'' +
                ", fcm_token='" + fcm_token + '\'' +
                ", is_available='" + is_available + '\'' +
                '}';
    }
}