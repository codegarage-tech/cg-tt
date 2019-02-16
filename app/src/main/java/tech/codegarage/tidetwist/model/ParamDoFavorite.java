package tech.codegarage.tidetwist.model;

public class ParamDoFavorite {

    private  String item_id ="";
    private  String app_user_id ="";
    private  String make_favourite ="";

    public ParamDoFavorite(String item_id, String app_user_id, String make_favourite) {
        this.item_id = item_id;
        this.app_user_id = app_user_id;
        this.make_favourite = make_favourite;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getApp_user_id() {
        return app_user_id;
    }

    public void setApp_user_id(String app_user_id) {
        this.app_user_id = app_user_id;
    }

    public String getMake_favourite() {
        return make_favourite;
    }

    public void setMake_favourite(String make_favourite) {
        this.make_favourite = make_favourite;
    }

    @Override
    public String toString() {
        return "{" +
                "item_id='" + item_id + '\'' +
                ", app_user_id='" + app_user_id + '\'' +
                ", make_favourite='" + make_favourite + '\'' +
                '}';
    }
}
