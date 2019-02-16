package tech.codegarage.tidetwist.model;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
@Parcel
public class Cuisine {

    private String id = "";
    private String name = "";
    private String image = "";
    private String active = "";
    private List<FoodCategory> food_category = new ArrayList<>();

    public Cuisine() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public List<FoodCategory> getFood_category() {
        return food_category;
    }

    public void setFood_category(List<FoodCategory> food_category) {
        this.food_category = food_category;
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", active='" + active + '\'' +
                ", food_category=" + food_category +
                '}';
    }
}