package tech.codegarage.fcm.payload;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import tech.codegarage.fcm.R;
import tech.codegarage.fcm.activity.CopyToClipboardActivity;
import tech.codegarage.fcm.activity.FcmPayloadActivity;
import tech.codegarage.fcm.util.DetailType;
import tech.codegarage.fcm.util.Truss;

import static tech.codegarage.fcm.util.FcmUtil.INTENT_KEY_DETAIL_TYPE;
import static tech.codegarage.fcm.util.FcmUtil.INTENT_KEY_MESSAGE;
import static tech.codegarage.fcm.util.FcmUtil.copyToClipboard;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class TextPayload extends Payload {

    static final String KEY = "text";
    public final String text;
    private final String title;
    private final boolean clipboard;

    private TextPayload(RemoteMessage message, String title, String text, boolean clipboard) {
        super(message);
        this.title = title;
        this.text = text;
        this.clipboard = clipboard;
    }

    static TextPayload create(RemoteMessage message) throws JSONException {
        final String data = extractPayloadData(message, KEY);
        Log.d("FCMdata", "FCMdata(extractPayloadData): " + data);
        JSONObject json = new JSONObject(data);
        final String title = json.optString("title");
        final String text = json.optString("message");
        final boolean clipboard = json.optBoolean("clipboard");
        return new TextPayload(message, title, text, clipboard);
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
        final Intent intent = new Intent(context, CopyToClipboardActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        final Notification notification = getNotificationBuilder(context)
                .setSmallIcon(R.drawable.ic_chat_24dp)
                .setContentTitle(TextUtils.isEmpty(title) ? "Text" : title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .addAction(0, context.getString(R.string.payload_text_copy), PendingIntent.getActivity(context, 0, intent, 0))
//                .setAutoCancel(true)
                .build();
        showNotification(context, notification, String.valueOf(timestamp), R.id.text_notification_id);
    }

    @Override
    public void cancelNotification(Context context) {
        cancelNotification(context, String.valueOf(timestamp), R.id.text_notification_id);
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
                    .pushSpan(new StyleSpan(android.graphics.Typeface.BOLD)).append("title: ").popSpan().append(title).append('\n')
                    .pushSpan(new StyleSpan(android.graphics.Typeface.BOLD)).append("text: ").popSpan().append(text).append('\n')
                    .pushSpan(new StyleSpan(android.graphics.Typeface.BOLD)).append("clipboard: ").popSpan().append(String.valueOf(clipboard))
                    .build();
        }
        return display;
    }

    @Override
    public CharSequence getRawCharSequence(Context context) {
        if (displayRaw == null) {
            displayRaw = new Truss()
                    .pushSpan(new StyleSpan(android.graphics.Typeface.BOLD)).append(title).popSpan().append('\n')
                    .pushSpan(new StyleSpan(android.graphics.Typeface.ITALIC)).append(text)
                    .build();
        }
        return displayRaw;
    }

    @Override
    public void execute(Context context) {
        if (clipboard) {
            final Context app = context.getApplicationContext();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    copyToClipboard(app.getApplicationContext(), text);
                }
            });
        }
    }
}