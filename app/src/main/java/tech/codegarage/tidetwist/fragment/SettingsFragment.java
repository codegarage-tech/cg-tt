package tech.codegarage.tidetwist.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.eggheadgames.aboutbox.activity.AboutActivity;
import com.eggheadgames.aboutbox.listener.LicenseClickListener;
import com.github.zagum.switchicon.SwitchIconView;
import com.reversecoder.attributionpresenter.activity.LicenseActivity;
import com.reversecoder.library.event.OnSingleClickListener;

import tech.codegarage.fcm.util.FcmUtil;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.activity.AboutAppUserActivity;
import tech.codegarage.tidetwist.activity.DownloadAppActivity;
import tech.codegarage.tidetwist.activity.DriverRegistrationActivity;
import tech.codegarage.tidetwist.activity.KitchenRegistrationActivity;
import tech.codegarage.tidetwist.base.BaseFragment;
import tech.codegarage.tidetwist.enumeration.FlavorType;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class SettingsFragment extends BaseFragment {

    private RelativeLayout rlEditProfile, rlRegisterKitchen, rlRegisterDriver, rlAbout, rlDownloadApp, rlOrderNotification, rlAppNotification;
    private SwitchIconView switchAppNotification, switchOrderNotification;

    public static SettingsFragment newInstance() {
//        Bundle args = new Bundle();
//        args.putParcelable(INTENT_KEY_FOOD_CHECKOUT, Parcels.wrap(checkoutItem));
        SettingsFragment fragment = new SettingsFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int initFragmentLayout() {
        return R.layout.fragment_settings;
    }

    @Override
    public void initFragmentBundleData(Bundle bundle) {
//        if (bundle != null) {
//            Parcelable mParcelableCheckoutItem = bundle.getParcelable(INTENT_KEY_FOOD_CHECKOUT);
//            if (mParcelableCheckoutItem != null) {
//                mCheckoutItem = Parcels.unwrap(mParcelableCheckoutItem);
//                Logger.d(TAG, TAG + " >>> " + "mCheckoutItem: " + mCheckoutItem.toString());
//            }
//        }
    }

    @Override
    public void initFragmentViews(View parentView) {
        rlEditProfile = parentView.findViewById(R.id.rl_edit_profile);
        rlRegisterKitchen = parentView.findViewById(R.id.rl_register_kitchen);
        rlRegisterDriver = parentView.findViewById(R.id.rl_register_driver);
        rlAbout = parentView.findViewById(R.id.rl_about);
        rlDownloadApp = parentView.findViewById(R.id.rl_download_app);
        rlOrderNotification = parentView.findViewById(R.id.rl_order_notification);
        rlAppNotification = parentView.findViewById(R.id.rl_app_notification);
        switchOrderNotification = parentView.findViewById(R.id.switch_order_notification);
        switchAppNotification = parentView.findViewById(R.id.switch_app_notification);
    }

    @Override
    public void initFragmentViewsData() {
        //Invisible account for kitchen and driver
        switch (FlavorType.getFlavor()) {
            case USER:
                break;
            case KITCHEN:
                rlRegisterKitchen.setVisibility(View.GONE);
                break;
            case DRIVER:
                rlRegisterDriver.setVisibility(View.GONE);
                break;
        }

        //Set fcm notification settings
        if (FcmUtil.getIsOrderNotification(getActivity()) == 1) {
            switchOrderNotification.setIconEnabled(true);
        } else {
            switchOrderNotification.setIconEnabled(false);
        }
        if (FcmUtil.getIsAppNotification(getActivity()) == 1) {
            switchAppNotification.setIconEnabled(true);
        } else {
            switchAppNotification.setIconEnabled(false);
        }
    }

    @Override
    public void initFragmentActions() {
        rlEditProfile.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                switch (FlavorType.getFlavor()) {
                    case USER:
                        Intent intent = new Intent(getActivity(), AboutAppUserActivity.class);
                        startActivity(intent);
                        break;
                    case KITCHEN:
                        break;
                    case DRIVER:
                        break;
                }
            }
        });

        rlRegisterKitchen.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Intent intent = new Intent(getActivity(), KitchenRegistrationActivity.class);
                startActivity(intent);
            }
        });

        rlRegisterDriver.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Intent intent = new Intent(getActivity(), DriverRegistrationActivity.class);
                startActivity(intent);
            }
        });

        rlAbout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                AboutActivity.launch(getActivity());
                AboutActivity.setLicenseListener(new LicenseClickListener() {
                    @Override
                    public void onLicenseClick() {
                        Intent intentLicense = new Intent(getActivity(), LicenseActivity.class);
                        startActivity(intentLicense);
                    }
                });
            }
        });

        rlDownloadApp.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Intent intent = new Intent(getActivity(), DownloadAppActivity.class);
                startActivity(intent);
            }
        });

        rlOrderNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FcmUtil.getIsOrderNotification(getActivity()) == 1) {
                    FcmUtil.saveIsOrderNotification(getActivity(), 0);
                    switchOrderNotification.setIconEnabled(false);
                } else {
                    FcmUtil.saveIsOrderNotification(getActivity(), 1);
                    switchOrderNotification.setIconEnabled(true);
                }
            }
        });

        rlAppNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FcmUtil.getIsAppNotification(getActivity()) == 1) {
                    FcmUtil.saveIsAppNotification(getActivity(), 0);
                    switchAppNotification.setIconEnabled(false);
                } else {
                    FcmUtil.saveIsAppNotification(getActivity(), 1);
                    switchAppNotification.setIconEnabled(true);
                }
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
}