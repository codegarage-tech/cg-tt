package tech.codegarage.fcm.payload;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
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
public class LinkPayload extends Payload {

    static final String KEY = "link";
    private final String url;
    private final String title;
    private final boolean open;

    private LinkPayload(RemoteMessage message, String title, String url, boolean open) {
        super(message);
        this.title = title;
        this.url = url;
        this.open = open;
    }

    static LinkPayload create(RemoteMessage message) throws JSONException {
        final String data = extractPayloadData(message, KEY);
        JSONObject json = new JSONObject(data);
        String title = json.optString("title");
        String url = json.optString("url");
        url = TextUtils.isEmpty(url) || url.contains("://") ? url : ("http://" + url);
        boolean force = json.optBoolean("open");
        return new LinkPayload(message, title, url, force);
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
        final Intent intent = getIntent();
        final NotificationCompat.Builder builder = getNotificationBuilder(context)
                .setSmallIcon(R.drawable.ic_link_24dp)
                .setContentTitle(TextUtils.isEmpty(title) ? "Link" : title)
                .setContentText(url);
        if (!TextUtils.isEmpty(url)) {
            builder.addAction(0, context.getString(R.string.payload_link_open), PendingIntent.getActivity(context, 0, intent, 0));
        }
//        builder.setAutoCancel(true);
        showNotification(context, builder.build(), String.valueOf(timestamp), R.id.link_notification_id);
    }

    public Intent getIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    public void cancelNotification(Context context) {
        cancelNotification(context, String.valueOf(timestamp), R.id.link_notification_id);
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
                    .pushSpan(new StyleSpan(android.graphics.Typeface.BOLD)).append("url: ").popSpan().append(url).append('\n')
                    .pushSpan(new StyleSpan(android.graphics.Typeface.BOLD)).append("open: ").popSpan().append(String.valueOf(open))
                    .build();
        }
        return display;
    }

    @Override
    public CharSequence getRawCharSequence(Context context) {
        if (displayRaw == null) {
            displayRaw = new Truss()
                    .pushSpan(new StyleSpan(android.graphics.Typeface.BOLD)).append(title).popSpan().append('\n')
                    .pushSpan(new StyleSpan(android.graphics.Typeface.ITALIC)).append(url)
                    .build();
        }
        return displayRaw;
    }

    @Override
    public void execute(Context context) {
        if (open) {
            try {
                context.getApplicationContext().startActivity(getIntent());
            } catch (Exception ignore) {

            }
        }
    }
}
