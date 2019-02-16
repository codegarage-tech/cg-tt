package tech.codegarage.tidetwist.model;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class ParamKitchenListByPopular {

    private  String per_page_item ="";
    private  String offset ="";
    private  String lat ="";
    private  String lng ="";
    private  String app_user_id ="";

    public ParamKitchenListByPopular(String per_page_item, String offset, String lat, String lng, String app_user_id) {
        this.per_page_item = per_page_item;
        this.offset = offset;
        this.lat = lat;
        this.lng = lng;
        this.app_user_id = app_user_id;
    }

    public String getPer_page_item() {
        return per_page_item;
    }

    public void setPer_page_item(String per_page_item) {
        this.per_page_item = per_page_item;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getApp_user_id() {
        return app_user_id;
    }

    public void setApp_user_id(String app_user_id) {
        this.app_user_id = app_user_id;
    }

    @Override
    public String toString() {
        return "{" +
                "per_page_item='" + per_page_item + '\'' +
                ", offset='" + offset + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", app_user_id='" + app_user_id + '\'' +
                '}';
    }
}
