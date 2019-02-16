package tech.codegarage.fcm.payload;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateUtils;
import android.text.style.StyleSpan;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tech.codegarage.fcm.R;
import tech.codegarage.fcm.util.Truss;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public abstract class Payload implements Comparable<Payload> {

    //Broadcast
    private static final String INTENT_ACTION = "Payload.Update";
    private static final IntentFilter INTENT_FILTER = new IntentFilter(INTENT_ACTION);
    public static final Intent INTENT = new Intent(INTENT_ACTION);

    private static final String KEY = "payloads";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    long timestamp;

    @Nullable
    transient RemoteMessage message;

    transient CharSequence display;

    transient CharSequence displayRaw;

    Payload() {
        this.timestamp = System.currentTimeMillis();
    }

    Payload(@NonNull RemoteMessage message) {
        this.timestamp = System.currentTimeMillis();
        this.message = message;
    }

    @NonNull
    public static Payload with(RemoteMessage message) {

        Log.d("Payload", "Payload(message): " + message.getData());
        Map<String, String> data = message.getData();
        Set<Map.Entry<String, String>> entries = data.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            Log.d("Payload", "Payload(key): " + entry.getKey());
            try {
                switch (entry.getKey()) {
                    case PingPayload.KEY:
                        return PingPayload.create(message);
                    case TextPayload.KEY:
                        return TextPayload.create(message);
                    case LinkPayload.KEY:
                        return LinkPayload.create(message);
                    case AppPayload.KEY:
                        return AppPayload.create(message);
                    case OrderPayload.KEY:
                        return OrderPayload.create(message);
                    default:
                        break;
                }
            } catch (Exception ignored) {
            }
        }
        return RawPayload.create(message);
    }

    @Nullable
    public static Payload with(@NonNull String key, @NonNull String value) {
        final String[] split = key.split("\\|");
        if (split.length != 2) {
            return null;
        }
        switch (split[1]) {
            case PingPayload.KEY:
                return GSON.fromJson(value, PingPayload.class);
            case TextPayload.KEY:
                return GSON.fromJson(value, TextPayload.class);
            case LinkPayload.KEY:
                return GSON.fromJson(value, LinkPayload.class);
            case AppPayload.KEY:
                return GSON.fromJson(value, AppPayload.class);
            case RawPayload.KEY:
                return GSON.fromJson(value, RawPayload.class);
            case OrderPayload.KEY:
                return GSON.fromJson(value, OrderPayload.class);
            default:
                return null;
        }
    }

    @NonNull
    public static List<Payload> fetchPayloads(@NonNull Context context) {
        final List<Payload> payloads = new ArrayList<>();
        final SharedPreferences sp = getSharedPreferences(context);
        for (Map.Entry<String, ?> entry : sp.getAll().entrySet()) {
            final String key = entry.getKey();
            final String value = (String) entry.getValue();
            Payload payload = with(key, value);
            if (payload != null) {
                payloads.add(payload);
            }
        }
        Collections.sort(payloads);
        return payloads;
    }

    @Nullable
    static String extractPayloadData(RemoteMessage message, String key) {
        return message.getData().get(key);
    }

    public static boolean remove(@NonNull Context context, @NonNull Payload payload) {
        payload.cancelNotification(context);
        final SharedPreferences sp = getSharedPreferences(context);
        for (Map.Entry<String, ?> entry : sp.getAll().entrySet()) {
            if (entry.getKey().equals(payload.timestamp + "|" + payload.key())) {
                sp.edit().remove(entry.getKey()).apply();
                return true;
            }
        }
        return false;
    }

    public static void removeAll(Context context) {
        getNotificationManager(context).cancelAll();
        getSharedPreferences(context).edit().clear().apply();
    }

    public static void registerOnSharedPreferenceChanges(@NonNull Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getSharedPreferences(context).registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterOnSharedPreferenceChanges(@NonNull Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getSharedPreferences(context).unregisterOnSharedPreferenceChangeListener(listener);
    }

    public static void registerPayloadUpdate(@NonNull Context context, BroadcastReceiver broadcastReceiver) {
        LocalBroadcastManager instance = LocalBroadcastManager.getInstance(context);
        instance.registerReceiver(broadcastReceiver, INTENT_FILTER);
    }

    public static void unregisterPayloadUpdate(@NonNull Context context, BroadcastReceiver broadcastReceiver) {
        LocalBroadcastManager instance = LocalBroadcastManager.getInstance(context);
        instance.unregisterReceiver(broadcastReceiver);
    }

    private static SharedPreferences getSharedPreferences(@NonNull Context context) {
        return context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
    }

    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public abstract String key();

    public final Payload saveToSharedPreferences(@NonNull Context context) {
        getSharedPreferences(context).edit().putString(timestamp + "|" + key(), GSON.toJson(this)).apply();
        return this;
    }

    public boolean shouldShowNotification() {
        return !(message != null && Boolean.valueOf(message.getData().get("hide")));
    }

    public abstract void execute(Context context);

    public abstract void showNotification(Context context);

    public abstract void cancelNotification(Context context);

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createNotificationChannel(Context context) {
        String id = context.getString(R.string.notification_channel_id);
        CharSequence name = context.getString(R.string.notification_channel_name);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setShowBadge(true);
        getNotificationManager(context).createNotificationChannel(channel);
    }

    @NonNull
    final NotificationCompat.Builder getNotificationBuilder(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context);
        }
        return new NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentIntent(createPendingIntent(context))
                .setLocalOnly(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
    }

    final void showNotification(@NonNull Context context, @NonNull Notification notification, @NonNull String tag, @IdRes int id) {
        getNotificationManager(context).notify(tag, id, notification);
    }

    final void showNotification(@NonNull Context context, @NonNull Notification notification, @IdRes int id) {
        getNotificationManager(context).notify(id, notification);
    }

    final void cancelNotification(@NonNull Context context, @NonNull String tag, @IdRes int id) {
        getNotificationManager(context).cancel(tag, id);
    }

    final void cancelNotification(@NonNull Context context, @IdRes int id) {
        getNotificationManager(context).cancel(id);
    }

    public abstract PendingIntent createPendingIntent(Context context);

    @Override
    public int compareTo(@NonNull Payload payload) {
        return (payload.timestamp < timestamp) ? -1 : ((payload.timestamp == timestamp) ? 0 : 1);
    }

    public abstract CharSequence getFormattedCharSequence(Context context);

    public abstract CharSequence getRawCharSequence(Context context);

    public final CharSequence getFormattedTimestamp() {
        return DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }

    static String extractAsJsonObject(RemoteMessage message) {
        try {
            JSONObject obj = new JSONObject();
            final Map<String, String> data = message.getData();
            for (Map.Entry<String, String> entry : data.entrySet()) {
                try {
                    obj.put(entry.getKey(), new JSONObject(entry.getValue()));
                } catch (JSONException e1) {
                    try {
                        obj.put(entry.getKey(), new JSONArray(entry.getValue()));
                    } catch (JSONException e2) {
                        obj.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            return obj.toString();
        } catch (Exception e) {
            return extractCharSequence(message).toString();
        }
    }

    static CharSequence extractCharSequence(RemoteMessage message) {
        Truss truss = new Truss();
        final Map<String, String> data = message.getData();
        Set<String> strings = data.keySet();
        for (String key : strings) {
            String value = data.get(key);
            truss.pushSpan(new StyleSpan(Typeface.BOLD));
            truss.append(key).append(":\n");
            truss.popSpan();
            truss.append(value).append('\n');
        }
        return truss.build();
    }
}