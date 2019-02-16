package tech.codegarage.tidetwist.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.badoualy.stepperindicator.StepperIndicator;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.jaeger.library.StatusBarUtil;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.random.RandomManager;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.library.util.AllSettingsManager;
import com.sslcommerz.library.payment.Classes.PayUsingSSLCommerz;
import com.sslcommerz.library.payment.Listener.OnPaymentResultListener;
import com.sslcommerz.library.payment.Util.ConstantData.CurrencyType;
import com.sslcommerz.library.payment.Util.ConstantData.ErrorKeys;
import com.sslcommerz.library.payment.Util.ConstantData.SdkCategory;
import com.sslcommerz.library.payment.Util.ConstantData.SdkType;
import com.sslcommerz.library.payment.Util.JsonModel.TransactionInfo;
import com.sslcommerz.library.payment.Util.Model.MandatoryFieldModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.adapter.CheckoutPagerAdapter;
import tech.codegarage.tidetwist.base.BaseLocationActivity;
import tech.codegarage.tidetwist.enumeration.PaymentStatusType;
import tech.codegarage.tidetwist.fragment.CheckoutDeliveryInfoFragment;
import tech.codegarage.tidetwist.fragment.CheckoutFoodFragment;
import tech.codegarage.tidetwist.fragment.CheckoutPaymentFragment;
import tech.codegarage.tidetwist.geocoding.LocationAddressListener;
import tech.codegarage.tidetwist.geocoding.ReverseGeocoderTask;
import tech.codegarage.tidetwist.geocoding.UserLocationAddress;
import tech.codegarage.tidetwist.model.AppUser;
import tech.codegarage.tidetwist.model.DoOrder;
import tech.codegarage.tidetwist.model.FoodItem;
import tech.codegarage.tidetwist.model.ParamDoOrder;
import tech.codegarage.tidetwist.model.ParamDoOrderItem;
import tech.codegarage.tidetwist.retrofit.APIClient;
import tech.codegarage.tidetwist.retrofit.APIInterface;
import tech.codegarage.tidetwist.retrofit.APIResponse;
import tech.codegarage.tidetwist.util.AllConstants;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.Logger;
import tech.codegarage.tidetwist.view.CanaroTextView;
import tech.codegarage.tidetwist.view.LockableViewPager;

import static tech.codegarage.tidetwist.util.AllConstants.TRANSACTION_MAXIMUM_RANDOM_NUMBER;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class CheckoutActivity extends BaseLocationActivity {

    //Toolbar
    private ImageView ivBack;
    private CanaroTextView tvTitle;

    private LockableViewPager vpCheckout;
    private CheckoutPagerAdapter checkoutPagerAdapter;
    private StepperIndicator stepperIndicator;
    private List<FoodItem> mSelectedFoodItem = new ArrayList<FoodItem>();
    private String locationAddress = "";
    private ParamDoOrder mParamDoOrder = new ParamDoOrder();
    private AppUser mAppUser;
    private String mKitchenId = "";
    private boolean isSuccessfulPayment = false;

    //stepper indicator
    private Button btnOrder;
    private ImageView btnPrevious, btnNext;

    //Background task
    private ReverseGeocoderTask currentLocationTask;
    private APIInterface mApiInterface;
    private DoOrderTask doOrderTask;

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
        return R.layout.activity_checkout;
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
        if (intent != null) {
            String mParcelable = intent.getStringExtra(AllConstants.INTENT_KEY_KITCHEN_ID);
            if (!AllSettingsManager.isNullOrEmpty(mParcelable)) {
                mKitchenId = mParcelable;
                Logger.d(TAG, TAG + " >>> " + "mKitchenId: " + mKitchenId);
            }
        }
    }

    @Override
    public void initActivityViews() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (CanaroTextView) findViewById(R.id.tv_title);

        vpCheckout = (LockableViewPager) findViewById(R.id.vp_checkout);
        stepperIndicator = (StepperIndicator) findViewById(R.id.stepper_indicator_checkout);
        btnPrevious = (ImageView) findViewById(R.id.btn_skip);
        btnOrder = (Button) findViewById(R.id.btn_finish);
        btnNext = (ImageView) findViewById(R.id.btn_next);
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {
        mApiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        String appUser = SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_USER);
        if (!AllSettingsManager.isNullOrEmpty(appUser)) {
            mAppUser = APIResponse.getResponseObject(appUser, AppUser.class);
            Logger.d(TAG, TAG + " >>> " + "mAppUser: " + mAppUser.toString());
        }

        tvTitle.setText(getString(R.string.title_activity_checkout));
        checkoutPagerAdapter = new CheckoutPagerAdapter(getSupportFragmentManager());
        vpCheckout.setAdapter(checkoutPagerAdapter);
        vpCheckout.setSwipable(false);
        stepperIndicator.setViewPager(vpCheckout, true);
    }

    @Override
    public void initActivityActions(Bundle savedInstanceState) {
        ivBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                initActivityBackPress();
            }
        });

        vpCheckout.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //this variable is needed for the very first time viewpage fragment selection
            boolean first = true;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (first && positionOffset == 0 && positionOffsetPixels == 0) {
                    onPageSelected(0);
                    first = false;
                }
                Logger.d(TAG, "onPageScrolled: " + "position: " + position + "positionOffset: " + positionOffset + "positionOffsetPixels: " + positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                Logger.d(TAG, "onPageSelected: " + "position: " + position);
                int lastPagePosition = checkoutPagerAdapter.getCount() - 1;
                btnNext.setVisibility(position == lastPagePosition ? View.GONE : View.VISIBLE);
                btnPrevious.setVisibility((position == 0) ? View.GONE : View.VISIBLE);
                btnOrder.setVisibility(position == lastPagePosition ? View.VISIBLE : View.GONE);
//                if (position == lastPagePosition) {
//                    flFooter.setBackgroundColor(getResources().getColor((R.color.colorPrimaryDark)));
//                } else {
//                    flFooter.setBackgroundColor(getResources().getColor((android.R.color.transparent)));
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Logger.d(TAG, "onPageScrollStateChanged: " + "state: " + state);
            }
        });
        btnNext.setOnClickListener(new OnBaseClickListener() {
            @Override
            public void OnPermissionValidation(View view) {
                boolean isInLastPage = vpCheckout.getCurrentItem() == checkoutPagerAdapter.getCount() - 1;
                if (!isInLastPage) {
                    if (isScreenVerified(vpCheckout.getCurrentItem())) {
                        vpCheckout.setCurrentItem(vpCheckout.getCurrentItem() + 1);
                    }
                }
            }
        });
        btnPrevious.setOnClickListener(new OnBaseClickListener() {
            @Override
            public void OnPermissionValidation(View view) {
                boolean isFirstPage = vpCheckout.getCurrentItem() == 0;
                if (!isFirstPage) {
                    vpCheckout.setCurrentItem(vpCheckout.getCurrentItem() - 1);
                }
            }
        });
        btnOrder.setOnClickListener(new OnBaseClickListener() {
            @Override
            public void OnPermissionValidation(View view) {
                boolean isInLastPage = vpCheckout.getCurrentItem() == checkoutPagerAdapter.getCount() - 1;
                if (isInLastPage) {
                    if (!NetworkManager.isConnected(getActivity())) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    CheckoutPaymentFragment checkoutPaymentFragment = (CheckoutPaymentFragment) checkoutPagerAdapter.getRegisteredFragment(2);
                    if (checkoutPaymentFragment != null) {
                        //Update params for order api
                        mParamDoOrder.setSubtotal(checkoutPaymentFragment.getSubtotal());
                        mParamDoOrder.setIs_promotional_offer(checkoutPaymentFragment.isPromCodeAccepted() + "");
                        mParamDoOrder.setPromotional_discount_percentage(checkoutPaymentFragment.getPromotionalDiscountPercentage() + "");
                        mParamDoOrder.setShipping_cost(checkoutPaymentFragment.getShippingCost());
                        mParamDoOrder.setGrand_total(checkoutPaymentFragment.getTotal());

                        //Check payment amount
                        float totalAmount = Float.parseFloat(mParamDoOrder.getGrand_total());
                        if (totalAmount >= 100) {
                            //Check payment type
                            if (checkoutPaymentFragment.isPaymentSelected()) {
                                doPayment(totalAmount);
                            } else {
                                //Set payment status
                                mParamDoOrder.setTransaction_id("");
                                mParamDoOrder.setPayment_status(PaymentStatusType.CASH_ON_DELIVERY.name());

                                //Send order to the server through api
                                doOrderTask = new DoOrderTask(getActivity(), mParamDoOrder);
                                doOrderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.toast_you_need_to_order_atleast_100_tk), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private boolean isScreenVerified(int position) {
        Logger.d(TAG, TAG + ">>" + "position is " + position);
        switch (checkoutPagerAdapter.getScreenType(position)) {
            case FOODS:
                CheckoutFoodFragment checkoutFoodFragment = (CheckoutFoodFragment) checkoutPagerAdapter.getRegisteredFragment(position);
                if (checkoutFoodFragment != null) {
                    if (checkoutFoodFragment.isAllFieldsVerified()) {
                        Logger.d(TAG, TAG + ">>" + "food items are verified.");

                        //Update params for order api
                        List<FoodItem> foodItems = AppUtil.getAllStoredFoodItems(getActivity());
                        List<ParamDoOrderItem> items = new ArrayList<>();
                        for (FoodItem foodItem : foodItems) {
                            ParamDoOrderItem paramDoOrderItem = new ParamDoOrderItem(foodItem.getProduct_id(), foodItem.getItem_quantity() + "", foodItem.getPrice());
                            items.add(paramDoOrderItem);
                        }
                        mParamDoOrder.setOrder_id("0");//Here 0 is for insert new order, otherwise update
                        mParamDoOrder.setManufacturer_id(mKitchenId);
                        mParamDoOrder.setApp_user_id(mAppUser.getApp_user_id());
                        mParamDoOrder.setTotal_vat("0.00");
                        mParamDoOrder.setItems(items);
                        Logger.d(TAG, TAG + ">>" + "mParamDoOrder(CheckoutFoodFragment) = " + mParamDoOrder.toString());

                        return true;
                    } else {
                        Logger.d(TAG, TAG + ">>" + "food items are not verified.");
                    }
                } else {
                    Logger.d(TAG, TAG + ">>" + "food fragment is null.");
                }
                return false;
            case DELIVERY_INFO:
                CheckoutDeliveryInfoFragment checkoutDeliveryInfoFragment = (CheckoutDeliveryInfoFragment) checkoutPagerAdapter.getRegisteredFragment(position);
                if (checkoutDeliveryInfoFragment != null) {
                    if (checkoutDeliveryInfoFragment.isAllFieldsVerified()) {
                        Logger.d(TAG, TAG + ">>" + "address is verified.");

                        //Update params for order api
                        mParamDoOrder.setDelivery_address(checkoutDeliveryInfoFragment.getUserAddress());
                        mParamDoOrder.setDelivery_time(checkoutDeliveryInfoFragment.getDeliveryTime());
                        mParamDoOrder.setDelivery_phone(checkoutDeliveryInfoFragment.getUserPhone());
                        mParamDoOrder.setDelivery_name(checkoutDeliveryInfoFragment.getUserName());
                        mParamDoOrder.setDelivery_email(checkoutDeliveryInfoFragment.getUserEmail());
                        Logger.d(TAG, TAG + ">>" + "mParamDoOrder(CheckoutDeliveryInfoFragment) = " + mParamDoOrder.toString());

                        return true;
                    } else {
                        Logger.d(TAG, TAG + ">>" + "address is not verified.");
                    }
                } else {
                    Logger.d(TAG, TAG + ">>" + "address fragment is null.");
                }
                return false;
            case PAYMENT:
                return false;
        }
        return false;
    }

    @Override
    public void onLocationFound(Location location) {
        if (location != null) {
            if ((currentLocationTask != null) && (currentLocationTask.getStatus() == AsyncTask.Status.RUNNING)) {
                currentLocationTask.cancel(true);
            }
            currentLocationTask = new ReverseGeocoderTask(getActivity(), new LocationAddressListener() {
                @Override
                public void getLocationAddress(UserLocationAddress userLocationAddress) {
                    if (userLocationAddress != null) {
                        Logger.d(TAG, "UserLocationAddress: " + userLocationAddress.toString());
                        locationAddress = userLocationAddress.getAddressLine();

                        //send response to the desired fragment
                        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
                        if (fragmentList != null) {
                            for (Fragment fragment : fragmentList) {
                                if (fragment instanceof CheckoutDeliveryInfoFragment) {
                                    ((CheckoutDeliveryInfoFragment) fragment).setUserCurrentAddress(locationAddress);
                                }
                            }
                        }
                    }
                }
            });
            currentLocationTask.execute(location);
        }
    }

    public String locationAddress() {
        return locationAddress;
    }

    @Override
    public void initActivityOnResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public void initActivityBackPress() {
        //Cancel checkout and finish activity
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void initActivityDestroyTasks() {
        dismissProgressDialog();

        if (currentLocationTask != null && currentLocationTask.getStatus() == AsyncTask.Status.RUNNING) {
            currentLocationTask.cancel(true);
        }
        if (doOrderTask != null && doOrderTask.getStatus() == AsyncTask.Status.RUNNING) {
            doOrderTask.cancel(true);
        }
    }

    @Override
    public void initActivityPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    /************************
     * Server communication *
     ************************/
    private class DoOrderTask extends AsyncTask<String, Integer, Response> {

        Context mContext;
        ParamDoOrder mParamDoOrder;

        private DoOrderTask(Context context, ParamDoOrder paramDoOrder) {
            mContext = context;
            mParamDoOrder = paramDoOrder;
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

            if (isSuccessfulPayment) {
                //This is shown while user place order by payment
                showRetryDialog();
            }
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Logger.d(TAG, TAG + ">>" + "APIResponse(DoOrderListTask): mParamDoOrder = " + mParamDoOrder.toString());
                Call<APIResponse<List<DoOrder>>> call = mApiInterface.apiDoOrder(mParamDoOrder);
                Logger.d(TAG, "APIResponse(DoOrderListTask): call = " + call.toString() + "");

                Response response = call.execute();
                Logger.d(TAG, "APIResponse(DoOrderListTask): paramOrder = " + response);
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
                    Logger.d(TAG, "APIResponse(DoOrderListTask): onResponse-server = " + result.toString());
                    APIResponse<List<DoOrder>> data = (APIResponse<List<DoOrder>>) result.body();
                    Logger.d(TAG, "APIResponse(DoOrderListTask): data = " + data.toString() + "");

                    if (data != null && data.getStatus().equalsIgnoreCase("1")) {
                        Logger.d(TAG, "APIResponse(DoOrderListTask()): onResponse-object = " + data.toString());
                        isSuccessfulPayment = false;

                        //After successful placement of order remove selected data from database
                        AppUtil.deleteAllStoredFoodItems(getActivity());

                        //Finish the activity and send result
                        Intent intent = new Intent();
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } else {
                        if (isSuccessfulPayment) {
                            //This is shown while user place order by payment
                            showRetryDialog();
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_info_found), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (isSuccessfulPayment) {
                        //This is shown while user place order by payment
                        showRetryDialog();
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    /***************
     * SSL payment *
     ***************/
    private void doPayment(float grandTotal) {
        int rand = RandomManager.getRandom(TRANSACTION_MAXIMUM_RANDOM_NUMBER);
        final String transactionId = "trans_" + rand;
        String storeId = "", storePassword = "", sdkType = "";
        if (AppUtil.isDebug(getActivity())) {
            storeId = AllConstants.D_S_I;
            storePassword = AllConstants.D_S_P;
            sdkType = SdkType.TESTBOX;
        } else {
            storeId = AllConstants.R_S_I;
            storePassword = AllConstants.R_S_P;
            sdkType = SdkType.LIVE;
        }
        MandatoryFieldModel mandatoryFieldModel = new MandatoryFieldModel(storeId, storePassword, grandTotal + "", transactionId, CurrencyType.BDT, sdkType, SdkCategory.BANK_LIST);
        Logger.d(TAG, TAG + ">> storeId: " + storeId);
        Logger.d(TAG, TAG + ">> storePassword: " + storePassword);
        Logger.d(TAG, TAG + ">> sdkType: " + sdkType);
        Logger.d(TAG, TAG + ">> mandatoryFieldModel: " + mandatoryFieldModel.toString());

        PayUsingSSLCommerz.getInstance().setData(getActivity(), mandatoryFieldModel, new OnPaymentResultListener() {
            @Override
            public void transactionSuccess(TransactionInfo transactionInfo) {
                Logger.d(TAG, TAG + ">>transactionInfo: " + "\n"
                        + "transactionInfo.getStatus(): " + transactionInfo.getStatus() + "\n"
                        + "transactionInfo.getSessionkey(): " + transactionInfo.getSessionkey() + "\n"
                        + "transactionInfo.getTranDate(): " + transactionInfo.getTranDate() + "\n"
                        + "transactionInfo.getTranId(): " + transactionInfo.getTranId() + "\n"
                        + "transactionInfo.getValId(): " + transactionInfo.getValId() + "\n"
                        + "transactionInfo.getAmount(): " + transactionInfo.getAmount() + "\n"
                        + "transactionInfo.getStoreAmount(): " + transactionInfo.getStoreAmount() + "\n"
                        + "transactionInfo.getBankTranId(): " + transactionInfo.getBankTranId() + "\n"
                        + "transactionInfo.getCardType(): " + transactionInfo.getCardType() + "\n"
                        + "transactionInfo.getCardNo(): " + transactionInfo.getCardNo() + "\n"
                        + "transactionInfo.getCardIssuer(): " + transactionInfo.getCardIssuer() + "\n"
                        + "transactionInfo.getCardBrand(): " + transactionInfo.getCardBrand() + "\n"
                        + "transactionInfo.getCardIssuerCountry(): " + transactionInfo.getCardIssuerCountry() + "\n"
                        + "transactionInfo.getCardIssuerCountryCode(): " + transactionInfo.getCardIssuerCountryCode() + "\n"
                        + "transactionInfo.getCurrencyType(): " + transactionInfo.getCurrencyType() + "\n"
                        + "transactionInfo.getCurrencyAmount(): " + transactionInfo.getCurrencyAmount() + "\n"
                        + "transactionInfo.getCurrencyRate(): " + transactionInfo.getCurrencyRate() + "\n"
                        + "transactionInfo.getBaseFair(): " + transactionInfo.getBaseFair() + "\n"
                        + "transactionInfo.getValueA(): " + transactionInfo.getValueA() + "\n"
                        + "transactionInfo.getValueB(): " + transactionInfo.getValueB() + "\n"
                        + "transactionInfo.getValueC(): " + transactionInfo.getValueC() + "\n"
                        + "transactionInfo.getValueD(): " + transactionInfo.getValueD() + "\n"
                        + "transactionInfo.getRiskTitle(): " + transactionInfo.getRiskTitle() + "\n"
                        + "transactionInfo.getRiskLevel(): " + transactionInfo.getRiskLevel() + "\n"
                        + "transactionInfo.getAPIConnect(): " + transactionInfo.getAPIConnect() + "\n"
                        + "transactionInfo.getValidatedOn(): " + transactionInfo.getValidatedOn() + "\n"
                        + "transactionInfo.getGwVersion(): " + transactionInfo.getGwVersion() + "\n"
                );

                // If payment is success and risk label is 0.
                if (transactionInfo.getRiskLevel().equals("0")) {
                    Logger.d(TAG, TAG + ">> Transaction Successfully completed");
                    isSuccessfulPayment = true;

                    //Check network
                    if (!NetworkManager.isConnected(getActivity())) {
                        showRetryDialog();
                        return;
                    }

                    //Set payment status
                    mParamDoOrder.setTransaction_id(transactionId);
                    mParamDoOrder.setPayment_status(PaymentStatusType.PAID.name());

                    //Send order to the server through api
                    doOrderTask();
                } else {
                    // Payment is success but payment is not complete yet. Card on hold now.
                    Logger.d(TAG, TAG + ">> Transaction in risk. Risk Title : " + transactionInfo.getRiskTitle());
                    isSuccessfulPayment = false;

                    dismissProgressDialog();
                    Toast.makeText(getActivity(), getString(R.string.toast_your_transaction_is_in_risk), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void transactionFail(TransactionInfo transactionInfo) {
                // Transaction failed
                Logger.d(TAG, TAG + ">> Transaction Fail");
                isSuccessfulPayment = false;
                Toast.makeText(getActivity(), getString(R.string.toast_your_transaction_fails), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void error(int errorCode) {
                String errorMessage = "";

                switch (errorCode) {
                    // Your provides information is not valid.
                    case ErrorKeys.USER_INPUT_ERROR:
                        Logger.d(TAG, TAG + ">> User Input Error");
                        errorMessage = getString(R.string.toast_ssl_user_input_error);
                        break;
                    // Internet is not connected.
                    case ErrorKeys.INTERNET_CONNECTION_ERROR:
                        Logger.d(TAG, TAG + ">> Internet Connection Error");
                        errorMessage = getString(R.string.toast_ssl_internet_connection_error);
                        break;
                    // Server is not giving valid data.
                    case ErrorKeys.DATA_PARSING_ERROR:
                        Logger.d(TAG, TAG + ">> Data Parsing Error");
                        errorMessage = getString(R.string.toast_ssl_data_parsing_error);
                        break;
                    // User press back button or canceled the transaction.
                    case ErrorKeys.CANCEL_TRANSACTION_ERROR:
                        Logger.d(TAG, TAG + ">> User Cancel The Transaction");
                        errorMessage = getString(R.string.toast_ssl_cancel_transaction_error);
                        break;
                    // Server is not responding.
                    case ErrorKeys.SERVER_ERROR:
                        Logger.d(TAG, TAG + ">> Server Error");
                        errorMessage = getString(R.string.toast_ssl_server_error);
                        break;
                    // For some reason network is not responding
                    case ErrorKeys.NETWORK_ERROR:
                        Logger.d(TAG, TAG + ">> Network Error");
                        errorMessage = getString(R.string.toast_ssl_network_error);
                        break;
                }

                isSuccessfulPayment = false;
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doOrderTask() {
        if (doOrderTask != null && doOrderTask.getStatus() == AsyncTask.Status.RUNNING) {
            doOrderTask.cancel(true);
        }
        doOrderTask = new DoOrderTask(getActivity(), mParamDoOrder);
        doOrderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showRetryDialog() {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
        dialogBuilder
                .withTitle(getString(R.string.dialog_message))
                .withMessage(getString(R.string.dialog_your_payment_is_successful_but_could_not_process_the_order_due_to_no_network))
                .withEffect(Effectstype.Newspager)
                .withDuration(700)
                .withButton1Text(getString(R.string.dialog_cancel))
                .withButton2Text(getString(R.string.dialog_retry))
                .isCancelableOnTouchOutside(true)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        doOrderTask();
                    }
                })
                .show();
    }
}