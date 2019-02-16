package tech.codegarage.tidetwist.base;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import tech.codegarage.tidetwist.geocoding.SelfHealingLocationManager;
import tech.codegarage.tidetwist.util.Logger;
import tech.codegarage.tidetwist.util.RuntimePermissionManager;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public abstract class BaseLocationActivity extends BaseActivity {

    //Abstract declaration
    public abstract void onLocationFound(Location location);

    public abstract LOCATION_UPDATE_FREQUENCY initLocationUpdateFrequency();

    private SelfHealingLocationManager mSelfHealingLocationManager;
    private static final int REQUEST_CODE_LOCATION_SETTINGS_PERMISSION = 420;
    private static final int REQUEST_CODE_GOOGLE_PLAY_SERVICE = 410;
    private boolean isPaused = false;
    private LOCATION_UPDATE_FREQUENCY mLocationUpdateFrequency;

    private BaseUpdateListener mPermissionUpdate = new BaseUpdateListener() {
        @Override
        public void onUpdate(Object update) {
            if ((boolean) update) {
                //All runtime permissions are accepted and go further
                requestForLocationUpdate();
            }
        }
    };

    public enum LOCATION_UPDATE_FREQUENCY {ONCE, FREQUENTLY}

    public void setLocationUpdateFrequency(LOCATION_UPDATE_FREQUENCY locationUpdateFrequency) {
        this.mLocationUpdateFrequency = locationUpdateFrequency;
    }

    public SelfHealingLocationManager getSelfHealingLocationManager() {
        return mSelfHealingLocationManager;
    }

    public LOCATION_UPDATE_FREQUENCY getLocationUpdateFrequency() {
        if (mLocationUpdateFrequency == null) {
            mLocationUpdateFrequency = LOCATION_UPDATE_FREQUENCY.ONCE;
        }
        return mLocationUpdateFrequency;
    }

    public BaseLocationActivity() {
        super();
        this.mDefaultPermissions = new ArrayList<String>() {{
            //For internet
            add(Manifest.permission.INTERNET);
            add(Manifest.permission.ACCESS_NETWORK_STATE);
            //For image caching
            add(Manifest.permission.READ_EXTERNAL_STORAGE);
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            //For phone state
//            add(Manifest.permission.READ_PHONE_STATE);
            //For location update
            add(Manifest.permission.ACCESS_FINE_LOCATION);
            add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }};

        //Need to set update frequency first, cause depending on this frequency location update will be
        //started into the runtime permission update listener
        setLocationUpdateFrequency(initLocationUpdateFrequency());
        setRuntimePermissionUpdateListener(mPermissionUpdate);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void requestForLocationUpdate() {
        try {
            //Initialized self healing location manager
            if (mSelfHealingLocationManager == null) {
                mSelfHealingLocationManager = new SelfHealingLocationManager.HealingLocationBuilder(getActivity())
                        .setUpdateFrequency(getLocationUpdateFrequency().name())
                        .setLocationUpdateListener(new SelfHealingLocationManager.LocationUpdateListener() {
                            @Override
                            public void onSuccess(SelfHealingLocationManager.UPDATE_TYPE updateType, Location location) {
                                Logger.d(TAG, "SelfHealingLocationManager(onSuccess): " + "updateType: " + updateType.name() + " location: " + location.toString());
                                onLocationFound(location);
                            }

                            @Override
                            public void onFailure(String message) {
                                Logger.d(TAG, "SelfHealingLocationManager(onFailure): " + message);
                            }
                        })
                        .create();
            }

            //Request for location settings dialog
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mSelfHealingLocationManager.getLocationRequest());
            builder.addLocationRequest(mSelfHealingLocationManager.getLocationRequest());
            SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
            settingsClient.checkLocationSettings(builder.build())
                    .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            if (mSelfHealingLocationManager != null) {
                                mSelfHealingLocationManager.getSmartLocationUpdate();
                            }
                        }
                    })
                    .addOnFailureListener(getActivity(), new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        // Show the dialog by calling startResolutionForResult(), and check the
                                        // result in onActivityResult().
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult(getActivity(), REQUEST_CODE_LOCATION_SETTINGS_PERMISSION);
                                    } catch (Exception sie) {
                                        sie.printStackTrace();
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    Toast.makeText(getActivity(), "Setting change is not available.Try in another device.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isLocationUpdatable() {
        return (RuntimePermissionManager.getInstance().isAllPermissionsGranted(getActivity(), getPermissions()) && (isGpsEnabled(getActivity())));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mSelfHealingLocationManager != null) {
            mSelfHealingLocationManager.stopLocationUpdate();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isPaused) {
            Logger.d(TAG, "SelfHealingLocationManager: Activity is resumed");
            if (isLocationUpdatable()) {
                Logger.d(TAG, "SelfHealingLocationManager: Location is updatable");
                if (mSelfHealingLocationManager != null && !mSelfHealingLocationManager.hasStartedLocationRequest()) {
                    Logger.d(TAG, "SelfHealingLocationManager: Location request is not started yet or is stopped");
                    if (mLocationUpdateFrequency == LOCATION_UPDATE_FREQUENCY.FREQUENTLY) {
                        Logger.d(TAG, "SelfHealingLocationManager: Starting location update for frequently");
                        mSelfHealingLocationManager.resumeLocationUpdate();
                    } else if (mLocationUpdateFrequency == LOCATION_UPDATE_FREQUENCY.ONCE) {
                        if (!mSelfHealingLocationManager.hasReceivedLocationUpdate()) {
                            Logger.d(TAG, "SelfHealingLocationManager: Starting location update for once");
                            mSelfHealingLocationManager.resumeLocationUpdate();
                        } else {
                            Logger.d(TAG, "SelfHealingLocationManager: Already got location update for once");
                        }
                    } else {
                        Logger.d(TAG, "SelfHealingLocationManager: Nothing to do here");
                    }
                } else {
                    Logger.d(TAG, "SelfHealingLocationManager: Location request is running");
                }
            } else {
                Logger.d(TAG, "SelfHealingLocationManager: Location is not updatable");
            }
        } else {
            Logger.d(TAG, "SelfHealingLocationManager: Activity is resumed but did not satisfy logic");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Logger.d(TAG, "SelfHealingLocationManager: Activity is paused");
        isPaused = true;
        if (mSelfHealingLocationManager != null && mSelfHealingLocationManager.hasStartedLocationRequest()) {
            Logger.d(TAG, "SelfHealingLocationManager: Location request is running");
            mSelfHealingLocationManager.stopLocationUpdate();
        } else {
            Logger.d(TAG, "SelfHealingLocationManager: Location request is not started yet or is stopped");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LOCATION_SETTINGS_PERMISSION) {
            switch (resultCode) {
                case Activity.RESULT_OK: {
                    // All required changes were successfully made
                    Logger.d(TAG, "SelfHealingLocationManager: Location is enabled by user!");
                    if (mSelfHealingLocationManager != null) {
                        mSelfHealingLocationManager.getSmartLocationUpdate();
                    }
                    break;
                }
                case Activity.RESULT_CANCELED: {
                    // The user was asked to change settings, but chose not to
                    Logger.d(TAG, "SelfHealingLocationManager: Location is not enabled, user cancelled.");
                    break;
                }
                default: {
                    break;
                }
            }
        } else if (requestCode == REQUEST_CODE_GOOGLE_PLAY_SERVICE) {
            //TODO: https://stackoverflow.com/questions/43648520/how-do-you-prompt-the-user-to-update-google-play-services
//            GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
//            int result = googleAPI.isGooglePlayServicesAvailable(getActivity());
//
//            switch (result) {
//                case ConnectionResult.SUCCESS:
//                    break;
//                case ConnectionResult.SERVICE_DISABLED:
//                    break;
//                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
//                    googleAPI.getErrorDialog(getActivity(), result, REQUEST_CODE_GOOGLE_PLAY_SERVICE).show();
//                    break;
//            }
        }
    }
}