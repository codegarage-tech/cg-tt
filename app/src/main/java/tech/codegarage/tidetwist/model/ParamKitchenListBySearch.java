package tech.codegarage.tidetwist.model;

public class ParamKitchenListBySearch {

    private  String per_page_item ="";
    private  String offset ="";
    private  String cusine ="";
    private  String zone_id ="";
    private  String app_user_id ="";

    public ParamKitchenListBySearch(String per_page_item, String offset, String cusine, String zone_id, String app_user_id) {
        this.per_page_item = per_page_item;
        this.offset = offset;
        this.cusine = cusine;
        this.zone_id = zone_id;
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

    public String getCusine() {
        return cusine;
    }

    public void setCusine(String cusine) {
        this.cusine = cusine;
    }

    public String getZone_id() {
        return zone_id;
    }

    public void setZone_id(String zone_id) {
        this.zone_id = zone_id;
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
                ", cusine='" + cusine + '\'' +
                ", zone_id='" + zone_id + '\'' +
                ", app_user_id='" + app_user_id + '\'' +
                '}';
    }
}
