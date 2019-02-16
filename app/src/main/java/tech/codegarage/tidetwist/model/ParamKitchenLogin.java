package tech.codegarage.tidetwist.model;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class ParamKitchenLogin {

    private String contact_person_phone = "";
    private String password = "";

    public ParamKitchenLogin(String contact_person_phone, String password) {
        this.contact_person_phone = contact_person_phone;
        this.password = password;
    }

    public String getContact_person_phone() {
        return contact_person_phone;
    }

    public void setContact_person_phone(String contact_person_phone) {
        this.contact_person_phone = contact_person_phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "{" +
                "contact_person_phone='" + contact_person_phone + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}