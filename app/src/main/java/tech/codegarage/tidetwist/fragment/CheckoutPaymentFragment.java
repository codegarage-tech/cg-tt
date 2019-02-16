package tech.codegarage.tidetwist.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.segmentradiogroup.SegmentedRadioGroup;
import com.reversecoder.library.util.AllSettingsManager;

import retrofit2.Call;
import retrofit2.Response;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.activity.CheckoutActivity;
import tech.codegarage.tidetwist.base.BaseFragment;
import tech.codegarage.tidetwist.model.ParamPromoCode;
import tech.codegarage.tidetwist.retrofit.APIClient;
import tech.codegarage.tidetwist.retrofit.APIInterface;
import tech.codegarage.tidetwist.retrofit.APIResponse;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.Logger;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class CheckoutPaymentFragment extends BaseFragment {

    private CardView cvPromoCode;
    private ImageView ivPromoCodeAccepted;
    private TextView tvPaymentTypeMessage, tvSubtotal, tvPromotionalDiscount, tvShippingCost, tvTotal;
    private RelativeLayout rlPromotionalDiscountPanel;
    private SegmentedRadioGroup srgPaymentType;
    private RadioButton srgRbtnPaymentOnDelivery, srgRbtnInstantPayment;
    private String mPromoCode = "";
    private int isPromoCodeAccepted = 0;
    private float promotional_discount_percentage = 0.0f;

    //Background task
    private APIInterface mApiInterface;
    private CheckPromoCodeTask checkPromoCodeTask;

    public static CheckoutPaymentFragment newInstance() {
        CheckoutPaymentFragment fragment = new CheckoutPaymentFragment();
        return fragment;
    }

    @Override
    public int initFragmentLayout() {
        return R.layout.fragment_checkout_payment;
    }

    @Override
    public void initFragmentBundleData(Bundle bundle) {

    }

    @Override
    public void initFragmentViews(View parentView) {
        srgPaymentType = (SegmentedRadioGroup) parentView.findViewById(R.id.srg_payment_type);
        srgRbtnInstantPayment = (RadioButton) parentView.findViewById(R.id.srg_rbtn_instant_payment);
        srgRbtnPaymentOnDelivery = (RadioButton) parentView.findViewById(R.id.srg_rbtn_payment_on_delivery);
        tvPaymentTypeMessage = (TextView) parentView.findViewById(R.id.tv_payment_type_message);
        cvPromoCode = (CardView) parentView.findViewById(R.id.cv_promo_code);
        ivPromoCodeAccepted = (ImageView) parentView.findViewById(R.id.iv_promo_code_accepted);
        tvSubtotal = (TextView) parentView.findViewById(R.id.tv_subtotal);
        tvShippingCost = (TextView) parentView.findViewById(R.id.tv_shipping_cost);
        tvTotal = (TextView) parentView.findViewById(R.id.tv_total);
        tvPromotionalDiscount = (TextView) parentView.findViewById(R.id.tv_promotional_discount);
        rlPromotionalDiscountPanel = (RelativeLayout) parentView.findViewById(R.id.rl_promotional_discount_panel);
    }

    @Override
    public void initFragmentViewsData() {
        mApiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        if (srgRbtnInstantPayment.isChecked()) {
            tvPaymentTypeMessage.setText(R.string.view_i_want_pay_now_by_my_desired_payment_method);
        } else {
            tvPaymentTypeMessage.setText(R.string.view_after_getting_delivery_i_will_give_cash_to_the_delivery_man);
        }

        //Set chosen item price
        setPriceInformation(0.0f);
    }

    private void setPriceInformation(float promotionalDiscount) {
        float subtotal = AppUtil.formatFloat(AppUtil.getSubtotalPrice(getActivity()));
        float shippingCost = AppUtil.formatFloat(Float.parseFloat(getString(R.string.view_sixty)));
        float total = AppUtil.getTotalPrice(subtotal, promotionalDiscount, shippingCost);
        tvSubtotal.setText(subtotal + " " + getString(R.string.view_tk));
        tvPromotionalDiscount.setText(getString(R.string.view_first_bracket_left) + promotionalDiscount + getString(R.string.view_percentage) + getString(R.string.view_first_bracket_right) + AppUtil.getPromotionalDiscountPrice(subtotal, promotionalDiscount) + " " + getString(R.string.view_tk));
        tvShippingCost.setText(shippingCost + " " + getString(R.string.view_tk));
        tvTotal.setText(total + " " + getString(R.string.view_tk));
    }

    @Override
    public void initFragmentActions() {
        srgPaymentType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.srg_rbtn_instant_payment:
                        tvPaymentTypeMessage.setText(R.string.view_i_want_pay_now_by_my_desired_payment_method);
                        break;
                    case R.id.srg_rbtn_payment_on_delivery:
                        tvPaymentTypeMessage.setText(R.string.view_after_getting_delivery_i_will_give_cash_to_the_delivery_man);
                        break;
                }
            }
        });

        cvPromoCode.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
                dialogBuilder
                        .withTitle(getString(R.string.dialog_enter_promo_code))
                        .withEffect(Effectstype.Newspager)
                        .withDuration(700)
                        .withButton1Text(getString(R.string.dialog_cancel))
                        .withButton2Text(getString(R.string.dialog_ok))
                        .isCancelableOnTouchOutside(true)
                        .setCustomView(R.layout.dialog_promo_code, getActivity())
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogBuilder.dismiss();
                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPromoCode = ((EditText) dialogBuilder.getCustomView().findViewById(R.id.edt_promo_code)).getText().toString();

                                if (AllSettingsManager.isNullOrEmpty(mPromoCode)) {
                                    Toast.makeText(getActivity(), getString(R.string.toast_please_input_promo_code), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (!NetworkManager.isConnected(getActivity())) {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                //Dismiss dialog
                                dialogBuilder.dismiss();

                                //Apply promo code
                                checkPromoCodeTask = new CheckPromoCodeTask(getActivity(), mPromoCode);
                                checkPromoCodeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        })
                        .show();

                //Set previous input
                ((EditText) dialogBuilder.getCustomView().findViewById(R.id.edt_promo_code)).setText(mPromoCode);
            }
        });
    }

    @Override
    public void initFragmentBackPress() {

    }

    @Override
    public void initFragmentOnResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void initFragmentUpdate(Object object) {

    }

    /***********************************************
     * Getter methods for accessing fragments data *
     ***********************************************/
    public boolean isPaymentSelected() {
        return srgRbtnInstantPayment.isChecked();
    }

    public int isPromCodeAccepted() {
        return isPromoCodeAccepted;
    }

    public String getSubtotal() {
        return tvSubtotal.getText().toString().trim().substring(0, tvSubtotal.getText().toString().length() - 2).trim();
    }

    public String getShippingCost() {
        return tvShippingCost.getText().toString().trim().substring(0, tvShippingCost.getText().toString().length() - 2).trim();
    }

    public String getTotal() {
        return tvTotal.getText().toString().trim().substring(0, tvTotal.getText().toString().length() - 2).trim();
    }

    public float getPromotionalDiscountPercentage() {
        return promotional_discount_percentage;
    }

    /************************
     * Server communication *
     ************************/
    private class CheckPromoCodeTask extends AsyncTask<String, Integer, Response> {

        Context mContext;
        String mPromoCode;

        public CheckPromoCodeTask(Context context, String promoCode) {
            mContext = context;
            mPromoCode = promoCode;
        }

        @Override
        protected void onPreExecute() {
            ((CheckoutActivity) getActivity()).showProgressDialog();
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                ParamPromoCode paramPromoCode = new ParamPromoCode(mPromoCode);
                Logger.d(TAG, "APIResponse(CheckPromoCodeTask): paramPromoCode = " + paramPromoCode.toString() + "");

                Call<APIResponse> call = mApiInterface.apiCheckPromoCode(paramPromoCode);
                Logger.d(TAG, "APIResponse(CheckPromoCodeTask): call = " + call.toString() + "");

                Response response = call.execute();
                Logger.d(TAG, "APIResponse(CheckPromoCodeTask): response = " + response + "");
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
                ((CheckoutActivity) getActivity()).dismissProgressDialog();

                if (result != null && result.isSuccessful()) {
                    Logger.d(TAG, "APIResponse(CheckPromoCodeTask): onResponse-server = " + result.toString());
                    APIResponse data = (APIResponse) result.body();
                    Logger.d(TAG, "APIResponse(CheckPromoCodeTask): data = " + data.toString() + "");

                    if (data != null) {
                        Logger.d(TAG, "APIResponse(CheckPromoCodeTask()): onResponse-object = " + data.toString());
                        ivPromoCodeAccepted.setVisibility(View.VISIBLE);

                        if (data.getStatus().equalsIgnoreCase("1")) {
                            ivPromoCodeAccepted.setBackgroundResource(R.drawable.vector_accepted);
                            isPromoCodeAccepted = 1;
                            rlPromotionalDiscountPanel.setVisibility(View.VISIBLE);
                            promotional_discount_percentage = 10.0f;
                        } else {
                            ivPromoCodeAccepted.setBackgroundResource(R.drawable.vector_canceled);
                            isPromoCodeAccepted = 0;
                            rlPromotionalDiscountPanel.setVisibility(View.GONE);
                            promotional_discount_percentage = 0.0f;
                        }

                        //Set chosen price with promotional discount
                        setPriceInformation(promotional_discount_percentage);

                        Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
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