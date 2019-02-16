package tech.codegarage.dropdownmenuplus.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class TitleBean<SubtitleBean> implements Serializable {

    String id;
    String name;
    List<SubtitleBean> subtitle;

    public TitleBean() {
    }

    public TitleBean(String id, String name, List<SubtitleBean> subtitle) {
        this.id = id;
        this.name = name;
        this.subtitle = subtitle;
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

    public List<SubtitleBean> getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(ArrayList<SubtitleBean> subtitle) {
        this.subtitle = subtitle;
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", subtitle=" + subtitle +
                '}';
    }
}
