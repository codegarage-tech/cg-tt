package tech.codegarage.tidetwist.geocoding;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.TextUtils;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.lang.ref.WeakReference;

import tech.codegarage.tidetwist.util.Logger;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class SelfHealingLocationManager {

    private final WeakReference<Activity> mWeakActivity;
    private Context mContext;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private LocationListener mGpsLocationListener, mNetworkLocationListener;
    private LOCATION_UPDATE_FREQUENCY mUpdateFrequency;
    private LocationUpdateListener mLocationUpdateListener;
    private boolean hasStartedLocationRequest, hasReceivedLocationUpdate;
    private boolean isReceivingGpsUpdate, isReceivingNetworkUpdate, isReceivingFusedLocationUpdate;
    private LocationManager mLocationManager;
    private long mRequestTimeout, mFastestInterval, mCounterInterval, mUpdateInterval;
    private int mMinimumDistance;
    private CountDownTimer mGPSCountdownTimer, mNetworkCountdownTimer;
    private UPDATE_TYPE lastUpdateType = UPDATE_TYPE.NONE;
    private String TAG = "SelfHealingLocationManager";

    public enum LOCATION_UPDATE_FREQUENCY {ONCE, FREQUENTLY}

    public enum UPDATE_TYPE {NONE, LAST_KNOWN_PASSIVE, LAST_KNOWN_GPS, LAST_KNOWN_NETWORK, LAST_KNOWN_FUSED_LOCATION_API, LISTENER_FUSED_LOCATION_API_UPDATE, LISTENER_GPS_UPDATE, LISTENER_NETWORK_UPDATE}

    public interface LocationUpdateListener {
        public void onSuccess(UPDATE_TYPE updateType, Location location);

        public void onFailure(String message);
    }

    public interface CountdownTimerUpdateListener {
        public void onUpdate(boolean isUpdated);
    }

    public SelfHealingLocationManager(HealingLocationBuilder healingLocationBuilder) {
        this.mWeakActivity = new WeakReference<>(healingLocationBuilder.getActivity());
        this.mContext = healingLocationBuilder.getActivity().getApplicationContext();
        this.mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        this.hasStartedLocationRequest = false;
        this.hasReceivedLocationUpdate = false;
        this.isReceivingGpsUpdate = false;
        this.isReceivingNetworkUpdate = false;
        this.isReceivingFusedLocationUpdate = false;
        this.mRequestTimeout = healingLocationBuilder.requestTimeout;
        this.mFastestInterval = healingLocationBuilder.fastestInterval;
        this.mMinimumDistance = healingLocationBuilder.minimumDistance;
        this.mCounterInterval = healingLocationBuilder.counterInterval;
        this.mUpdateInterval = healingLocationBuilder.updateInterval;
        this.mUpdateFrequency = healingLocationBuilder.getUpdateFrequency();
        this.mLocationUpdateListener = healingLocationBuilder.getLocationUpdateListener();
        this.mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        this.mLocationRequest = createLocationRequest(mUpdateInterval, mFastestInterval, healingLocationBuilder.getPriority());
    }

    public LocationRequest createLocationRequest(long updateInterval, long fastestInterval, int priority) {
        if (mLocationRequest == null) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(updateInterval);
            mLocationRequest.setFastestInterval(fastestInterval);
            mLocationRequest.setPriority(priority);
        }
        return mLocationRequest;
    }

    public LocationRequest getLocationRequest() {
        return mLocationRequest;
    }

    public boolean hasReceivedLocationUpdate() {
        return hasReceivedLocationUpdate;
    }

    public boolean hasStartedLocationRequest() {
        return hasStartedLocationRequest;
    }

    public void resumeLocationUpdate() {
        getSmartLocationUpdate();
    }

    public void getSmartLocationUpdate() {
        hasStartedLocationRequest = true;
        getLastKnownLocation();
        getCurrentLocationUpdate();
    }

    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        if (mLocationUpdateListener == null) {
            try {
                throw new Exception("Location update listener is not found");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        final Activity activity = mWeakActivity.get();
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            mLocationUpdateListener.onFailure("Activity reference is not found in last known location");
            return;
        }

        try {
            //Check passive last known location
            Location lastKnownPassiveLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (lastKnownPassiveLocation != null) {
                updateLocation(UPDATE_TYPE.LAST_KNOWN_PASSIVE, lastKnownPassiveLocation);
                return;
            } else {
                mLocationUpdateListener.onFailure("Passive last known location is not found");
            }

            //Check GPS last known location
            Location lastKnownGpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownGpsLocation != null) {
                updateLocation(UPDATE_TYPE.LAST_KNOWN_GPS, lastKnownGpsLocation);
                return;
            } else {
                mLocationUpdateListener.onFailure("GPS last known location is not found");
            }

            //Check Network last known location
            Location lastKnownNetworkLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastKnownNetworkLocation != null) {
                updateLocation(UPDATE_TYPE.LAST_KNOWN_NETWORK, lastKnownNetworkLocation);
                return;
            } else {
                mLocationUpdateListener.onFailure("Network last known location is not found");
            }

            //Check FusedLocationProviderClient last known location
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                updateLocation(UPDATE_TYPE.LAST_KNOWN_FUSED_LOCATION_API, location);
                            } else {
                                mLocationUpdateListener.onFailure("Fused Last known location is not found");
                            }
                        }
                    });
        } catch (Exception ex) {
            mLocationUpdateListener.onFailure(ex.getMessage());
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocationUpdate() {
        //Skip if there is no listener for updating data
        if (mLocationUpdateListener == null) {
            try {
                throw new Exception("Location update listener is not found");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        //Skip if already running location update service
        if (isReceivingUpdate()) {
            mLocationUpdateListener.onFailure("Device is already receiving location updates");
            return;
        }

        //Skip if there is no found any reference context
        final Activity activity = mWeakActivity.get();
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            mLocationUpdateListener.onFailure("Activity reference is not found in last known location");
            return;
        }

        //Request depending on valid internet connection
        if (!isConnectedToNetwork(activity)) {
            //Start offline update
            startOfflineLocationUpdate();
        } else {
            //If there is internet connection, we can easily execute fused location provider client
            startFusedLocationUpdate();
        }
    }

    private void startOfflineLocationUpdate() {
        try {
            Logger.d(TAG, "SelfHealingLocationManager: Starting offline data update");
            if (isGpsEnabled(mContext)) {
                Logger.d(TAG, "SelfHealingLocationManager: Started checking gps location update");
                startGpsLocationUpdate(new CountdownTimerUpdateListener() {
                    @Override
                    public void onUpdate(boolean isUpdated) {
                        if (isUpdated) {
                            if (!isReceivingGpsUpdate) {
                                Logger.d(TAG, "SelfHealingLocationManager: receiving gps update " + isReceivingGpsUpdate);
                                startNetworkLocationUpdate(new CountdownTimerUpdateListener() {
                                    @Override
                                    public void onUpdate(boolean isUpdated) {
                                        if (isUpdated) {

                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            } else if (isNetworkEnabled()) {
                Logger.d(TAG, "SelfHealingLocationManager: Started checking network location update");
                startNetworkLocationUpdate(new CountdownTimerUpdateListener() {
                    @Override
                    public void onUpdate(boolean isUpdated) {
                        if (isUpdated) {

                        }
                    }
                });
            } else {
                mLocationUpdateListener.onFailure("No way to get location update in offline mode");
            }
        } catch (Exception ex) {
            mLocationUpdateListener.onFailure(ex.getMessage());
        }
    }

    @SuppressLint("MissingPermission")
    private void startFusedLocationUpdate() {
        try {
            if (mLocationCallback == null) {
                Logger.d(TAG, "Location callback is null");
                mLocationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            Logger.d(TAG, "Found fused location result");
                            if (locationResult.getLastLocation() != null) {
                                updateLocation(UPDATE_TYPE.LISTENER_FUSED_LOCATION_API_UPDATE, locationResult.getLastLocation());
                            } else {
                                mLocationUpdateListener.onFailure("Fused location is not found");
                            }
                        } else {
                            mLocationUpdateListener.onFailure("Location result is not found");
                        }
                    }
                };
            }

            if (isConnectedToNetwork(mContext)) {
                Logger.d(TAG, "Starting fused location update");
                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            }
        } catch (Exception ex) {
            mLocationUpdateListener.onFailure(ex.getMessage());
        }
    }

    @SuppressLint("MissingPermission")
    private void startGpsLocationUpdate(final CountdownTimerUpdateListener countdownTimerUpdateListener) {
        try {
            destroyGpsCountdownTimer();
            mGPSCountdownTimer = new CountDownTimer(mRequestTimeout, mCounterInterval) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Logger.d(TAG, "Waiting gps counter: " + (mRequestTimeout - millisUntilFinished) / 1000);
                }

                @Override
                public void onFinish() {
                    Logger.d(TAG, "SelfHealingLocationManager: finished gps count down timer");
                    countdownTimerUpdateListener.onUpdate(true);
                }
            };
            if (mGpsLocationListener == null) {
                mGpsLocationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Logger.d(TAG, "Found gps location result");
                        if (location != null) {
                            updateLocation(UPDATE_TYPE.LISTENER_GPS_UPDATE, location);
                        } else {
                            mLocationUpdateListener.onFailure("GPS listener found null location");
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
            }

            mGPSCountdownTimer.start();
            Logger.d(TAG, "SelfHealingLocationManager: started gps count down timer");
            if (isGpsEnabled(mContext)) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, mUpdateInterval, mMinimumDistance, mGpsLocationListener);
                Logger.d(TAG, "SelfHealingLocationManager: started gps location update");
            }
        } catch (Exception ex) {
            mLocationUpdateListener.onFailure(ex.getMessage());
        }
    }

    @SuppressLint("MissingPermission")
    private void startNetworkLocationUpdate(final CountdownTimerUpdateListener countdownTimerUpdateListener) {
        try {
            destroyNetworkCountdownTimer();
            mNetworkCountdownTimer = new CountDownTimer(mRequestTimeout, mCounterInterval) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Logger.d(TAG, "Waiting network counter: " + (mRequestTimeout - millisUntilFinished) / 1000);
                }

                @Override
                public void onFinish() {
                    Logger.d(TAG, "SelfHealingLocationManager: finished network count down timer");
                    countdownTimerUpdateListener.onUpdate(true);
                }
            };
            if (mNetworkLocationListener == null) {
                mNetworkLocationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Logger.d(TAG, "Found network location result");
                        if (location != null) {
                            updateLocation(UPDATE_TYPE.LISTENER_NETWORK_UPDATE, location);
                        } else {
                            mLocationUpdateListener.onFailure("Network listener found null location");
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
            }

            mNetworkCountdownTimer.start();
            Logger.d(TAG, "SelfHealingLocationManager: started network count down timer");
            if (isNetworkEnabled()) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mUpdateInterval, mMinimumDistance, mNetworkLocationListener);
                Logger.d(TAG, "SelfHealingLocationManager: started network location update");
            }
        } catch (Exception ex) {
            mLocationUpdateListener.onFailure(ex.getMessage());
        }
    }

    public boolean isGpsEnabled(Context context) {
//        if (mLocationManager == null) {
//            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
//        }
//        boolean isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        Logger.d(TAG, "SelfHealingLocationManager: gps enable " + isGpsEnabled);
//        return isGpsEnabled;
        //Above logic is not working, but below logic is working

        boolean isGpsEnabled = false;
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException ex) {
                ex.printStackTrace();
            }
            isGpsEnabled = locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            isGpsEnabled = !TextUtils.isEmpty(locationProviders);
        }
        Logger.d(TAG, "SelfHealingLocationManager: gps enable " + isGpsEnabled);
        return isGpsEnabled;
    }

    public boolean isNetworkEnabled() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        }
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Logger.d(TAG, "SelfHealingLocationManager: network enable " + isNetworkEnabled);
        return isNetworkEnabled;
    }

    public boolean isReceivingUpdate() {
        return (isReceivingGpsUpdate || isReceivingNetworkUpdate || isReceivingFusedLocationUpdate);
    }

    private void stopGpsLocationUpdate() {
        if (mGpsLocationListener != null) {
            mLocationManager.removeUpdates(mGpsLocationListener);
            isReceivingGpsUpdate = false;
            Logger.d(TAG, "SelfHealingLocationManager: stopped gps location update");
        } else {
            Logger.d(TAG, "SelfHealingLocationManager: gps location update listener is null");
        }
    }

    private void stopNetworkLocationUpdate() {
        if (mNetworkLocationListener != null) {
            mLocationManager.removeUpdates(mNetworkLocationListener);
            isReceivingNetworkUpdate = false;
            Logger.d(TAG, "SelfHealingLocationManager: stopped network location update");
        } else {
            Logger.d(TAG, "SelfHealingLocationManager: network location update listener is null");
        }
    }

    private void stopFusedLocationUpdate() {
        if (mLocationCallback != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            isReceivingFusedLocationUpdate = false;
            Logger.d(TAG, "SelfHealingLocationManager: stopped fused location update");
        } else {
            Logger.d(TAG, "SelfHealingLocationManager: fused location update listener is null");
        }
    }

    public void stopLocationUpdate() {
        stopGpsLocationUpdate();
        stopNetworkLocationUpdate();
        stopFusedLocationUpdate();

        destroyGpsCountdownTimer();
        destroyNetworkCountdownTimer();
        hasStartedLocationRequest = false;
    }

    private synchronized void updateLocation(UPDATE_TYPE updateType, Location location) {
        if (location != null) {
            hasReceivedLocationUpdate = true;
            lastUpdateType = updateType;
            mLocationUpdateListener.onSuccess(lastUpdateType, location);

            switch (lastUpdateType) {
                case LISTENER_GPS_UPDATE:
                    isReceivingGpsUpdate = true;
                    break;
                case LISTENER_NETWORK_UPDATE:
                    isReceivingNetworkUpdate = true;
                    break;
                case LISTENER_FUSED_LOCATION_API_UPDATE:
                    isReceivingFusedLocationUpdate = true;
                    break;
            }
        }

        if (mUpdateFrequency == LOCATION_UPDATE_FREQUENCY.ONCE) {
            Logger.d(TAG, "SelfHealingLocationManager: update frequency is one, that's why destroying all location update");
            stopLocationUpdate();
        } else {
            Logger.d(TAG, "SelfHealingLocationManager: update frequency is frequently");
            switch (lastUpdateType) {
                case LISTENER_GPS_UPDATE:
                    if (!isConnectedToNetwork(mContext)) {
                        Logger.d(TAG, "SelfHealingLocationManager: keeping only gps location update frequency");
                        stopNetworkLocationUpdate();
                        stopFusedLocationUpdate();
                        destroyNetworkCountdownTimer();
                    } else {
                        Logger.d(TAG, "SelfHealingLocationManager: ==========started self recovering in gps update==========");
                        stopLocationUpdate();
                        startFusedLocationUpdate();
                    }
                    break;
                case LISTENER_NETWORK_UPDATE:
                    if (!isConnectedToNetwork(mContext)) {
                        Logger.d(TAG, "SelfHealingLocationManager: keeping only network location update frequency");
                        stopGpsLocationUpdate();
                        stopFusedLocationUpdate();
                        destroyGpsCountdownTimer();
                    } else {
                        Logger.d(TAG, "SelfHealingLocationManager: ==========started self recovering in network update==========");
                        stopLocationUpdate();
                        startFusedLocationUpdate();
                    }
                    break;
                case LISTENER_FUSED_LOCATION_API_UPDATE:
                    if (isConnectedToNetwork(mContext)) {
                        Logger.d(TAG, "SelfHealingLocationManager: keeping only fused location update frequency");
                        stopGpsLocationUpdate();
                        stopNetworkLocationUpdate();
                        destroyGpsCountdownTimer();
                        destroyNetworkCountdownTimer();
                    } else {
                        Logger.d(TAG, "SelfHealingLocationManager: ==========started self recovering in fused location update==========");
                        stopLocationUpdate();
                        startOfflineLocationUpdate();
                    }
                    break;
            }
        }
    }

    private void destroyGpsCountdownTimer() {
        if (mGPSCountdownTimer != null) {
            mGPSCountdownTimer.cancel();
            Logger.d(TAG, "SelfHealingLocationManager: destroyed gps count down timer");
        } else {
            Logger.d(TAG, "SelfHealingLocationManager: gps count down timer is null");
        }
    }

    private void destroyNetworkCountdownTimer() {
        if (mNetworkCountdownTimer != null) {
            mNetworkCountdownTimer.cancel();
            Logger.d(TAG, "SelfHealingLocationManager: destroyed network count down timer");
        } else {
            Logger.d(TAG, "SelfHealingLocationManager: network count down timer is null");
        }
    }

    private boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        boolean isConnectedToNetwork = info != null && info.isConnected();
        Logger.d(TAG, "SelfHealingLocationManager: Internet connection is " + isConnectedToNetwork);
        return isConnectedToNetwork;
    }

    public int isGooglePlayServiceAvailable(Context context) {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        return googleAPI.isGooglePlayServicesAvailable(context);
    }

    public static class HealingLocationBuilder {

        private Activity activity;
        private LOCATION_UPDATE_FREQUENCY updateFrequency;
        private LocationUpdateListener locationUpdateListener;
        private long counterInterval;
        private long updateInterval;
        private long fastestInterval;
        private long requestTimeout;
        private int minimumDistance;
        private int priority;

        public HealingLocationBuilder(Activity activity) {
            this.activity = activity;
            this.updateFrequency = LOCATION_UPDATE_FREQUENCY.ONCE;
            this.counterInterval = 1000;
            this.updateInterval = 30000;
            this.fastestInterval = 10000;
            this.requestTimeout = 15000;
            this.minimumDistance = 0;
            this.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        }

        public Activity getActivity() {
            return activity;
        }

        public HealingLocationBuilder setUpdateFrequency(String updateFrequency) {
            this.updateFrequency = LOCATION_UPDATE_FREQUENCY.valueOf(updateFrequency);
            return this;
        }

        public LOCATION_UPDATE_FREQUENCY getUpdateFrequency() {
            return updateFrequency;
        }

        public HealingLocationBuilder setLocationUpdateListener(LocationUpdateListener locationUpdateListener) {
            this.locationUpdateListener = locationUpdateListener;
            return this;
        }

        public LocationUpdateListener getLocationUpdateListener() {
            return locationUpdateListener;
        }

        public long getRequestTimeout() {
            return requestTimeout;
        }

        public HealingLocationBuilder setRequestTimeout(long requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }

        public int getMinimumDistance() {
            return minimumDistance;
        }

        public HealingLocationBuilder setMinimumDistance(int minimumDistance) {
            this.minimumDistance = minimumDistance;
            return this;
        }

        public HealingLocationBuilder setCounterInterval(long counterInterval) {
            this.counterInterval = counterInterval;
            return this;
        }

        public long getCounterInterval() {
            return counterInterval;
        }

        public long getUpdateInterval() {
            return updateInterval;
        }

        public HealingLocationBuilder setUpdateInterval(long updateInterval) {
            this.updateInterval = updateInterval;
            return this;
        }

        public HealingLocationBuilder setFastestInterval(long fastestInterval) {
            this.fastestInterval = fastestInterval;
            return this;
        }

        public long getFastestInterval() {
            return fastestInterval;
        }

        public HealingLocationBuilder setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public int getPriority() {
            return priority;
        }

        public SelfHealingLocationManager create() {
            if (this.activity == null) {
                try {
                    throw new Exception("Activity context needs to be passed in");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (this.locationUpdateListener == null) {
                try {
                    throw new Exception("No callback listener is provided, do you expect updates?");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return new SelfHealingLocationManager(this);
        }
    }
}