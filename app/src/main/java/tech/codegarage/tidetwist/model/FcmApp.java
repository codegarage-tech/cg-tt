package tech.codegarage.tidetwist.model;

import tech.codegarage.tidetwist.base.BaseModel;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class FcmApp extends BaseModel<FcmApp> {

    private String packageName = "";
    private String name = "";

    public FcmApp() {
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{" +
                "packageName='" + packageName + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}