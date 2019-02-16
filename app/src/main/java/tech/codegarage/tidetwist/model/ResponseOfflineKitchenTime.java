package tech.codegarage.tidetwist.model;

import java.util.List;

public class ResponseOfflineKitchenTime {

    private String status = "";
    private List<KitchenTime> data;

    public ResponseOfflineKitchenTime() {
    }

    public ResponseOfflineKitchenTime(String status, List<KitchenTime> data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<KitchenTime> getData() {
        return data;
    }

    public void setData(List<KitchenTime> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
