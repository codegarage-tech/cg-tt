package tech.codegarage.fcm.payload;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
public class AppPayload extends Payload {

    static final String KEY = "app";
    private final String packageName;
    private final String title;

    private AppPayload(RemoteMessage message, String title, String packageName) {
        super(message);
        this.title = title;
        this.packageName = packageName;
    }

    static AppPayload create(RemoteMessage message) throws JSONException {
        final String data = extractPayloadData(message, KEY);
        Log.d("FCMdata","FCMdata(extractPayloadData): "+data);
        JSONObject json = new JSONObject(data);
        String title = json.optString("title");
        String packageName = json.optString("packageName");
        return new AppPayload(message, title, packageName);
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
        final Intent installIntent = getInstallIntent();
        final Intent removeIntent = getRemoveIntent();
        final Notification notification = getNotificationBuilder(context)
                .setSmallIcon(R.drawable.ic_shop_24dp)
                .setContentTitle(TextUtils.isEmpty(title) ? "App" : title)
                .setContentText(packageName)
                .addAction(0, context.getString(R.string.payload_app_install), PendingIntent.getActivity(context, 0, installIntent, 0))
                .addAction(0, context.getString(R.string.payload_app_remove), PendingIntent.getActivity(context, 0, removeIntent, 0))
//                .setAutoCancel(true)
                .build();
        showNotification(context, notification, String.valueOf(timestamp), R.id.app_notification_id);
    }

    public Intent getInstallIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public Intent getRemoveIntent() {
        Intent intent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    public void cancelNotification(Context context) {
        cancelNotification(context, String.valueOf(timestamp), R.id.app_notification_id);
    }

    @Override
    public PendingIntent createPendingIntent(Context context) {
        String data = extractAsJsonObject(message).toString();
        Log.d("FCMdata","FCMdata: "+data);
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
                    .pushSpan(new StyleSpan(android.graphics.Typeface.BOLD)).append("packageName: ").popSpan().append(packageName)
                    .build();
        }
        return display;
    }

    @Override
    public CharSequence getRawCharSequence(Context context) {
        if (displayRaw == null) {
            displayRaw = new Truss()
                    .pushSpan(new StyleSpan(android.graphics.Typeface.BOLD)).append(title).popSpan().append('\n')
                    .pushSpan(new StyleSpan(android.graphics.Typeface.ITALIC)).append(packageName)
                    .build();
        }
        return displayRaw;
    }

    @Override
    public void execute(Context context) {

    }
}