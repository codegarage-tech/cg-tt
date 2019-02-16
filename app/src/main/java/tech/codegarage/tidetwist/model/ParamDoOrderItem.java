package tech.codegarage.tidetwist.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class ParamDoOrderItem {

    private String item_id = "";
    private String qty = "";
    private String price = "";

    public ParamDoOrderItem(String item_id, String qty, String price) {
        this.item_id = item_id;
        this.qty = qty;
        this.price = price;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
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
                "item_id='" + item_id + '\'' +
                ", qty='" + qty + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}