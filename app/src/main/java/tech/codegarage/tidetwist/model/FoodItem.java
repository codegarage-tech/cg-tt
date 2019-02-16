package tech.codegarage.tidetwist.model;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import tech.codegarage.tidetwist.realm.RealmListParcelConverter;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
@Parcel
public class FoodItem extends RealmObject {

    @PrimaryKey
    private String product_id = "";
    private String name = "";
    private String ingredients = "";
    @ParcelPropertyConverter(RealmListParcelConverter.class)
    private RealmList<Image> image = new RealmList<>();
    private String manufacturer_id = "";
    private String price = "";
    private String status = "";
    private String category_id = "";
    private String is_approved = "";
    private String has_offer = "";
    private String offer_price = "";
    private int is_favourite = 0;
    private String review_count = "";
    private String average_rating = "";
    private int item_quantity = 0;

    public FoodItem() {
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public RealmList<Image> getImage() {
        return image;
    }

    public void setImage(RealmList<Image> image) {
        this.image = image;
    }

    public String getManufacturer_id() {
        return manufacturer_id;
    }

    public void setManufacturer_id(String manufacturer_id) {
        this.manufacturer_id = manufacturer_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getIs_approved() {
        return is_approved;
    }

    public void setIs_approved(String is_approved) {
        this.is_approved = is_approved;
    }

    public String getHas_offer() {
        return has_offer;
    }

    public void setHas_offer(String has_offer) {
        this.has_offer = has_offer;
    }

    public String getOffer_price() {
        return offer_price;
    }

    public void setOffer_price(String offer_price) {
        this.offer_price = offer_price;
    }

    public int getIs_favourite() {
        return is_favourite;
    }

    public void setIs_favourite(int is_favourite) {
        this.is_favourite = is_favourite;
    }

    public String getReview_count() {
        return review_count;
    }

    public void setReview_count(String review_count) {
        this.review_count = review_count;
    }

    public String getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(String average_rating) {
        this.average_rating = average_rating;
    }

    public int getItem_quantity() {
        return item_quantity;
    }

    public void setItem_quantity(int item_quantity) {
        this.item_quantity = item_quantity;
    }

    @Override
    public String toString() {
        return "{" +
                "product_id='" + product_id + '\'' +
                ", name='" + name + '\'' +
                ", ingredients='" + ingredients + '\'' +
                ", image=" + image +
                ", manufacturer_id='" + manufacturer_id + '\'' +
                ", price='" + price + '\'' +
                ", status='" + status + '\'' +
                ", category_id='" + category_id + '\'' +
                ", is_approved='" + is_approved + '\'' +
                ", has_offer='" + has_offer + '\'' +
                ", offer_price='" + offer_price + '\'' +
                ", is_favourite=" + is_favourite +
                ", review_count='" + review_count + '\'' +
                ", average_rating='" + average_rating + '\'' +
                ", item_quantity=" + item_quantity +
                '}';
    }
}