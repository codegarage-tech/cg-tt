package tech.codegarage.tidetwist.model;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class ParamAppUser {

    private String app_user_id = "";
    private String name = "";
    private String phone = "";
    private String email = "";
    private String lat = "";
    private String lng = "";
    private String address = "";
    private String image = "";
    private String fcm_token = "";

    public ParamAppUser() {
    }

    public ParamAppUser(String app_user_id, String name, String phone, String email, String lat, String lng, String address, String image, String fcm_token) {
        this.app_user_id = app_user_id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.image = image;
        this.fcm_token = fcm_token;
    }

    public String getApp_user_id() {
        return app_user_id;
    }

    public void setApp_user_id(String app_user_id) {
        this.app_user_id = app_user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
                "app_user_id='" + app_user_id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", address='" + address + '\'' +
                ", image='" + image + '\'' +
                ", fcm_token='" + fcm_token + '\'' +
                '}';
    }
}