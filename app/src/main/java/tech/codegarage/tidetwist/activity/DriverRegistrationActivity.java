package tech.codegarage.tidetwist.activity;

import android.content.Context;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.segmentradiogroup.SegmentedRadioGroup;
import com.reversecoder.library.util.AllSettingsManager;

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
import tech.codegarage.tidetwist.model.DriverUser;
import tech.codegarage.tidetwist.model.KitchenUser;
import tech.codegarage.tidetwist.model.ParamDriverRegistration;
import tech.codegarage.tidetwist.retrofit.APIClient;
import tech.codegarage.tidetwist.retrofit.APIInterface;
import tech.codegarage.tidetwist.retrofit.APIResponse;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.Logger;
import tech.codegarage.tidetwist.util.ValidationManager;
import tech.codegarage.tidetwist.view.CanaroTextView;

import static tech.codegarage.tidetwist.util.KeyboardManager.hideKeyboard;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class DriverRegistrationActivity extends BaseLocationActivity {

    //Toolbar
    private ImageView ivBack;
    private CanaroTextView tvTitle;

    private SegmentedRadioGroup srgAddressType;
    private RadioButton srgRbtnCurrentAddress, srgRbtnPreferredAddress;
    private EditText edtUserAddress, edtUserFirstName, edtUserLastName, edtUserEmail, edtUserPassword;
    private PhoneNumberEditTextWithMask edtUserPhone;
    private ImageView ivAcceptedPhone, ivAcceptedEmail;
    private Button btnRequestRegistration;
    private KitchenUser mKitchenUser;
    private RelativeLayout rlFooter;

    //Background task
    private ReverseGeocoderTask currentLocationTask;
    private String currentAddress = "", preferredAddress = "";
    private UserLocationAddress mUserLocationAddress;
    private APIInterface mApiInterface;
    private TokenFetcher tokenFetcher;
    DoDriverRegistrationTask doDriverRegistrationTask;

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
        return R.layout.activity_driver_registration;
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
                            currentAddress = locationAddress.getAddressLine();

                            //Set current location
                            if (srgRbtnCurrentAddress.isChecked()) {
                                edtUserAddress.setText(currentAddress + "");
                                edtUserAddress.setEnabled(false);
                            }
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
        tvTitle.setText(getString(R.string.title_activity_driver_registration));

        srgAddressType = (SegmentedRadioGroup) findViewById(R.id.srg_address_type);
        srgRbtnCurrentAddress = (RadioButton) findViewById(R.id.srg_rbtn_current_address);
        srgRbtnPreferredAddress = (RadioButton) findViewById(R.id.srg_rbtn_preferred_address);
        edtUserAddress = (EditText) findViewById(R.id.edt_user_address);
        edtUserFirstName = (EditText) findViewById(R.id.edt_user_first_name);
        edtUserLastName = (EditText) findViewById(R.id.edt_user_last_name);
        edtUserEmail = (EditText) findViewById(R.id.edt_user_email);
        edtUserPhone = (PhoneNumberEditTextWithMask) findViewById(R.id.edt_user_phone);
        ivAcceptedPhone = (ImageView) findViewById(R.id.iv_accepted_phone);
        ivAcceptedEmail = (ImageView) findViewById(R.id.iv_accepted_email);
        edtUserPassword = (EditText) findViewById(R.id.edt_user_password);
        btnRequestRegistration = (Button) findViewById(R.id.btn_request_registration);
        rlFooter = (RelativeLayout) findViewById(R.id.rl_footer);
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

        //For disabling country picker
        edtUserPhone.setPickerEnable(false);

        mKitchenUser = AppUtil.getKitchenUser(getActivity());
        Logger.d(TAG, TAG + "mKitchenUser: " + mKitchenUser);
    }

    @Override
    public void initActivityActions(Bundle savedInstanceState) {
        ivBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                initActivityBackPress();
            }
        });

        edtUserPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validateMobileNumber();
            }
        });

        edtUserPhone.setOnTextChangeListener(new PhoneNumberEditTextWithMask.onTextChangeListener() {
            @Override
            public void onTextChange(CharSequence s, int start, int before, int count) {
                validateMobileNumber();
            }
        });

        edtUserEmail.addTextChangedListener(new TextWatcher() {
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

        edtUserEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validateEmail();
            }
        });

        srgAddressType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.srg_rbtn_current_address:
                        preferredAddress = edtUserAddress.getText().toString();

                        edtUserAddress.setTextColor(getResources().getColor(R.color.Off_white));
                        edtUserAddress.setText(currentAddress);
                        edtUserAddress.setEnabled(false);
                        break;
                    case R.id.srg_rbtn_preferred_address:
                        edtUserAddress.setText(preferredAddress);
                        edtUserAddress.setEnabled(true);
                        break;
                }
            }
        });

        btnRequestRegistration.setOnClickListener(new OnBaseClickListener() {
            @Override
            public void OnPermissionValidation(View view) {
                //Hide keyboard
                hideKeyboard(getActivity());

                //Check internet connection
                if (!NetworkManager.isConnected(getActivity())) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check address
                final String address = edtUserAddress.getText().toString();
                if (AllSettingsManager.isNullOrEmpty(address)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_address), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check phone number
                if (AllSettingsManager.isNullOrEmpty(edtUserPhone.getText().toString())) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_mobile_no), Toast.LENGTH_SHORT).show();
                    return;
                }
                final String phoneNumber = edtUserPhone.getFullNumberWithPlus();
                if (!ValidationManager.isValidBangladeshiMobileNumber(phoneNumber)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_valid_mobile_no), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check email
                final String email = edtUserEmail.getText().toString();
                if (AllSettingsManager.isNullOrEmpty(email)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_email), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!ValidationManager.isValidEmail(email)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_valid_email), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check first name
                final String firstName = edtUserFirstName.getText().toString();
                if (AllSettingsManager.isNullOrEmpty(firstName)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_first_name), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check last name
                final String lastName = edtUserLastName.getText().toString();
                if (AllSettingsManager.isNullOrEmpty(lastName)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_last_name), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check password
                final String password = edtUserPassword.getText().toString();
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

                            ParamDriverRegistration paramDriverRegistration = new ParamDriverRegistration("0", firstName, lastName, phoneNumber, email, latitude, longitude, address, "", update, password);
                            Logger.d(TAG, TAG + ">>paramDriverRegistration: " + paramDriverRegistration.toString() + "");

                            //Register driver user
                            doDriverRegistrationTask = new DoDriverRegistrationTask(getActivity(), paramDriverRegistration);
                            doDriverRegistrationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        if (doDriverRegistrationTask != null && doDriverRegistrationTask.getStatus() == AsyncTask.Status.RUNNING) {
            doDriverRegistrationTask.cancel(true);
        }
    }

    @Override
    public void initActivityPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    /**************
     * Validation *
     **************/
    private void validateMobileNumber() {
        if (ValidationManager.isValidBangladeshiMobileNumber(edtUserPhone.getFullNumberWithPlus())) {
            Logger.d(TAG, "validation: valid");
            ivAcceptedPhone.setVisibility(View.VISIBLE);
            ivAcceptedPhone.setBackgroundResource(R.drawable.vector_accepted);
        } else {
            Logger.d(TAG, "validation: gone invalid");
            ivAcceptedPhone.setVisibility(View.GONE);
        }
    }

    private void validateEmail() {
        if (ValidationManager.isValidEmail(edtUserEmail.getText().toString())) {
            Logger.d(TAG, "validation: valid");
            ivAcceptedEmail.setVisibility(View.VISIBLE);
            ivAcceptedEmail.setBackgroundResource(R.drawable.vector_accepted);
        } else {
            Logger.d(TAG, "validation: gone invalid");
            ivAcceptedEmail.setVisibility(View.GONE);
        }
    }

    /************************
     * Server communication *
     ************************/
    private class DoDriverRegistrationTask extends AsyncTask<String, Integer, Response> {

        Context mContext;
        ParamDriverRegistration mParamDriverRegistration;

        public DoDriverRegistrationTask(Context context, ParamDriverRegistration paramDriverRegistration) {
            mContext = context;
            mParamDriverRegistration = paramDriverRegistration;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Logger.d(TAG, TAG + " >>> " + "mParamDriverRegistration: " + mParamDriverRegistration.toString());
                Call<APIResponse<List<DriverUser>>> call = mApiInterface.apiRequestForDriverRegistration(mParamDriverRegistration);
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
                            Logger.d(TAG, "APIResponse(DoKichenSignUpTask()): onResponse-driveruser = " + data.getData().get(0).toString());
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