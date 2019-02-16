package tech.codegarage.tidetwist.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.nex3z.flowlayout.FlowLayout;
import com.nex3z.flowlayout.FlowLayoutManager;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.library.util.AllSettingsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.jerryhanks.countrypicker.PhoneNumberEditTextWithMask;
import retrofit2.Call;
import retrofit2.Response;
import tech.codegarage.fcm.util.OnTokenUpdateListener;
import tech.codegarage.fcm.util.TokenFetcher;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.base.BaseLocationActivity;
import tech.codegarage.tidetwist.geocoding.LocationAddressListener;
import tech.codegarage.tidetwist.geocoding.ReverseGeocoderTask;
import tech.codegarage.tidetwist.geocoding.UserLocationAddress;
import tech.codegarage.tidetwist.model.Area;
import tech.codegarage.tidetwist.model.City;
import tech.codegarage.tidetwist.model.KitchenUser;
import tech.codegarage.tidetwist.model.ParamKitchenRegistration;
import tech.codegarage.tidetwist.model.ResponseOfflineCityWithArea;
import tech.codegarage.tidetwist.retrofit.APIClient;
import tech.codegarage.tidetwist.retrofit.APIInterface;
import tech.codegarage.tidetwist.retrofit.APIResponse;
import tech.codegarage.tidetwist.util.AllConstants;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.KeyboardManager;
import tech.codegarage.tidetwist.util.Logger;
import tech.codegarage.tidetwist.util.ValidationManager;
import tech.codegarage.tidetwist.view.CanaroTextView;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class KitchenRegistrationActivity extends BaseLocationActivity {

    //Toolbar
    ImageView ivBack;
    CanaroTextView tvTitle;

    private EditText edtKitchenName, edtKitchenEmail, edtKitchenPassword;
    private PhoneNumberEditTextWithMask edtKitchenPhone;
    private ImageView ivAcceptedPhone, ivAcceptedEmail;
    private Button btnRequestRegistration;
    private KitchenUser mKitchenUser;
    private RelativeLayout rlFooter;

    // Flow Layout
    private FlowLayout flowLayoutDivision, flowLayoutZone;
    private FlowLayoutManager flowLayoutManagerDivision, flowLayoutManagerZone;
    private TextView tvDivision, tvZone;
    private City mDivision;
    private Area mZone;

    //Background task
    private ReverseGeocoderTask currentLocationTask;
    private String mLocationAddress = "";
    private UserLocationAddress mUserLocationAddress;
    private APIInterface mApiInterface;
    private GetAllCityWithAreaTask getAllCityWithAreaTask;
    private TokenFetcher tokenFetcher;
    private DoKitchenRegistrationTask doKitchenRegistrationTask;

    @Override
    public LOCATION_UPDATE_FREQUENCY initLocationUpdateFrequency() {
        return LOCATION_UPDATE_FREQUENCY.ONCE;
    }

    @Override
    public String[] initActivityPermissions() {
        return new String[]{};
    }

    @Override
    public int initActivityLayout() {
        return R.layout.activity_kitchen_registration;
    }

    @Override
    public void initStatusBarView() {
        StatusBarUtil.setTransparent(getActivity());
    }

    @Override
    public void onLocationFound(Location location) {
        if (location != null) {

            if (NetworkManager.isConnected(getActivity())) {

                if ((currentLocationTask != null) && (currentLocationTask.getStatus() == AsyncTask.Status.RUNNING)) {
                    currentLocationTask.cancel(true);
                }

                currentLocationTask = new ReverseGeocoderTask(getActivity(), new LocationAddressListener() {
                    @Override
                    public void getLocationAddress(UserLocationAddress locationAddress) {
                        if (locationAddress != null) {
                            Logger.d(TAG, "UserLocationAddress: " + locationAddress.toString());
                            mUserLocationAddress = locationAddress;
                            mLocationAddress = locationAddress.getAddressLine();
                        }
                    }
                });
                currentLocationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, location);
            }
        }
    }

    @Override
    public void initNavigationBarView() {

    }

    @Override
    public void initIntentData(Bundle savedInstanceState, Intent intent) {

    }

    @Override
    public void initActivityViews() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (CanaroTextView) findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.title_activity_kitchen_registration));

        edtKitchenName = (EditText) findViewById(R.id.edt_user_name);
        edtKitchenEmail = (EditText) findViewById(R.id.edt_user_email);
        edtKitchenPhone = (PhoneNumberEditTextWithMask) findViewById(R.id.edt_user_phone);
        edtKitchenPassword = (EditText) findViewById(R.id.edt_user_password);
        btnRequestRegistration = (Button) findViewById(R.id.btn_request_registration);
        ivAcceptedPhone = (ImageView) findViewById(R.id.iv_accepted_phone);
        ivAcceptedEmail = (ImageView) findViewById(R.id.iv_accepted_email);
        rlFooter = (RelativeLayout) findViewById(R.id.rl_footer);

        //Flow layout
        flowLayoutDivision = (FlowLayout) findViewById(R.id.fl_division);
        flowLayoutZone = (FlowLayout) findViewById(R.id.fl_zone);
        tvDivision = (TextView) findViewById(R.id.tv_user_division);
        tvZone = (TextView) findViewById(R.id.tv_user_zone);
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {
        //Set scroll view bottom margin for those device which has navigation bar
        if (!AppUtil.hasNavigationBar(getActivity())) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rlFooter.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, 50);
            rlFooter.setLayoutParams(layoutParams);
        }

        mApiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        mKitchenUser = AppUtil.getKitchenUser(getActivity());
        Logger.d(TAG, TAG + "mKitchenUser: " + mKitchenUser);

        //For disabling country picker
        edtKitchenPhone.setPickerEnable(false);

        if (!NetworkManager.isConnected(getActivity())) {
            loadOfflineCountryWithCityData();
        } else {
            getAllCityWithAreaTask = new GetAllCityWithAreaTask(getActivity());
            getAllCityWithAreaTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void initActivityActions(Bundle savedInstanceState) {
        ivBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                initActivityBackPress();
            }
        });

        edtKitchenPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validateMobileNumber();
            }
        });

        edtKitchenPhone.setOnTextChangeListener(new PhoneNumberEditTextWithMask.onTextChangeListener() {
            @Override
            public void onTextChange(CharSequence s, int start, int before, int count) {
                validateMobileNumber();
            }
        });

        edtKitchenEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtKitchenEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validateEmail();
            }
        });

        btnRequestRegistration.setOnClickListener(new OnBaseClickListener() {
            @Override
            public void OnPermissionValidation(View view) {
                //Hide keyboard
                KeyboardManager.hideKeyboard(getActivity());

                //Check internet connection
                if (!NetworkManager.isConnected(getActivity())) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check empty fields
                //Check phone number
                if (AllSettingsManager.isNullOrEmpty(edtKitchenPhone.getText().toString())) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_mobile_no), Toast.LENGTH_SHORT).show();
                    return;
                }
                final String phoneNumber = edtKitchenPhone.getFullNumberWithPlus();
                Logger.d(TAG, TAG + " >>> " + "phoneNumberVerification(setOnClickListener): " + phoneNumber);
                if (!ValidationManager.isValidBangladeshiMobileNumber(phoneNumber)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_valid_mobile_no), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check email
                final String email = edtKitchenEmail.getText().toString();
                if (AllSettingsManager.isNullOrEmpty(email)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_email), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!ValidationManager.isValidEmail(email)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_valid_email), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check division
                if (mDivision == null) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_select_your_division), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check zone
                if (mZone == null) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_select_your_zone), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check name
                final String name = edtKitchenName.getText().toString();
                if (AllSettingsManager.isNullOrEmpty(name)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_name), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check password
                final String password = edtKitchenPassword.getText().toString();
                if (AllSettingsManager.isNullOrEmpty(password)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_password), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check current location
                final String latitude = (mUserLocationAddress != null) ? (!AllSettingsManager.isNullOrEmpty(mUserLocationAddress.getLatitude() + "") ? mUserLocationAddress.getLatitude() + "" : "") : "";
                final String longitude = (mUserLocationAddress != null) ? (!AllSettingsManager.isNullOrEmpty(mUserLocationAddress.getLongitude() + "") ? mUserLocationAddress.getLongitude() + "" : "") : "";

                //Fetch new token, this is necessary for registering kitchen
                if (tokenFetcher != null && tokenFetcher.getStatus() == AsyncTask.Status.RUNNING) {
                    tokenFetcher.cancel(true);
                }
                showProgressDialog();
                tokenFetcher = new TokenFetcher(getActivity(), new OnTokenUpdateListener() {
                    @Override
                    public void onTokenUpdate(String update) {
                        if (!AllSettingsManager.isNullOrEmpty(update)) {
                            Logger.d(TAG, TAG + " >>> " + "mFcmToken(update response): " + update);

                            ParamKitchenRegistration paramKitchenRegistration = new ParamKitchenRegistration("0", name, mLocationAddress, ((mZone != null) ? mZone.getZone_id() : ""), latitude, longitude, email, password, phoneNumber, update);
                            Logger.d(TAG, TAG + ">>paramKitchenRegistration: " + paramKitchenRegistration.toString() + "");

                            //Register kitchen user
                            doKitchenRegistrationTask = new DoKitchenRegistrationTask(getActivity(), paramKitchenRegistration);
                            doKitchenRegistrationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            dismissProgressDialog();
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                tokenFetcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    @Override
    public void initActivityOnResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void initActivityBackPress() {
        finish();
    }

    @Override
    public void initActivityDestroyTasks() {
        dismissProgressDialog();

        if (currentLocationTask != null && currentLocationTask.getStatus() == AsyncTask.Status.RUNNING) {
            currentLocationTask.cancel(true);
        }
        if (tokenFetcher != null && tokenFetcher.getStatus() == AsyncTask.Status.RUNNING) {
            tokenFetcher.cancel(true);
        }
        if (getAllCityWithAreaTask != null && getAllCityWithAreaTask.getStatus() == AsyncTask.Status.RUNNING) {
            getAllCityWithAreaTask.cancel(true);
        }
        if (doKitchenRegistrationTask != null && doKitchenRegistrationTask.getStatus() == AsyncTask.Status.RUNNING) {
            doKitchenRegistrationTask.cancel(true);
        }
    }

    @Override
    public void initActivityPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    /**************
     * Validation *
     **************/
    private void validateMobileNumber() {
        if (ValidationManager.isValidBangladeshiMobileNumber(edtKitchenPhone.getFullNumberWithPlus())) {
            Logger.d(TAG, "validation: valid");
            ivAcceptedPhone.setVisibility(View.VISIBLE);
            ivAcceptedPhone.setBackgroundResource(R.drawable.vector_accepted);
        } else {
            Logger.d(TAG, "validation: gone invalid");
            ivAcceptedPhone.setVisibility(View.GONE);
        }
    }

    private void validateEmail() {
        if (ValidationManager.isValidEmail(edtKitchenEmail.getText().toString())) {
            Logger.d(TAG, "validation: valid");
            ivAcceptedEmail.setVisibility(View.VISIBLE);
            ivAcceptedEmail.setBackgroundResource(R.drawable.vector_accepted);
        } else {
            Logger.d(TAG, "validation: gone invalid");
            ivAcceptedEmail.setVisibility(View.GONE);
        }
    }

    /***************************
     * Methods for flow layout *
     ***************************/
    private void initFlowLayoutDivision(final List<City> cities) {
        List<String> mCityKey = getUniqueDivisionKeys(cities);
        Logger.d(TAG, "mCityKey: " + mCityKey.size());

        if (mCityKey.size() > 0) {
            flowLayoutManagerDivision = new FlowLayoutManager.FlowViewBuilder(getActivity(), flowLayoutDivision, mCityKey, new FlowLayoutManager.onFlowViewClick() {
                @Override
                public void flowViewClick(TextView updatedTextView) {
                    List<TextView> selectedDivision = flowLayoutManagerDivision.getSelectedFlowViews();
                    String mSelectedDivision = (selectedDivision.size() > 0) ? selectedDivision.get(0).getText().toString() : "";

                    //Clear zone data
                    if (flowLayoutManagerZone != null) {
                        flowLayoutManagerZone.removeAllKeys();
                    }
                    mZone = null;
                    tvZone.setText(getString(R.string.view_zone));

                    //Select division
                    if (AllSettingsManager.isNullOrEmpty(mSelectedDivision)) {
                        tvDivision.setText(getString(R.string.view_division));
                        mDivision = null;
                    } else {
                        tvDivision.setText(mSelectedDivision);
                        mDivision = getDivisionByName(cities, mSelectedDivision);

                        //Select zone
                        if (mDivision != null) {
                            initFlowLayoutZone(mDivision.getArea());
                        }
                    }
                }
            })
                    .setSingleChoice(true)
                    .build();
        }
    }

    private void initFlowLayoutZone(final List<Area> areas) {
        if (areas.size() > 0) {
            List<String> mZoneKey = getUniqueZoneKeys(areas);
            if (mZoneKey.size() > 0) {
                flowLayoutManagerZone = new FlowLayoutManager.FlowViewBuilder(getActivity(), flowLayoutZone, mZoneKey, new FlowLayoutManager.onFlowViewClick() {
                    @Override
                    public void flowViewClick(TextView updatedTextView) {
                        List<TextView> selectedZone = flowLayoutManagerZone.getSelectedFlowViews();
                        String mSelectedZone = (selectedZone.size() > 0) ? selectedZone.get(0).getText().toString() : "";

                        if (AllSettingsManager.isNullOrEmpty(mSelectedZone)) {
                            tvZone.setText(getString(R.string.view_zone));
                            mZone = null;
                        } else {
                            tvZone.setText(mSelectedZone);
                            mZone = getZone(areas, mSelectedZone);
                        }
                    }
                })
                        .setSingleChoice(true)
                        .build();

                //set selected category
//            if ((restaurantLoginData != null)) {
//                if (!AllSettingsManager.isNullOrEmpty(restaurantLoginData.getRestaurant_category_name())) {
//                    flowLayoutManager.clickFlowView(restaurantLoginData.getRestaurant_category_name());
//                    tvZone.setText(restaurantLoginData.getRestaurant_category_name());
//                }
//            }
            }
        }
    }

    public List<String> getUniqueZoneKeys(List<Area> areas) {
        List<String> categories = new ArrayList<>();
        for (Area area : areas) {
            categories.add(area.getName());
        }
        Collections.sort(categories);
        return categories;
    }

    public List<String> getUniqueDivisionKeys(List<City> cities) {
        List<String> categories = new ArrayList<>();
        for (City city : cities) {
            categories.add(city.getName());
        }
        Collections.sort(categories);
        return categories;
    }

    public City getDivisionByName(List<City> cities, String city) {
        for (City mCity : cities) {
            if (mCity.getName().equalsIgnoreCase(city)) {
                return mCity;
            }
        }
        return null;
    }

    public City getDivisionById(List<City> cities, String cityId) {
        for (City mCity : cities) {
            if (mCity.getCountry_id().equalsIgnoreCase(cityId)) {
                return mCity;
            }
        }
        return null;
    }

    public Area getZone(List<Area> areas, String area) {
        for (Area mArea : areas) {
            if (mArea.getName().equalsIgnoreCase(area)) {
                return mArea;
            }
        }
        return null;
    }

    /************************
     * Server communication *
     ************************/
    private void loadOfflineCountryWithCityData() {
        String offlineCityWithArea = "";
        if (!AllSettingsManager.isNullOrEmpty(SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_CITY_WITH_AREA))) {
            offlineCityWithArea = SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_CITY_WITH_AREA);
            Logger.d(TAG, "KitchenCityWithAreaData(Session): " + offlineCityWithArea);
        } else {
            offlineCityWithArea = AllConstants.DEFAULT_CITY_WITH_AREA;
            Logger.d(TAG, "KitchenCityWithAreaData(default): " + offlineCityWithArea);
        }

        ResponseOfflineCityWithArea responseOfflineCityWithArea = APIResponse.getResponseObject(offlineCityWithArea, ResponseOfflineCityWithArea.class);
        initFlowLayoutDivision(responseOfflineCityWithArea.getData());
    }

    private class GetAllCityWithAreaTask extends AsyncTask<String, Integer, Response> {

        Context mContext;

        public GetAllCityWithAreaTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            ProgressDialog progressDialog = showProgressDialog();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                }
            });
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Logger.d(TAG, TAG + ">> Background task is cancelled");
            loadOfflineCountryWithCityData();
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Call<APIResponse<List<City>>> call = mApiInterface.apiGetAllCitiesWithAreas();

                Response response = call.execute();
                if (response.isSuccessful()) {
                    return response;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Response result) {
            dismissProgressDialog();
            try {
                if (result != null && result.isSuccessful()) {
                    Logger.d(TAG, "APIResponse(GetAllCityWithTask): onResponse-server = " + result.toString());
                    APIResponse<List<City>> data = (APIResponse<List<City>>) result.body();

                    if (data != null && data.getStatus().equalsIgnoreCase("1")) {
                        Logger.d(TAG, "APIResponse(GetAllCityWithTask()): onResponse-object = " + data.toString());

                        //Initialize city with area data for search tab bar
                        if (data.getData() != null && data.getData().size() > 0) {
                            //Store times data into the session
                            SessionManager.setStringSetting(getActivity(), AllConstants.SESSION_KEY_CITY_WITH_AREA, data.toString());

                            initFlowLayoutDivision(data.getData());
                        }
                    } else {
                        loadOfflineCountryWithCityData();
//                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_info_found) + ",\n" + getResources().getString(R.string.toast_loaded_offline_data), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loadOfflineCountryWithCityData();
//                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_could_not_retrieve_info) + ",\n" + getResources().getString(R.string.toast_loaded_offline_data), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private class DoKitchenRegistrationTask extends AsyncTask<String, Integer, Response> {

        Context mContext;
        ParamKitchenRegistration mParamKitchenRegistration;

        public DoKitchenRegistrationTask(Context context, ParamKitchenRegistration paramKitchenRegistration) {
            mContext = context;
            mParamKitchenRegistration = paramKitchenRegistration;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Logger.d(TAG, TAG + " >>> " + "mParamKitchenRegistration: " + mParamKitchenRegistration.toString());
                Call<APIResponse<List<KitchenUser>>> call = mApiInterface.apiRequestForKitchenRegistration(mParamKitchenRegistration);
                Logger.d(TAG, TAG + ">> call: " + call.toString());

                Response response = call.execute();
                Logger.d(TAG, TAG + " >>> response: " + response + "");
                if (response.isSuccessful()) {
                    return response;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Response result) {
            try {
                dismissProgressDialog();

                if (result != null && result.isSuccessful()) {
                    Logger.d(TAG, "APIResponse(DoKichenSignUpTask): onResponse-server = " + result.toString());
                    APIResponse<List<KitchenUser>> data = (APIResponse<List<KitchenUser>>) result.body();
                    Logger.d(TAG, "APIResponse(data): " + data.toString());

                    if (data != null) {
                        Logger.d(TAG, "APIResponse(DoKichenSignUpTask()): onResponse-object = " + data.toString());

                        Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_LONG).show();
                        if (data.getStatus().equalsIgnoreCase("1") && data.getData().size() == 1) {
                            Logger.d(TAG, "APIResponse(DoKichenSignUpTask()): onResponse-kitchenuser = " + data.getData().get(0).toString());
                        }
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_info_found), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}