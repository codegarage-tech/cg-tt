package tech.codegarage.fcm.payload;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import tech.codegarage.fcm.R;
import tech.codegarage.fcm.activity.FcmPayloadActivity;
import tech.codegarage.fcm.util.DetailType;
import tech.codegarage.fcm.util.Truss;

import static tech.codegarage.fcm.util.FcmUtil.INTENT_KEY_DETAIL_TYPE;
import static tech.codegarage.fcm.util.FcmUtil.INTENT_KEY_MESSAGE;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class PingPayload extends Payload {

    static final String KEY = "ping";
    private final String title;

    private PingPayload(RemoteMessage message, String title) {
        super(message);
        this.title = title;
    }

    static PingPayload create(RemoteMessage message) throws JSONException {
        final String data = extractPayloadData(message, KEY);
        JSONObject json = new JSONObject(data);
        final String title = json.optString("title");
        return new PingPayload(message, title);
    }

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public void showNotification(Context context) {
        if (message == null) {
            return;
        }
        final Notification notification = getNotificationBuilder(context)
                .setSmallIcon(R.drawable.ic_notifications_none_24dp)
                .setContentTitle("Ping")
                .setAutoCancel(true)
                .build();
        showNotification(context, notification, R.id.ping_notification_id);
    }

    @Override
    public void cancelNotification(Context context) {
        cancelNotification(context, R.id.ping_notification_id);
    }

    @Override
    public PendingIntent createPendingIntent(Context context) {
        String data = extractAsJsonObject(message).toString();
        Log.d("FCMdata", "FCMdata: " + data);
        Intent intentPendingDetail = new Intent(context, FcmPayloadActivity.class);
        intentPendingDetail.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intentPendingDetail.putExtra(INTENT_KEY_MESSAGE, data);
        intentPendingDetail.putExtra(INTENT_KEY_DETAIL_TYPE, DetailType.FCM.name());
        return PendingIntent.getActivity(context, !TextUtils.isEmpty(data) ? data.hashCode() : (int) System.currentTimeMillis(), intentPendingDetail, 0);
    }

    @Override
    public CharSequence getFormattedCharSequence(Context context) {
        if (display == null) {
            display = new Truss()
                    .pushSpan(new StyleSpan(android.graphics.Typeface.BOLD)).append("title: ").popSpan().append(title)
                    .build();
        }
        return display;
    }

    @Override
    public CharSequence getRawCharSequence(Context context) {
        if (displayRaw == null) {
            displayRaw = new Truss()
                    .pushSpan(new StyleSpan(android.graphics.Typeface.BOLD)).append(title).popSpan().append('\n')
                    .build();
        }
        return displayRaw;
    }

    @Override
    public void execute(Context context) {

    }
}