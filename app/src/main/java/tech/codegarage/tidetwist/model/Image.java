package tech.codegarage.tidetwist.model;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
@Parcel
public class Image extends RealmObject {

    @PrimaryKey
    private String id = "";
    private String type = "";
    private String type_id = "";
    private String image = "";

    public Image() {
    }

    public Image(String id, String type, String type_id, String image) {
        this.id = id;
        this.type = type;
        this.type_id = type_id;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", type_id='" + type_id + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
