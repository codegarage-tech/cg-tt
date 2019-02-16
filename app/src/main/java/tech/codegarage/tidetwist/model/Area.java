package tech.codegarage.tidetwist.model;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class Area {

    private String zone_id = "";
    private String name = "";

    public Area(String zone_id, String name) {
        this.zone_id = zone_id;
        this.name = name;
    }

    public String getZone_id() {
        return zone_id;
    }

    public void setZone_id(String zone_id) {
        this.zone_id = zone_id;
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
                "zone_id='" + zone_id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
