package tech.codegarage.fcm.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

import tech.codegarage.fcm.R;
import tech.codegarage.fcm.util.Truss;

import static tech.codegarage.fcm.util.FcmUtil.INTENT_KEY_MESSAGE;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class FcmPayloadActivity extends Activity {

//    public static final String EXTRA_MESSAGE = "extra.message";
//
//    public static Intent createIntent(@NonNull Context context, @NonNull RemoteMessage message) {
//        Intent i = new Intent(context, FcmPayloadActivity.class);
//        i.putExtra(EXTRA_MESSAGE, message);
//        return i;
//    }
//
//    public static PendingIntent createPendingIntent(@NonNull Context context, @NonNull RemoteMessage message) {
//        String messageId = message.getMessageId();
//        return PendingIntent.getActivity(context, messageId != null ? messageId.hashCode() : (int) System.currentTimeMillis(), FcmPayloadActivity.createIntent(context, message), 0);
//    }

    private static CharSequence buildMessage(@Nullable RemoteMessage message) {
        if (message == null) {
            return null;
        }

        Truss truss = new Truss();
        final Map<String, String> data = message.getData();
        final String messageId = message.getMessageId();
        if (messageId != null) {
            truss.pushSpan(new StyleSpan(Typeface.BOLD)).append("Id: ").popSpan().append(messageId).append('\n');
        }
        final String messageType = message.getMessageType();
        if (messageType != null) {
            truss.pushSpan(new StyleSpan(Typeface.BOLD)).append("Type: ").popSpan().append(messageType).append('\n');
        }
        final String from = message.getFrom();
        if (from != null) {
            truss.pushSpan(new StyleSpan(Typeface.BOLD)).append("From: ").popSpan().append(from).append('\n');
        }
        final String to = message.getTo();
        if (to != null) {
            truss.pushSpan(new StyleSpan(Typeface.BOLD)).append("To: ").popSpan().append(to).append('\n');
        }
        truss.pushSpan(new StyleSpan(Typeface.BOLD)).append("Time: ").popSpan().append(String.valueOf(message.getSentTime())).append('\n');
        truss.pushSpan(new StyleSpan(Typeface.BOLD)).append("Ttl: ").popSpan().append(message.getTtl()).append('\n');

        Set<String> strings = data.keySet();
        for (String key : strings) {
            truss.append('\n');
            String value = data.get(key);
            truss.pushSpan(new StyleSpan(Typeface.BOLD));
            truss.append(key).append(":\n");
            truss.popSpan();
            try {
                final JSONObject json = new JSONObject(value);
                truss.append(json.toString(2));
            } catch (JSONException e) {
                truss.append(value);
            }
        }

        return truss.build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcm_message);
//        ((TextView) findViewById(R.id.fcm_message_value)).setText(buildMessage((RemoteMessage) getIntent().getParcelableExtra(EXTRA_MESSAGE)));
        String messageData = getIntent().getStringExtra(INTENT_KEY_MESSAGE);
        Log.d("FCMdata","FCMdata: "+messageData);
        ((TextView) findViewById(R.id.fcm_message_value)).setText(messageData);
    }
}