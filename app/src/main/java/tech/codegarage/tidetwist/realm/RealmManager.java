package tech.codegarage.tidetwist.realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class RealmManager {

    private static final String REALM_NAME = "tidetwist.realm";
    private static final long REALM_SCHEMA_VERSION = 1;

    private static RealmManager mInstance;
    private final Realm mRealm;
    private static final String TAG = RealmManager.class.getSimpleName();
    private OnRealmDataChangeListener onRealmDataChangeListener = null;

    public interface OnRealmDataChangeListener<T extends RealmObject> {
        public void onInsert(T realmModel);

        public void onUpdate(T realmModel);

        public void onDelete(T realmModel);
    }

//    private RealmManager() {
//        mRealm = Realm.getDefaultInstance();
//    }

//    public static RealmManager getInstance() {
//        if (mInstance == null) {
//            mInstance = new RealmManager();
//        }
//        return mInstance;
//    }

    private RealmManager(Application application) {
        //Initialize Realm Database
        Realm.init(application.getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(REALM_NAME)
                .schemaVersion(REALM_SCHEMA_VERSION)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        mRealm = Realm.getDefaultInstance();
    }

    public static RealmManager with(Fragment fragment) {
        if (mInstance == null) {
            mInstance = new RealmManager(fragment.getActivity().getApplication());
        }
        return mInstance;
    }

    public static RealmManager with(Activity activity) {
        if (mInstance == null) {
            mInstance = new RealmManager(activity.getApplication());
        }
        return mInstance;
    }

    public static RealmManager with(Application application) {
        if (mInstance == null) {
            mInstance = new RealmManager(application);
        }
        return mInstance;
    }

    public RealmManager setOnRealmDataChangeListener(OnRealmDataChangeListener onRealmDataChangeListener) {
        this.onRealmDataChangeListener = onRealmDataChangeListener;
        return mInstance;
    }

    public Realm getRealm() {
        return mRealm;
    }

    public void refresh() {
        mRealm.refresh();
    }

    public void destroyRealm() {
        if (!mRealm.isClosed()) {
            mRealm.close();
        }
    }

    /**************************
     * Generic helper methods *
     **************************/
    public <E extends RealmObject> void insertOrUpdate(E dbObject) {
        try {
            if (!mRealm.isInTransaction())
                mRealm.beginTransaction();

            mRealm.insertOrUpdate(dbObject);
            mRealm.commitTransaction();
        } catch (Exception e) {
            Log.d(TAG, "Failed to insertOrUpdate: " + e.getMessage());
        }
    }

    public <E extends RealmObject> void insertIfDataIsNotExist(Class<E> modelClass, E dbObject, String fieldName, String value) {
        try {
            if (!isDataExist(modelClass, fieldName, value)) {
                insertOrUpdate(dbObject);
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to insertOrUpdate: " + e.getMessage());
        }
    }

    public <E extends RealmObject> RealmObject getData(Class<E> modelClass, String fieldName, String value) {
        try {
            return mRealm.where(modelClass).equalTo(fieldName, value).findFirst();
        } catch (Exception e) {
            Log.d(TAG, "Failed to getData: " + e.getMessage());
        }
        return null;
    }

    public <E extends RealmObject> RealmResults<E> getAllResultData(Class<E> modelClass) {
        try {
            return mRealm.where(modelClass).findAll();
        } catch (Exception e) {
            Log.d(TAG, "Failed to getAllData: " + e.getMessage());
        }
        return null;
    }

    public <E extends RealmObject> List<E> getAllListData(Class<E> modelClass) {
        try {
            return mRealm.copyFromRealm(mRealm.where(modelClass).findAll());
        } catch (Exception e) {
            Log.d(TAG, "Failed to getAllData: " + e.getMessage());
        }
        return null;
    }

    public <E extends RealmObject> void deleteData(Class<E> modelClass, String fieldName, String value) {
        try {
            if (!mRealm.isInTransaction())
                mRealm.beginTransaction();

            mRealm.where(modelClass).equalTo(fieldName, value).findFirst().deleteFromRealm();
            mRealm.commitTransaction();
        } catch (Exception e) {
            Log.d(TAG, "Failed to deleteData: " + e.getMessage());
        }
    }

    public <E extends RealmObject> void deleteAllData(Class<E> modelClass) {
        try {
            if (!mRealm.isInTransaction())
                mRealm.beginTransaction();

            mRealm.delete(modelClass);
            mRealm.commitTransaction();
        } catch (Exception e) {
            Log.d(TAG, "Failed to deleteData: " + e.getMessage());
        }
    }

    public <E extends RealmObject> boolean isDataExist(Class<E> modelClass, String fieldName, String value) {
        try {
            if (mRealm.where(modelClass).equalTo(fieldName, value).findFirst() != null) {
                return true;
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to isDataExist: " + e.getMessage());
        }
        return false;
    }

    public <E extends RealmObject> boolean hasData(Class<E> modelClass) {
        try {
            return mRealm.where(modelClass).findAll().size() > 0;
        } catch (Exception e) {
            Log.d(TAG, "Failed to hasData: " + e.getMessage());
        }
        return false;
    }

//    public boolean setCard(StripeCard stripeCard) {
//        if (!isCardExist(stripeCard)) {
//            Log.d(TAG, stripeCard.getCardName() + " is not exist.");
//            getRealm().beginTransaction();
//            getRealm().copyToRealm(stripeCard);
//            getRealm().commitTransaction();
//        }
//
//        if (isCardExist(stripeCard)) {
//            Log.d(TAG, "Card exist: " + stripeCard.toString());
//
//            if (onRealmDataChangeListener != null) {
//                onRealmDataChangeListener.onInsert(stripeCard);
//                Log.d(TAG, "Card is listening: " + stripeCard.toString());
//            }
//
//            //Save card into session
//            SessionManager.setStringSetting(FoodSignalApplication.getGlobalContext(), SESSION_SELECTED_CARD, StripeCard.getResponseString(stripeCard));
//
//            return true;
//        }
//
//        return false;
//    }
}