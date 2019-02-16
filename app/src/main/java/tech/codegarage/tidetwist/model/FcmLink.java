package tech.codegarage.tidetwist.model;

import tech.codegarage.tidetwist.base.BaseModel;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class FcmLink extends BaseModel<FcmLink> {

    private String title = "";
    private String url = "";
    private boolean open = false;

    public FcmLink() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public String toString() {
        return "{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", open=" + open +
                '}';
    }
}