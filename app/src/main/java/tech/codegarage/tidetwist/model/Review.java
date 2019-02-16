package tech.codegarage.tidetwist.model;

import org.parceler.Parcel;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
@Parcel
public class Review {

    private String id = "";
    private String item_id = "";
    private String app_user_id = "";
    private String star = "";
    private String review_comment = "";
    private String review_date = "";
    private String name = "";
    private String email = "";
    private String address = "";
    private String image = "";

    public Review() {
    }

    public Review(String id, String item_id, String app_user_id, String star, String review_comment, String review_date, String name, String email, String address, String image) {
        this.id = id;
        this.item_id = item_id;
        this.app_user_id = app_user_id;
        this.star = star;
        this.review_comment = review_comment;
        this.review_date = review_date;
        this.name = name;
        this.email = email;
        this.address = address;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getApp_user_id() {
        return app_user_id;
    }

    public void setApp_user_id(String app_user_id) {
        this.app_user_id = app_user_id;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getReview_comment() {
        return review_comment;
    }

    public void setReview_comment(String review_comment) {
        this.review_comment = review_comment;
    }

    public String getReview_date() {
        return review_date;
    }

    public void setReview_date(String review_date) {
        this.review_date = review_date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    @Override
    public String toString() {
        return "Review{" +
                "id='" + id + '\'' +
                ", item_id='" + item_id + '\'' +
                ", app_user_id='" + app_user_id + '\'' +
                ", star='" + star + '\'' +
                ", review_comment='" + review_comment + '\'' +
                ", review_date='" + review_date + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}