package tech.codegarage.tidetwist.base;

import com.google.gson.Gson;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class BaseModel<F> {

    public static <T> T getResponseObject(String jsonString, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, clazz);
    }

    public static <T> String getResponseString(T object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }
}
