package tech.codegarage.tidetwist.model;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class ParamKitchenLogout {

    private String manufacturer_id = "";

    public ParamKitchenLogout(String manufacturer_id) {
        this.manufacturer_id = manufacturer_id;
    }

    public String getManufacturer_id() {
        return manufacturer_id;
    }

    public void setManufacturer_id(String manufacturer_id) {
        this.manufacturer_id = manufacturer_id;
    }

    @Override
    public String toString() {
        return "{" +
                "manufacturer_id='" + manufacturer_id + '\'' +
                '}';
    }
}