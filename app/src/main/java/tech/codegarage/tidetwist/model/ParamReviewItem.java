package tech.codegarage.tidetwist.model;

public class ParamReviewItem {

    private String item_id = "";
    private String app_user_id = "";
    private String review_comment = "";
    private String star = "";
    private String id = "";


    public ParamReviewItem(String item_id, String app_user_id, String review_comment, String star, String id) {
        this.item_id = item_id;
        this.app_user_id = app_user_id;
        this.review_comment = review_comment;
        this.star = star;
        this.id = id;
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

    public String getReview_comment() {
        return review_comment;
    }

    public void setReview_comment(String review_comment) {
        this.review_comment = review_comment;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ParamReviewItem{" +
                "item_id='" + item_id + '\'' +
                ", app_user_id='" + app_user_id + '\'' +
                ", review_comment='" + review_comment + '\'' +
                ", star='" + star + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
