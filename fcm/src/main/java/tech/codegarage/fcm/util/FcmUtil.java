package tech.codegarage.fcm.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import tech.codegarage.fcm.R;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class FcmUtil {

    private static final String TAG = FcmUtil.class.getSimpleName();
    private static final String SESSION_KEY_TOKEN_USER = "SESSION_KEY_TOKEN_USER";
    private static final String SESSION_KEY_TOKEN_KITCHEN = "SESSION_KEY_TOKEN_KITCHEN";
    private static final String SESSION_KEY_TOKEN_DRIVER = "SESSION_KEY_TOKEN_DRIVER";
    private static final String SESSION_KEY_IS_FCM_REGISTERED = "SESSION_KEY_REGISTER";
    private static final String SESSION_KEY_APP_NOTIFICATION = "SESSION_KEY_APP_NOTIFICATION";
    private static final String SESSION_KEY_ORDER_NOTIFICATION = "SESSION_KEY_ORDER_NOTIFICATION";
    public static final String INTENT_KEY_DETAIL_TYPE = "INTENT_KEY_DETAIL_TYPE";
    public static final String INTENT_KEY_MESSAGE = "INTENT_KEY_MESSAGE";

    public static String printStackTrace(@NonNull Throwable exception) {
        StringWriter trace = new StringWriter();
        exception.printStackTrace(new PrintWriter(trace));
        return trace.toString();
    }

    public static void copyToClipboard(Context context, CharSequence text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(null, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
    }

    static String safeReplaceToAlphanum(@Nullable String string) {
        if (string == null) {
            return null;
        }
        return string.replaceAll("[^a-zA-Z0-9]", "");
    }

    public static void saveToken(Context context, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        switch (FcmManager.getFlavorType()) {
            case USER:
                editor.putString(SESSION_KEY_TOKEN_USER, value);
                break;
            case KITCHEN:
                editor.putString(SESSION_KEY_TOKEN_KITCHEN, value);
                break;
            case DRIVER:
                editor.putString(SESSION_KEY_TOKEN_DRIVER, value);
                break;
        }
        editor.commit();
    }

    public static String getToken(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        switch (FcmManager.getFlavorType()) {
            case USER:
                return sp.getString(SESSION_KEY_TOKEN_USER, "");
            case KITCHEN:
                return sp.getString(SESSION_KEY_TOKEN_KITCHEN, "");
            case DRIVER:
                return sp.getString(SESSION_KEY_TOKEN_DRIVER, "");
        }
        return "";
    }

    public static void saveIsAppNotification(Context context, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(SESSION_KEY_APP_NOTIFICATION, value);
        editor.commit();
    }

    public static int getIsAppNotification(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(SESSION_KEY_APP_NOTIFICATION, 1);
    }

    public static void saveIsOrderNotification(Context context, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(SESSION_KEY_ORDER_NOTIFICATION, value);
        editor.commit();
    }

    public static int getIsOrderNotification(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(SESSION_KEY_ORDER_NOTIFICATION, 1);
    }

    public static int getIsFcmRegistered(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(SESSION_KEY_IS_FCM_REGISTERED, 0);
    }

    public static void saveIsFcmRegistered(Context context, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(SESSION_KEY_IS_FCM_REGISTERED, value);
        editor.commit();
    }

//    public static void registerFcm() {
//        FirebaseMessaging.getInstance().subscribeToTopic(FcmManager.getFlavorType().name()).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, TAG + ">> " + FcmManager.getFlavorType().name() + " subscribeToTopic successful");
//            }
//        });
//    }
//
//    public static void unregisterFcm() {
//        FirebaseMessaging.getInstance().unsubscribeFromTopic(FcmManager.getFlavorType().name()).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, TAG + ">> " + FcmManager.getFlavorType().name() + " unsubscribeFromTopic successful");
//            }
//        });
//    }

    public static boolean isNullOrEmpty(String myString) {
        if (myString == null) {
            return true;
        }
        if (myString.length() == 0 || myString.equalsIgnoreCase("null")
                || myString.equalsIgnoreCase("")) {
            return true;
        }
        return false;
    }

    public static String getAnyKeyValueFromJson(String jsonResponse, String key) {
        String value = "";
        try {
            //Create json object from string
            JSONObject newJson = new JSONObject(jsonResponse);

            // Get keys from json
            Iterator<String> panelKeys = newJson.keys();

            while (panelKeys.hasNext()) {
                // get key from list
                JSONObject panel = newJson.getJSONObject(panelKeys.next());
                value = panel.getString(key);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            value = "";
        }

        return value;
    }
}