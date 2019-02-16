package tech.codegarage.tidetwist.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.developers.imagezipper.ImageZipper;
import com.jaeger.library.StatusBarUtil;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.segmentradiogroup.SegmentedRadioGroup;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.library.util.AllSettingsManager;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
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
public class AboutAppUserActivity extends BaseLocationActivity {

    //Toolbar
    ImageView ivBack;
    CanaroTextView tvTitle;

    // Header view
    private ImageView ivUser;
    private EditText edtUserAddress, edtUserName, edtUserEmail;
    private PhoneNumberEditTextWithMask edtUserPhone;
    private ImageView ivAcceptedPhone, ivAcceptedEmail;
    private Button btnUpdate;
    private String currentAddress = "", preferredAddress = "";
    private SegmentedRadioGroup srgAddressType;
    private RadioButton srgRbtnCurrentAddress, srgRbtnPreferredAddress;
    private AppUser mAppUser;
    private Location mCurrentLocation;
    private String locationAddress = "";
    private String mImagePath = "", mBase64 = "";

    //Background task
    private APIInterface mApiInterface;
    private UpdateAppUserTask updateAppUserTask;
    private ReverseGeocoderTask currentLocationTask;
    private TokenFetcher tokenFetcher;

    @Override
    public LOCATION_UPDATE_FREQUENCY initLocationUpdateFrequency() {
        return LOCATION_UPDATE_FREQUENCY.ONCE;
    }

    @Override
    public String[] initActivityPermissions() {
        return new String[]{Manifest.permission.CAMERA};
    }

    @Override
    public int initActivityLayout() {
        return R.layout.activity_about_app_user;
    }

    @Override
    public void initStatusBarView() {
        StatusBarUtil.setTransparent(getActivity());
    }

    @Override
    public void initNavigationBarView() {

    }

    @Override
    public void initIntentData(Bundle savedInstanceState, Intent intent) {

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

                        setUserCurrentAddress(locationAddress);
                    }
                }
            });
            currentLocationTask.execute(location);
        }
    }

    public void setUserCurrentAddress(String address) {
        currentAddress = address;
        if (srgRbtnCurrentAddress.isChecked()) {
            edtUserAddress.setText(currentAddress);
            edtUserAddress.setEnabled(false);
        }
    }

    @Override
    public void initActivityViews() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivUser = (ImageView) findViewById(R.id.iv_user);
        tvTitle = (CanaroTextView) findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.title_activity_profile));

        edtUserEmail = (EditText) findViewById(R.id.edt_user_email);
        edtUserAddress = (EditText) findViewById(R.id.edt_user_address);
        edtUserPhone = (PhoneNumberEditTextWithMask) findViewById(R.id.edt_user_phone);
        ivAcceptedPhone = (ImageView) findViewById(R.id.iv_accepted_phone);
        ivAcceptedEmail = (ImageView) findViewById(R.id.iv_accepted_email);
        edtUserName = (EditText) findViewById(R.id.edt_user_name);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        srgAddressType = (SegmentedRadioGroup) findViewById(R.id.srg_address_type);
        srgRbtnCurrentAddress = (RadioButton) findViewById(R.id.srg_rbtn_current_address);
        srgRbtnPreferredAddress = (RadioButton) findViewById(R.id.srg_rbtn_preferred_address);
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {
        mApiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        //For disabling country picker
        edtUserPhone.setPickerEnable(false);

        String appUser = SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_USER);
        if (!AllSettingsManager.isNullOrEmpty(appUser)) {
            mAppUser = APIResponse.getResponseObject(appUser, AppUser.class);
            Logger.d(TAG, TAG + " >>> " + "mAppUser: " + mAppUser.toString());

            setUserInfo(mAppUser);
        }

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
                        edtUserAddress.setText((AllSettingsManager.isNullOrEmpty(preferredAddress) ? mAppUser.getAddress() : preferredAddress));
                        edtUserAddress.setEnabled(true);
                        break;
                }
            }
        });
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

        ivUser.setOnClickListener(new OnBaseClickListener() {
            @Override
            public void OnPermissionValidation(View view) {
                Matisse.from(getActivity())
                        .choose(MimeType.ofImage())
                        .theme(R.style.Matisse_Dracula)
                        .capture(true)
                        .setDefaultCaptureStrategy()
                        .countable(false)
                        .maxSelectable(1)
                        .imageEngine(new GlideEngine())
                        .forResult(AllConstants.INTENT_REQUEST_CODE_IMAGE_PICKER);
            }
        });

        btnUpdate.setOnClickListener(new OnBaseClickListener() {
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
                //Check address
                String address = edtUserAddress.getText().toString();
                if (AllSettingsManager.isNullOrEmpty(address)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_address), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check phone
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

                //Check name
                final String name = edtUserName.getText().toString();
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

                            //Update app user
                            ParamAppUser paramAppUser = new ParamAppUser(mAppUser.getApp_user_id(), name, phoneNumber, email, latitude, longitude, locationAddress, mBase64, update);
                            updateAppUserTask = new UpdateAppUserTask(getActivity(), paramAppUser);
                            updateAppUserTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        if (requestCode == AllConstants.INTENT_REQUEST_CODE_IMAGE_PICKER && resultCode == RESULT_OK) {
            List<String> mData = Matisse.obtainPathResult(data);

            if (mData.size() == 1) {
                mImagePath = mData.get(0);
                Logger.d(TAG, "MatisseImage: " + mImagePath);

                AppUtil.loadImage(getActivity(), ivUser, mImagePath, false, true, false);

                try {
                    File imageZipperFile = new ImageZipper(getActivity())
                            .setQuality(100)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .compressToFile(new File(mImagePath));
                    mBase64 = AllConstants.PREFIX_BASE64_STRING + ImageZipper.getBase64forImage(imageZipperFile);
                    Logger.d(TAG, "MatisseImage(mBase64): " + mBase64);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void initActivityBackPress() {
        finish();
    }

    @Override
    public void initActivityDestroyTasks() {
        if (currentLocationTask != null && currentLocationTask.getStatus() == AsyncTask.Status.RUNNING) {
            currentLocationTask.cancel(true);
        }
        if (tokenFetcher != null && tokenFetcher.getStatus() == AsyncTask.Status.RUNNING) {
            tokenFetcher.cancel(true);
        }
        if (updateAppUserTask != null && updateAppUserTask.getStatus() == AsyncTask.Status.RUNNING) {
            updateAppUserTask.cancel(true);
        }
    }

    @Override
    public void initActivityPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    private void setUserInfo(AppUser appUser) {
        AppUtil.loadImage(getActivity(), ivUser, appUser.getImage(), false, true, true);

        edtUserAddress.setText(appUser.getAddress());
        edtUserPhone.setText(appUser.getPhone().substring(4, appUser.getPhone().length()));
        edtUserEmail.setText(appUser.getEmail());
        edtUserName.setText(appUser.getName());

        validateMobileNumber();
        validateEmail();
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
    private class UpdateAppUserTask extends AsyncTask<String, Integer, Response> {

        Context mContext;
        ParamAppUser mParamAppUser;

        private UpdateAppUserTask(Context context, ParamAppUser paramAppUser) {
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
                            String jsonAppUser = APIResponse.getResponseString(data.getData().get(0));
                            Logger.d(TAG, TAG + " >>> " + "APIResponse(DoCreateUser()): app-user = " + jsonAppUser);
                            Logger.d(TAG, TAG + " >>> " + "AppUser(home-response): " + jsonAppUser);
                            SessionManager.setStringSetting(getActivity(), AllConstants.SESSION_KEY_USER, jsonAppUser);

                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_profile_is_updated_successfully), Toast.LENGTH_SHORT).show();
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