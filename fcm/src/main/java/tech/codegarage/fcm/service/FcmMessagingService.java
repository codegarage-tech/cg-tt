package tech.codegarage.fcm.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import tech.codegarage.fcm.payload.OrderPayload;
import tech.codegarage.fcm.payload.Payload;
import tech.codegarage.fcm.util.FcmUtil;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class FcmMessagingService extends FirebaseMessagingService {

    private static final String TAG = FcmMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //Check fcm is registered or not.
        // Device is only registered when the user is at logged in state
        if (FcmUtil.getIsFcmRegistered(this) == 1) {
            Log.d(TAG, TAG + ">> " + "FCM is registered");
            Log.d(TAG, TAG + ">> " + remoteMessage.getData().toString());
            Payload payload = Payload.with(remoteMessage);

            //Save notification into preference
            if (!payload.key().equalsIgnoreCase(OrderPayload.KEY)) {
                Log.d(TAG, TAG + ">> " + "Saving app notification");
                payload.saveToSharedPreferences(this);
            } else {
                Log.d(TAG, TAG + " >> " + "Avoid saving order notification");
            }

            //Show app notification
            if (!payload.key().equalsIgnoreCase(OrderPayload.KEY) && (FcmUtil.getIsAppNotification(this) == 1) && payload.shouldShowNotification()) {
                payload.showNotification(this);
            } else {
                Log.d(TAG, TAG + " >> " + "Avoid showing app notification");
            }

            //Show order notification
            if (payload.key().equalsIgnoreCase(OrderPayload.KEY) && (FcmUtil.getIsOrderNotification(this) == 1) && payload.shouldShowNotification()) {
                Log.d(TAG, TAG + " >> " + "Showing order notification");
                payload.showNotification(this);
            } else {
                Log.d(TAG, TAG + " >> " + "Avoid showing order notification");
            }
            payload.execute(this);
        } else {
            Log.d(TAG, TAG + ">> " + "FCM is not registered");
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        if (!FcmUtil.isNullOrEmpty(token)) {
            Log.d(TAG, TAG + " >> " + "onNewToken: " + token);
            FcmUtil.saveToken(getApplicationContext(), token);
        }
    }
}