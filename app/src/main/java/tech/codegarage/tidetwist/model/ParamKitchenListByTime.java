package tech.codegarage.tidetwist.model;

public class ParamKitchenListByTime {

    private String per_page_item = "";
    private String offset = "";
    private String lat = "";
    private String lng = "";
    private String time_id = "";
    private String app_user_id = "";

    public ParamKitchenListByTime(String per_page_item, String offset, String lat, String lng, String time_id, String app_user_id) {
        this.per_page_item = per_page_item;
        this.offset = offset;
        this.lat = lat;
        this.lng = lng;
        this.time_id = time_id;
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

    public String getTime_id() {
        return time_id;
    }

    public void setTime_id(String time_id) {
        this.time_id = time_id;
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
                ", time_id='" + time_id + '\'' +
                ", app_user_id='" + app_user_id + '\'' +
                '}';
    }
}
