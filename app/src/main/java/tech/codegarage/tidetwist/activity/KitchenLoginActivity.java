package tech.codegarage.tidetwist.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.base.BaseLocationActivity;
import tech.codegarage.tidetwist.model.KitchenUser;
import tech.codegarage.tidetwist.model.ParamKitchenLogin;
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
public class KitchenLoginActivity extends BaseLocationActivity {

    //Toolbar
    private ImageView ivBack;
    private CanaroTextView tvTitle;

    private EditText edtKitchenPassword;
    private PhoneNumberEditTextWithMask edtKitchenPhone;
    private ImageView ivAcceptedPhone;
    private Button btnKitchenLogin, btnRequestRegistration;

    //Background task
    private APIInterface apiInterface;
    DoKitchenLoginTask doKitchenLoginTask;

    @Override
    public LOCATION_UPDATE_FREQUENCY initLocationUpdateFrequency() {
        return LOCATION_UPDATE_FREQUENCY.ONCE;
    }

    @Override
    public void onLocationFound(Location location) {

    }

    @Override
    public String[] initActivityPermissions() {
        return new String[]{};
    }

    @Override
    public int initActivityLayout() {
        return R.layout.activity_kitchen_login;
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
    public void initActivityViews() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (CanaroTextView) findViewById(R.id.tv_title);
        tvTitle = (CanaroTextView) findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.title_activity_kitchen_login));

        edtKitchenPhone = (PhoneNumberEditTextWithMask) findViewById(R.id.edt_kitchen_phone);
        ivAcceptedPhone = (ImageView) findViewById(R.id.iv_accepted_phone);
        edtKitchenPassword = (EditText) findViewById(R.id.edt_kitchen_password);
        btnKitchenLogin = (Button) findViewById(R.id.btn_kitchen_login);
        btnRequestRegistration = (Button) findViewById(R.id.btn_request_registration);
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {
        apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        //For disabling country picker
        edtKitchenPhone.setPickerEnable(false);

        if (AppUtil.isDebug(getActivity())) {
            edtKitchenPhone.setText("1794-620787");
            edtKitchenPassword.setText("123456");

            validateMobileNumber();
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

        btnRequestRegistration.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Intent intentKitchenRegistration = new Intent(getActivity(), KitchenRegistrationActivity.class);
                startActivity(intentKitchenRegistration);
            }
        });
        btnKitchenLogin.setOnClickListener(new OnBaseClickListener() {
            @Override
            public void OnPermissionValidation(View view) {
                //Hide keyboard
                KeyboardManager.hideKeyboard(getActivity());

                //Check internet connection
                if (!NetworkManager.isConnected(getActivity())) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    return;
                }

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

                //Check password
                String password = edtKitchenPassword.getText().toString();
                if (AllSettingsManager.isNullOrEmpty(password)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_password), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Login Kitchen
                ParamKitchenLogin paramKitchenLogin = new ParamKitchenLogin(phoneNumber, password);
                Logger.d(TAG, TAG + " >>> " + "paramKitchenLogin: " + paramKitchenLogin.toString());
                doKitchenLoginTask = new DoKitchenLoginTask(getActivity(), paramKitchenLogin);
                doKitchenLoginTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

        if (doKitchenLoginTask != null && doKitchenLoginTask.getStatus() == AsyncTask.Status.RUNNING) {
            doKitchenLoginTask.cancel(true);
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

    /************************
     * Server communication *
     ************************/
    private class DoKitchenLoginTask extends AsyncTask<String, Integer, Response> {

        Context mContext;
        ParamKitchenLogin mParamKitchenLogin;

        public DoKitchenLoginTask(Context context, ParamKitchenLogin paramKitchenLogin) {
            mContext = context;
            mParamKitchenLogin = paramKitchenLogin;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Logger.d(TAG, TAG + " >>> " + "mKitchenLogin: " + mParamKitchenLogin.toString());
                Call<APIResponse<List<KitchenUser>>> call = apiInterface.apiKitchenLogin(mParamKitchenLogin);

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
                    Logger.d(TAG, "APIResponse(DoKitchenLoginTask): onResponse-server = " + result.toString());
                    APIResponse<List<KitchenUser>> data = (APIResponse<List<KitchenUser>>) result.body();
                    Logger.d("AppUserKitchen", data.toString());

                    if (data != null) {
                        Logger.d(TAG, "APIResponse(DoKitchenLoginTask()): onResponse-object = " + data.toString());

                        if (data.getStatus().equalsIgnoreCase("1") && data.getData().size() == 1) {
                            KitchenUser mKitchenUser = data.getData().get(0);
                            Logger.d(TAG, "APIResponse(DoKitchenLoginTask()): onResponse-mKitchenUser = " + mKitchenUser.toString());

                            if (mKitchenUser.getIs_approved().equalsIgnoreCase("1")) {
                                //Save data into session
                                String jsonKitchenUser = APIResponse.getResponseString(data.getData().get(0));
                                Logger.d(TAG, TAG + " >>> " + "APIResponse(DoKichenLogin): kitchen-user = " + jsonKitchenUser);
                                SessionManager.setStringSetting(getActivity(), AllConstants.SESSION_KEY_KITCHEN, jsonKitchenUser);

                                //Register fcm for getting notifications
                                FcmUtil.saveIsFcmRegistered(getActivity(), 1);

                                //Show toast message
                                Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_LONG).show();

                                //return to the home activity
                                Intent intentKitchenHome = new Intent(getActivity(), HomeActivity.class);
                                startActivity(intentKitchenHome);
                                finish();
                            } else {
                                Toast.makeText(getActivity(), getResources().getString(R.string.toast_the_user_is_not_verified), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_LONG).show();
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