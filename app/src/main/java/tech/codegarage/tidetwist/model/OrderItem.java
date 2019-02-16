package tech.codegarage.tidetwist.model;

import org.parceler.Parcel;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
@Parcel
public class OrderItem {

    private String name = "";
    private String image = "";
    private String qty = "";
    private String price = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", qty='" + qty + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}