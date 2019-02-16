package tech.codegarage.tidetwist.model;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
@Parcel
public  class FoodCategory {

    private String category_id = "";
    private String name = "";
    private List<FoodItem> food_items = new ArrayList<>();

    public FoodCategory() {
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FoodItem> getFood_items() {
        return food_items;
    }

    public void setFood_items(List<FoodItem> food_items) {
        this.food_items = food_items;
    }

    @Override
    public String toString() {
        return "{" +
                "category_id='" + category_id + '\'' +
                ", name='" + name + '\'' +
                ", food_items=" + food_items +
                '}';
    }
}
