package tech.codegarage.tidetwist.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class City {

    private String country_id = "";
    private String name = "";
    private List<Area> area = new ArrayList<>();

    public City(String country_id, String name, List<Area> area) {
        this.country_id = country_id;
        this.name = name;
        this.area = area;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Area> getArea() {
        return area;
    }

    public void setArea(List<Area> area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "{" +
                "country_id='" + country_id + '\'' +
                ", name='" + name + '\'' +
                ", area=" + area +
                '}';
    }
}
