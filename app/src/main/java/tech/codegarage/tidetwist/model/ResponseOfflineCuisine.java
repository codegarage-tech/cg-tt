package tech.codegarage.tidetwist.model;

import java.util.List;

public class ResponseOfflineCuisine {

    private String status = "";
    private List<Cuisine> data;

    public ResponseOfflineCuisine() {
    }

    public ResponseOfflineCuisine(String status, List<Cuisine> data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Cuisine> getData() {
        return data;
    }

    public void setData(List<Cuisine> data) {
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
