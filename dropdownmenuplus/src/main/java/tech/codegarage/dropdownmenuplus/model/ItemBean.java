package tech.codegarage.dropdownmenuplus.model;

import java.io.Serializable;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class ItemBean implements Serializable {

    String id;
    String name;

    public ItemBean() {
    }

    public ItemBean(String id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
