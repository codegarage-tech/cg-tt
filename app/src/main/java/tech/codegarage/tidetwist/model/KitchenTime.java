package tech.codegarage.tidetwist.model;

import org.parceler.Parcel;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
@Parcel
public class KitchenTime {

    private String time_id = "";
    private String prepare_time = "";
    private String active = "";
    private String image = "";
    private boolean isSelected = false;

    public KitchenTime() {
    }

    public KitchenTime(String time_id, String prepare_time, String active, String image) {
        this.time_id = time_id;
        this.prepare_time = prepare_time;
        this.active = active;
        this.image = image;
    }

    public String getTime_id() {
        return time_id;
    }

    public void setTime_id(String time_id) {
        this.time_id = time_id;
    }

    public String getPrepare_time() {
        return prepare_time;
    }

    public void setPrepare_time(String prepare_time) {
        this.prepare_time = prepare_time;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "{" +
                "time_id='" + time_id + '\'' +
                ", prepare_time='" + prepare_time + '\'' +
                ", active='" + active + '\'' +
                ", image='" + image + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}