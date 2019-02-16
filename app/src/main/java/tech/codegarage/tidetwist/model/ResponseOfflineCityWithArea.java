package tech.codegarage.tidetwist.model;

import java.util.List;

public class ResponseOfflineCityWithArea {

    private String status = "";
    private List<City> data;

    public ResponseOfflineCityWithArea() {
    }

    public ResponseOfflineCityWithArea(String status, List<City> data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<City> getData() {
        return data;
    }

    public void setData(List<City> data) {
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