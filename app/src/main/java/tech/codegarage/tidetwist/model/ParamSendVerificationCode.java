package tech.codegarage.tidetwist.model;

public class ParamSendVerificationCode {

    private String phone = "";

    public ParamSendVerificationCode(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "{" +
                "phone='" + phone + '\'' +
                '}';
    }
}
