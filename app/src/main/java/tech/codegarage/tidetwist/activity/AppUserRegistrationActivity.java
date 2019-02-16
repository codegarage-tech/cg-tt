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
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.jaeger.library.StatusBarUtil;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.library.util.AllSettingsManager;

import java.util.List;

import me.jerryhanks.countrypicker.PhoneNumberEditTextWithMask;
import retrofit2.Call;
import retrofit2.Response;
import tech.codegarage.fcm.util.FcmUtil;
import tech.codegarage.fcm.util.OnTokenUpdateListener;
import tech.codegarage.fcm.util.TokenFetcher;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.base.BaseLocationActivity;
import tech.codegarage.tidetwist.geocoding.LocationAddressListener;
import tech.codegarage.tidetwist.geocoding.ReverseGeocoderTask;
import tech.codegarage.tidetwist.geocoding.UserLocationAddress;
import tech.codegarage.tidetwist.model.AppUser;
import tech.codegarage.tidetwist.model.ParamAppUser;
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
public class AppUserRegistrationActivity extends BaseLocationActivity {

    //Toolbar
    private ImageView ivBack;
    private CanaroTextView tvTitle;
    private LinearLayout llMenu;

    private Location mCurrentLocation;
    private Button btnRegisterUser;
    private ImageView ivAcceptedPhone, ivAcceptedEmail;
    private PhoneNumberEditTextWithMask edtPhone;
    private EditText edtName, edtEmail;

    //Background task
    private APIInterface mApiInterface;
    private RegisterAppUserTask registerAppUserTask;
    private ReverseGeocoderTask currentLocationTask;
    private TokenFetcher tokenFetcher;

    private String locationAddress = "";

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
        return R.layout.activity_app_user_registration;
    }

    @Override
    public void initIntentData(Bundle savedInstanceState, Intent intent) {

    }

    @Override
    public void initStatusBarView() {
        StatusBarUtil.setTransparent(getActivity());
    }

    @Override
    public void initNavigationBarView() {

    }

    @Override
    public void initActivityViews() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (CanaroTextView) findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.title_activity_app_user_registration));
        llMenu = (LinearLayout) findViewById(R.id.ll_menu);

        btnRegisterUser = (Button) findViewById(R.id.btn_register_user);
        ivAcceptedPhone = (ImageView) findViewById(R.id.iv_accepted_phone);
        ivAcceptedEmail = (ImageView) findViewById(R.id.iv_accepted_email);
        edtPhone = (PhoneNumberEditTextWithMask) findViewById(R.id.edt_phone);
        edtName = (EditText) findViewById(R.id.edt_name);
        edtEmail = (EditText) findViewById(R.id.edt_email);
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {
        mApiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        //For disabling country picker
        edtPhone.setPickerEnable(false);

        if (AppUtil.isDebug(getActivity())) {
            edtPhone.setText("1794-620787");
            edtEmail.setText("rashed.droid@gmail.com");
            edtName.setText("Md. Rashadul Alam");

            validateMobileNumber();
            validateEmail();
        }
    }

    @Override
    public void onLocationFound(Location location) {
        if (location != null) {
            mCurrentLocation = location;

            if ((currentLocationTask != null) && (currentLocationTask.getStatus() == AsyncTask.Status.RUNNING)) {
                currentLocationTask.cancel(true);
            }
            currentLocationTask = new ReverseGeocoderTask(getActivity(), new LocationAddressListener() {
                @Override
                public void getLocationAddress(UserLocationAddress userLocationAddress) {
                    if (userLocationAddress != null) {
                        Logger.d(TAG, "UserLocationAddress: " + userLocationAddress.toString());
                        locationAddress = userLocationAddress.getAddressLine();
                    }
                }
            });
            currentLocationTask.execute(location);
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

        edtPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validateMobileNumber();
            }
        });

        edtPhone.setOnTextChangeListener(new PhoneNumberEditTextWithMask.onTextChangeListener() {
            @Override
            public void onTextChange(CharSequence s, int start, int before, int count) {
                validateMobileNumber();
            }
        });

        edtEmail.addTextChangedListener(new TextWatcher() {
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

        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validateEmail();
            }
        });

        btnRegisterUser.setOnClickListener(new OnBaseClickListener() {
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
                if (AllSettingsManager.isNullOrEmpty(edtPhone.getText().toString())) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_mobile_no), Toast.LENGTH_SHORT).show();
                    return;
                }
                final String phoneNumber = edtPhone.getFullNumberWithPlus();
                Logger.d(TAG, TAG + " >>> " + "phoneNumberVerification(setOnClickListener): " + phoneNumber);
                if (!ValidationManager.isValidBangladeshiMobileNumber(phoneNumber)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_valid_mobile_no), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check email
                final String email = edtEmail.getText().toString();
                if (AllSettingsManager.isNullOrEmpty(email)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_email), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!ValidationManager.isValidEmail(email)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_valid_email), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check name
                final String name = edtName.getText().toString();
                if (AllSettingsManager.isNullOrEmpty(name)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_name), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Fetch new token, this is necessary for registering app user
                if (tokenFetcher != null && tokenFetcher.getStatus() == AsyncTask.Status.RUNNING) {
                    tokenFetcher.cancel(true);
                }
                showProgressDialog();
                tokenFetcher = new TokenFetcher(getActivity(), new OnTokenUpdateListener() {
                    @Override
                    public void onTokenUpdate(String update) {
                        if (!AllSettingsManager.isNullOrEmpty(update)) {
                            Logger.d(TAG, TAG + " >>> " + "mFcmToken(update response): " + update);

                            //Get current location
                            String latitude = (mCurrentLocation != null) ? (!AllSettingsManager.isNullOrEmpty(mCurrentLocation.getLatitude() + "") ? mCurrentLocation.getLatitude() + "" : "") : "";
                            String longitude = (mCurrentLocation != null) ? (!AllSettingsManager.isNullOrEmpty(mCurrentLocation.getLongitude() + "") ? mCurrentLocation.getLongitude() + "" : "") : "";

                            //Register app user
                            ParamAppUser paramAppUser = new ParamAppUser("0", name, phoneNumber, email, latitude, longitude, locationAddress, "", update);
                            registerAppUserTask = new RegisterAppUserTask(getActivity(), paramAppUser);
                            registerAppUserTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
        dialogBuilder
                .withTitle(getString(R.string.dialog_message))
                .withMessage(getString(R.string.dialog_do_you_wanna_close_the_app))
                .withEffect(Effectstype.Newspager)
                .withDuration(700)
                .withButton1Text(getString(R.string.dialog_cancel))
                .withButton2Text(getString(R.string.dialog_ok))
                .isCancelableOnTouchOutside(true)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Dismiss dialog
                        dialogBuilder.dismiss();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Dismiss dialog
                        dialogBuilder.dismiss();
                        finish();
                    }
                })
                .show();
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
        if (registerAppUserTask != null && registerAppUserTask.getStatus() == AsyncTask.Status.RUNNING) {
            registerAppUserTask.cancel(true);
        }
    }

    @Override
    public void initActivityPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    /**************
     * Validation *
     **************/
    private void validateMobileNumber() {
        if (ValidationManager.isValidBangladeshiMobileNumber(edtPhone.getFullNumberWithPlus())) {
            Logger.d(TAG, "validation: valid");
            ivAcceptedPhone.setVisibility(View.VISIBLE);
            ivAcceptedPhone.setBackgroundResource(R.drawable.vector_accepted);
        } else {
            Logger.d(TAG, "validation: gone invalid");
            ivAcceptedPhone.setVisibility(View.GONE);
        }
    }

    private void validateEmail() {
        if (ValidationManager.isValidEmail(edtEmail.getText().toString())) {
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
    private class RegisterAppUserTask extends AsyncTask<String, Integer, Response> {

        Context mContext;
        ParamAppUser mParamAppUser;

        private RegisterAppUserTask(Context context, ParamAppUser paramAppUser) {
            mContext = context;
            mParamAppUser = paramAppUser;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Logger.d(TAG, TAG + " >>> " + "AppUser(home-param): " + mParamAppUser.toString());
                Call<APIResponse<List<AppUser>>> call = mApiInterface.apiCreateAppUser(mParamAppUser);

                Response response = call.execute();
                Logger.d(TAG, TAG + " >>> " + "response: " + response);
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
                    Logger.d(TAG, TAG + " >>> " + "APIResponse(DoCreateUser): onResponse-server = " + result.toString());
                    APIResponse<List<AppUser>> data = (APIResponse<List<AppUser>>) result.body();
                    Logger.d(TAG, TAG + " >>> " + "data: " + data.toString() + "");

                    if (data != null && data.getStatus().equalsIgnoreCase("1")) {
                        Logger.d(TAG, TAG + " >>> " + "APIResponse(DoCreateUser()): onResponse-object = " + data.toString());

                        if (data.getData().size() == 1) {
                            //Save data into session
                            String jsonAppUser = APIResponse.getResponseString(data.getData().get(0));
                            Logger.d(TAG, TAG + " >>> " + "APIResponse(DoCreateUser()): app-user = " + jsonAppUser);
                            Logger.d(TAG, TAG + " >>> " + "AppUser(home-response): " + jsonAppUser);
                            SessionManager.setStringSetting(getActivity(), AllConstants.SESSION_KEY_USER, jsonAppUser);

                            //Register fcm for getting notifications
                            FcmUtil.saveIsFcmRegistered(getActivity(), 1);

                            //Navigate to the home
                            Intent intentHome = new Intent(getActivity(), HomeActivity.class);
                            startActivity(intentHome);
                            finish();
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