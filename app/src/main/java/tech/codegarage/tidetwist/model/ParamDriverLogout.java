package tech.codegarage.tidetwist.model;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class ParamDriverLogout {

    private String driver_id = "";

    public ParamDriverLogout(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    @Override
    public String toString() {
        return "{" +
                "driver_id='" + driver_id + '\'' +
                '}';
    }
}