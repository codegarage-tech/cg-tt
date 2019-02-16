package tech.codegarage.tidetwist.model;

import tech.codegarage.tidetwist.base.BaseModel;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class FcmText extends BaseModel<FcmText> {

    private String title = "";
    private String message = "";
    private boolean clipboard = false;

    public FcmText() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isClipboard() {
        return clipboard;
    }

    public void setClipboard(boolean clipboard) {
        this.clipboard = clipboard;
    }

    @Override
    public String toString() {
        return "{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", clipboard=" + clipboard +
                '}';
    }
}