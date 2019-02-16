package tech.codegarage.tidetwist.model;

import java.util.ArrayList;
import java.util.List;

public class ParamAddFoodItem {

    private String product_id = "";
    private String name = "";
    private String manufacturer_id = "";
    private String price = "";
    private String status = "";
    private String category_id = "";
    private String has_offer = "";
    private String offer_price = "";
    private List<String> image = new ArrayList<>();
    private String ingredients = "";

    public ParamAddFoodItem(String product_id, String name, String manufacturer_id, String price, String status, String category_id, String has_offer, String offer_price, List<String> image, String ingredients) {
        this.product_id = product_id;
        this.name = name;
        this.manufacturer_id = manufacturer_id;
        this.price = price;
        this.status = status;
        this.category_id = category_id;
        this.has_offer = has_offer;
        this.offer_price = offer_price;
        this.image = image;
        this.ingredients = ingredients;
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

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return "{" +
                "product_id='" + product_id + '\'' +
                ", name='" + name + '\'' +
                ", manufacturer_id='" + manufacturer_id + '\'' +
                ", price='" + price + '\'' +
                ", status='" + status + '\'' +
                ", category_id='" + category_id + '\'' +
                ", has_offer='" + has_offer + '\'' +
                ", offer_price='" + offer_price + '\'' +
                ", image=" + image +
                ", ingredients='" + ingredients + '\'' +
                '}';
    }
}
