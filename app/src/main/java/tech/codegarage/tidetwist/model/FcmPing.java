package tech.codegarage.tidetwist.model;

import tech.codegarage.tidetwist.base.BaseModel;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class FcmPing extends BaseModel<FcmPing> {

    private String title = "";

    public FcmPing() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "{" +
                "title='" + title + '\'' +
                '}';
    }
}