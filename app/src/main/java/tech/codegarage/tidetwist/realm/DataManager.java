package tech.codegarage.tidetwist.realm;

import android.app.Activity;
import android.util.Log;

import tech.codegarage.tidetwist.model.VoiceSearchItem;
import tech.codegarage.tidetwist.util.AllConstants;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class DataManager {

    private static final String TAG = "DataManager";

    public static void storeVoiceSearchItem(Activity activity, VoiceSearchItem voiceSearchItem) {
        RealmManager mRealmManager = RealmManager.with(activity);
        mRealmManager.insertOrUpdate(voiceSearchItem);
//        Log.d(TAG, TAG + ">>> storeVoiceSearchItem: " + ((VoiceSearchItem) mRealmManager.getData(VoiceSearchItem.class, AllConstants.TABLE_KEY_VOICE_SEARCH_ITEM_ID, voiceSearchItem.getItemId())).toString());
    }
}
