package tech.codegarage.tidetwist.model;


public class ParamKitchenListByFavorite {

    private  String per_page_item ="";
    private  String app_user_id ="";
    private  String offset ="";

    public ParamKitchenListByFavorite(String per_page_item, String app_user_id, String offset) {
        this.per_page_item = per_page_item;
        this.app_user_id = app_user_id;
        this.offset = offset;
    }

    public String getPer_page_item() {
        return per_page_item;
    }

    public void setPer_page_item(String per_page_item) {
        this.per_page_item = per_page_item;
    }

    public String getApp_user_id() {
        return app_user_id;
    }

    public void setApp_user_id(String app_user_id) {
        this.app_user_id = app_user_id;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "{" +
                "per_page_item='" + per_page_item + '\'' +
                ", app_user_id='" + app_user_id + '\'' +
                ", offset='" + offset + '\'' +
                '}';
    }
}
