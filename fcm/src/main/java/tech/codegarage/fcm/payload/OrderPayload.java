package tech.codegarage.fcm.payload;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import tech.codegarage.fcm.R;
import tech.codegarage.fcm.util.DetailType;
import tech.codegarage.fcm.util.FcmManager;
import tech.codegarage.fcm.util.FcmUtil;

import static tech.codegarage.fcm.util.FcmUtil.INTENT_KEY_DETAIL_TYPE;
import static tech.codegarage.fcm.util.FcmUtil.INTENT_KEY_MESSAGE;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class OrderPayload extends Payload {

    private static final String TAG = OrderPayload.class.getSimpleName();
    public static final String KEY = "order";
    public final String text;

    private OrderPayload(RemoteMessage message, String text) {
        super(message);
        this.text = text;
    }

    static OrderPayload create(RemoteMessage message) {
        final String text = extractAsJsonObject(message);
        return new OrderPayload(message, text);
    }

    private CharSequence getCharSequence() {
        try {
            return new JSONObject(text).toString(4);
        } catch (JSONException e) {
            return text;
        }
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
        CharSequence content = getCharSequence();
        String orderStatus = FcmUtil.getAnyKeyValueFromJson(content.toString(), context.getString(R.string.payload_text_order_message_key));
        Log.d(TAG, "content(json): " + content.toString());
        Log.d(TAG, "content(message): " + FcmUtil.getAnyKeyValueFromJson(content.toString(), context.getString(R.string.payload_text_order_message_key)));

        final Notification notification = getNotificationBuilder(context)
                .setSmallIcon(R.drawable.ic_order_30dp)
                .setContentTitle(context.getString(R.string.payload_text_order_status))
                .setContentText(orderStatus)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(orderStatus))
                .setAutoCancel(true)
                .build();
        showNotification(context, notification, String.valueOf(timestamp), R.id.raw_notification_id);
    }

    @Override
    public void cancelNotification(Context context) {
        cancelNotification(context, String.valueOf(timestamp), R.id.raw_notification_id);
    }

    @Override
    public PendingIntent createPendingIntent(Context context) {
        String data = getCharSequence().toString();
        Log.d("FCMdata", "FCMdata: " + data);
        Intent intentPendingDetail = new Intent(context, FcmManager.getOrderContentClass());
        intentPendingDetail.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intentPendingDetail.putExtra(INTENT_KEY_MESSAGE, data);
        intentPendingDetail.putExtra(INTENT_KEY_DETAIL_TYPE, DetailType.FCM.name());
        return PendingIntent.getActivity(context, !TextUtils.isEmpty(data) ? data.hashCode() : (int) System.currentTimeMillis(), intentPendingDetail, 0);
    }

    @Override
    public CharSequence getFormattedCharSequence(Context context) {
        if (display == null) {
            display = getCharSequence();
        }
        return display;
    }

    @Override
    public CharSequence getRawCharSequence(Context context) {
        if (displayRaw == null) {
            displayRaw = getCharSequence();
        }
        return displayRaw;
    }

    @Override
    public void execute(Context context) {
        //Send broadcast to activity
        String data = getCharSequence().toString();
        Log.d(TAG, "intent value: " + data);
        INTENT.putExtra(INTENT_KEY_MESSAGE, data);
        LocalBroadcastManager.getInstance(context).sendBroadcast(INTENT);
    }
}