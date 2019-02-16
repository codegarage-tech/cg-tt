//package tech.codegarage.tidetwist.base;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentSender;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.PendingResult;
//import com.google.android.gms.common.api.ResultCallback;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.LocationSettingsRequest;
//import com.google.android.gms.location.LocationSettingsResult;
//import com.google.android.gms.location.LocationSettingsStatusCodes;
//import tech.codegarage.tidetwist.R;
//import tech.codegarage.tidetwist.util.RuntimePermissionManager;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;
//import static tech.codegarage.tidetwist.util.RuntimePermissionManager.REQUEST_CODE_PERMISSION;
//
///**
// * @author Md. Rashadul Alam
// * Email: rashed.droid@gmail.com
// */
//public abstract class BaseLocationActivity extends AppCompatActivity {
//
//    //Location variable
//    public GoogleApiClient mGoogleApiClient;
//    public LocationRequest mLocationRequest;
//    public LocationSettingsRequest mLocationSettingsRequest;
//    private Location mCurrentLocation;
//    public LocationListener mLocationListener;
//
//    public abstract void onLocationFound(Location location);
//
//    public static final long UPDATE_INTERVAL_LOCATION_IN_MILLISECONDS = 10 * 1000;
//    public static final long UPDATE_INTERVAL_FASTEST_IN_MILLISECONDS = UPDATE_INTERVAL_LOCATION_IN_MILLISECONDS / 2;
//    public static int GPS_ACCURACY = PRIORITY_HIGH_ACCURACY;
//    public static final int REQUEST_LOCATION_CHECK_SETTINGS = 0x1;
//
//    private BaseLocationActivity mActivity;
//    public Bundle mSavedInstanceState;
//    public String TAG = BaseLocationActivity.class.getSimpleName();
//    public ProgressDialog mProgressDialog;
//
//    //Abstract declaration
//    public abstract int initActivityLayout();
//
//    public abstract void initStatusBarView();
//
//    public abstract void initNavigationBarView();
//
//    public abstract void initIntentData(Bundle savedInstanceState, Intent intent);
//
//    public abstract void initActivityViews();
//
//    public abstract void initActivityViewsData(Bundle savedInstanceState);
//
//    public abstract void initActivityActions(Bundle savedInstanceState);
//
//    public abstract void initActivityOnResult(int requestCode, int resultCode, Intent data);
//
//    public abstract void initActivityBackPress();
//
//    public abstract void initActivityDestroyTasks();
//
//    public abstract void initActivityPermissionResult(int requestCode, String permissions[], int[] grantResults);
//
//    public BaseLocationActivity() {
//        this.mActivity = this;
//        this.TAG = getClass().getSimpleName();
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(initActivityLayout());
//
//        mSavedInstanceState = savedInstanceState;
//        initStatusBarView();
//        initNavigationBarView();
//        initIntentData(mSavedInstanceState, getIntent());
//        initActivityViews();
//
//        if (checkAndRequestPermissions()) {
//            initLocation();
//            initActivityViewsData(mSavedInstanceState);
//            initActivityActions(mSavedInstanceState);
//        }
//    }
//
//    public Activity getActivity() {
//        return mActivity;
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//
//        mSavedInstanceState = savedInstanceState;
//    }
//
//    /**********************
//     * Permission methods *
//     **********************/
//    private boolean checkAndRequestPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!RuntimePermissionManager.isAllPermissionsGranted(mActivity)) {
//                ArrayList<String> permissionNeeded = RuntimePermissionManager.getAllDeniedPermissions(mActivity);
//                for (int i = 0; i < permissionNeeded.size(); i++) {
//                    Log.d(TAG, "Ungranted Permission: " + permissionNeeded.get(i));
//                }
//                ActivityCompat.requestPermissions(this, permissionNeeded.toArray(new String[permissionNeeded.size()]), REQUEST_CODE_PERMISSION);
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        initActivityPermissionResult(requestCode, permissions, grantResults);
//
//        switch (requestCode) {
//            case REQUEST_CODE_PERMISSION:
//                if (RuntimePermissionManager.isAllPermissionsGranted(mActivity, permissions)) {
//                    initLocation();
//                    initActivityViewsData(mSavedInstanceState);
//                    initActivityActions(mSavedInstanceState);
//
//                    if (mGoogleApiClient != null) {
//                        mGoogleApiClient.connect();
//                    }
//
//                    startUpdate(mGoogleApiClient, mLocationRequest, mLocationListener);
//                }
//                break;
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        initActivityOnResult(requestCode, resultCode, data);
//
//        //send on activity result event to the fragment
//        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
//        if (fragmentList != null) {
//            for (Fragment fragment : fragmentList) {
//                if (fragment instanceof BaseFragment) {
//                    ((BaseFragment) fragment).onFragmentResult(requestCode, resultCode, data);
//                }
//            }
//        }
//
//        //Handle location result
//        switch (requestCode) {
//            // Check for the integer request code originally supplied to startResolutionForResult().
//            case REQUEST_LOCATION_CHECK_SETTINGS:
//                switch (resultCode) {
//                    case Activity.RESULT_OK:
//                        Log.d(TAG, "User agreed to make required location settings changes.");
//                        startUpdate(mGoogleApiClient, mLocationRequest, mLocationListener);
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        Log.d(TAG, "User chose not to make required location settings changes.");
//                        break;
//                }
//                break;
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        initActivityBackPress();
//
//        //send back press event to the fragment
//        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
//        if (fragmentList != null) {
//            for (Fragment fragment : fragmentList) {
//                if (fragment instanceof BaseFragment) {
//                    ((BaseFragment) fragment).onFragmentBackPressed();
//                }
//            }
//        }
//    }
//
//    public void onDestroy() {
//        initActivityDestroyTasks();
//
//        super.onDestroy();
//    }
//
//    /********************
//     * Location methods *
//     ********************/
//    private void initLocation() {
//        //Check if Google Play Services Available or not
//        if (!isGooglePlayServicesAvailable()) {
//            Log.d(TAG, "Google Play Services are not available");
//            Toast.makeText(BaseLocationActivity.this, "Please install google play service.", Toast.LENGTH_SHORT).show();
//            finish();
//        } else {
//            mGoogleApiClient = initGoogleApiClient();
//            mLocationRequest = initLocationRequest();
//            mLocationListener = initLocationListener();
//            mLocationSettingsRequest = initLocationSettingsRequest(mLocationRequest);
//            checkLocationSettings(mGoogleApiClient, mLocationSettingsRequest);
//        }
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (isGooglePlayServicesAvailable() && RuntimePermissionManager.isAllPermissionsGranted(mActivity)) {
//            if (mGoogleApiClient != null) {
//                mGoogleApiClient.connect();
//            }
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (isGooglePlayServicesAvailable() && RuntimePermissionManager.isAllPermissionsGranted(mActivity)) {
//            startUpdate(mGoogleApiClient, mLocationRequest, mLocationListener);
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (isGooglePlayServicesAvailable() && RuntimePermissionManager.isAllPermissionsGranted(mActivity)) {
//            stopUpdate(mGoogleApiClient, mLocationListener);
//        }
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (isGooglePlayServicesAvailable() && RuntimePermissionManager.isAllPermissionsGranted(mActivity)) {
//            if (mGoogleApiClient != null) {
//                if (mGoogleApiClient.isConnected()) {
//                    mGoogleApiClient.disconnect();
//                }
//            }
//        }
//    }
//
//    public boolean isGooglePlayServicesAvailable() {
//        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
//        int result = googleAPI.isGooglePlayServicesAvailable(BaseLocationActivity.this);
//        if (result != ConnectionResult.SUCCESS) {
////            if (googleAPI.isUserResolvableError(result)) {
////                googleAPI.getErrorDialog(BaseLocationActivity.this, result, 1234).show();
////            }
//            return false;
//        }
//        return true;
//    }
//
//    public GoogleApiClient initGoogleApiClient() {
//        Log.i(TAG, "In initGoogleApiClient");
//        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(BaseLocationActivity.this)
//                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
//                    @Override
//                    public void onConnected(@Nullable Bundle bundle) {
//                        Log.d(TAG, "GoogleApiClient Connection established");
//
//                        //While connecting GoogleApiClient, check last location if found. Otherwise we need to wait for next refresh of getting current location.
//                        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && isLocationSettingsEnabled()) {
//                            Log.d(TAG, "Requesting for last location.");
//                            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//                            if (mCurrentLocation != null) {
//                                Log.d(TAG, "Last Location found: " + mCurrentLocation.toString());
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onConnectionSuspended(int i) {
//                        Log.d(TAG, "GoogleApiClient Connection suspended");
//                    }
//                })
//                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
//                    @Override
//                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//                        Log.d(TAG, "GoogleApiClient Connection failed");
//                    }
//                })
//                .addApi(LocationServices.API)
//                .build();
//        return googleApiClient;
//    }
//
//    public LocationRequest initLocationRequest() {
//        LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setInterval(UPDATE_INTERVAL_LOCATION_IN_MILLISECONDS);
//        locationRequest.setFastestInterval(UPDATE_INTERVAL_FASTEST_IN_MILLISECONDS);
////        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT_IN_METER);
//        locationRequest.setPriority(GPS_ACCURACY);
//        return locationRequest;
//    }
//
//    public LocationListener initLocationListener() {
//        LocationListener locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                mCurrentLocation = location;
//
//                //Stop requesting update if location is found
//                if (mCurrentLocation != null) {
//                    Log.d(TAG, "onLocationChanged: " + mCurrentLocation.toString());
//                    onLocationFound(mCurrentLocation);
//
//                    Log.d(TAG, "Location data found, stopping location update request");
//                    stopUpdate(mGoogleApiClient, mLocationListener);
//                }
//            }
//        };
//        return locationListener;
//    }
//
//    public LocationSettingsRequest initLocationSettingsRequest(LocationRequest locationRequest) {
//        if (locationRequest != null) {
//            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
//            builder.addLocationRequest(locationRequest);
//            LocationSettingsRequest locationSettingsRequest = builder.build();
//            return locationSettingsRequest;
//        }
//        return null;
//    }
//
//    public void checkLocationSettings(GoogleApiClient googleApiClient, LocationSettingsRequest locationSettingsRequest) {
//        if (googleApiClient != null && locationSettingsRequest != null) {
//            PendingResult<LocationSettingsResult> result =
//                    LocationServices.SettingsApi.checkLocationSettings(
//                            googleApiClient,
//                            locationSettingsRequest
//                    );
//            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//                @Override
//                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
//                    final Status status = locationSettingsResult.getStatus();
//                    switch (status.getStatusCode()) {
//
//                        case LocationSettingsStatusCodes.SUCCESS:
//                            Log.d(TAG, "All location settings are satisfied.");
//                            startUpdate(mGoogleApiClient, mLocationRequest, mLocationListener);
//                            break;
//
//                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                            Log.d(TAG, "Location settings are not satisfied. Show the user a dialog to" + "upgrade location settings ");
//                            try {
//                                status.startResolutionForResult(BaseLocationActivity.this, REQUEST_LOCATION_CHECK_SETTINGS);
//                            } catch (IntentSender.SendIntentException e) {
//                                Log.d(TAG, "PendingIntent unable to execute request.");
//                            }
//                            break;
//
//                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                            Log.d(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " + "not created.");
//                            break;
//                    }
//                }
//            });
//        }
//    }
//
//    public boolean isLocationSettingsEnabled() {
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//    }
//
//    public void requestLocationUpdates(GoogleApiClient googleApiClient, LocationRequest locationRequest, LocationListener locationListener) {
//        Log.d(TAG, "In requestLocationUpdates");
//        Log.d(TAG, "googleApiClient: " + (googleApiClient == null));
//        Log.d(TAG, "locationRequest: " + (locationRequest == null));
//        Log.d(TAG, "locationListener: " + (locationListener == null));
//        Log.d(TAG, "googleApiClient.isConnected(): " + (googleApiClient.isConnected()));
//        if (googleApiClient != null && googleApiClient.isConnected() && locationRequest != null && locationListener != null) {
//            Log.d(TAG, "requestLocationUpdates starting");
//            LocationServices.FusedLocationApi.requestLocationUpdates(
//                    googleApiClient,
//                    locationRequest, locationListener
//            ).setResultCallback(new ResultCallback<Status>() {
//                @Override
//                public void onResult(Status status) {
//                    Log.d(TAG, "requestLocationUpdates: " + status.toString());
//                }
//            });
//        }
//    }
//
//    public void removeLocationUpdates(GoogleApiClient googleApiClient, LocationListener locationListener) {
//        Log.d(TAG, "In requestLocationUpdates");
//        if (googleApiClient != null && locationListener != null) {
//            Log.d(TAG, "removeLocationUpdates starting");
//            LocationServices.FusedLocationApi.removeLocationUpdates(
//                    mGoogleApiClient, locationListener
//            ).setResultCallback(new ResultCallback<Status>() {
//                @Override
//                public void onResult(Status status) {
//                    Log.d(TAG, "removeLocationUpdates: " + status.toString());
//                }
//            });
//        }
//
//    }
//
//    public void startUpdate(GoogleApiClient googleApiClient, LocationRequest locationRequest, LocationListener locationListener) {
//        Log.d(TAG, "In startUpdate");
//        Log.d(TAG, "googleApiClient: " + (googleApiClient == null));
//        Log.d(TAG, "locationRequest: " + (locationRequest == null));
//        Log.d(TAG, "locationListener: " + (locationListener == null));
//        Log.d(TAG, "googleApiClient.isConnected(): " + (googleApiClient.isConnected()));
//        if (googleApiClient != null && googleApiClient.isConnected() && locationRequest != null && locationListener != null) {
//            Log.d(TAG, "startUpdate starting");
//            requestLocationUpdates(googleApiClient, locationRequest, locationListener);
//        }
//    }
//
//    public void stopUpdate(GoogleApiClient googleApiClient, LocationListener locationListener) {
//        Log.d(TAG, "In stopUpdate");
//        Log.d(TAG, "googleApiClient: " + (googleApiClient == null));
//        Log.d(TAG, "locationListener: " + (locationListener == null));
//        if (googleApiClient != null && googleApiClient.isConnected() && locationListener != null) {
//            Log.d(TAG, "stopUpdate starting");
//            removeLocationUpdates(googleApiClient, locationListener);
//        }
//    }
//
//    /***************************
//     * Progress dialog methods *
//     ***************************/
//    public ProgressDialog showProgressDialog() {
//        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(getActivity());
//            mProgressDialog.setMessage(getResources().getString(R.string.view_loading));
//            mProgressDialog.setIndeterminate(false);
//            mProgressDialog.setCancelable(true);
//            mProgressDialog.setCanceledOnTouchOutside(false);
//            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface arg0) {
//                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                        mProgressDialog.dismiss();
//                        mProgressDialog = null;
//                    }
//                }
//            });
//        }
//
//        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
//            mProgressDialog.show();
//        }
//
//        return mProgressDialog;
//    }
//
//    public void dismissProgressDialog() {
//        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//            mProgressDialog.dismiss();
//            mProgressDialog = null;
//        }
//    }
//}