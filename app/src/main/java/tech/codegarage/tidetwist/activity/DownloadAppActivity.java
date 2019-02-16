package tech.codegarage.tidetwist.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.eggheadgames.aboutbox.AboutBoxUtils;
import com.eggheadgames.aboutbox.AboutConfig;
import com.jaeger.library.StatusBarUtil;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.segmentradiogroup.SegmentedRadioGroup;

import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.base.BaseActivity;
import tech.codegarage.tidetwist.enumeration.FlavorType;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.IntentManager;
import tech.codegarage.tidetwist.view.CanaroTextView;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class DownloadAppActivity extends BaseActivity {

    //Toolbar
    private ImageView ivBack, ivMenu;
    private CanaroTextView tvTitle;
    private LinearLayout llMenu;

    private ImageView ivQRCode;
    private TextView tvPlayStoreUrl;
    private String packageName = "", playStoreUrl;
    private SegmentedRadioGroup srgFlavorType;
    private RadioButton srgRbtnUser, srgRbtnKitchen, srgRbtnDriver;

    @Override
    public String[] initActivityPermissions() {
        return new String[]{};
    }

    @Override
    public int initActivityLayout() {
        return R.layout.activity_download_app;
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
        tvTitle.setText(getString(R.string.title_activity_download_app));
        llMenu = (LinearLayout) findViewById(R.id.ll_menu);
        llMenu.setVisibility(View.VISIBLE);
        ivMenu = (ImageView) findViewById(R.id.iv_menu);
        ivMenu.setBackgroundResource(R.drawable.vector_share_empty_white);

        ivQRCode = (ImageView) findViewById(R.id.iv_qr_code);
        tvPlayStoreUrl = (TextView) findViewById(R.id.tv_play_store_url);
        srgFlavorType = (SegmentedRadioGroup) findViewById(R.id.srg_flavor_type);
        srgRbtnUser = (RadioButton) findViewById(R.id.srg_rbtn_user);
        srgRbtnKitchen = (RadioButton) findViewById(R.id.srg_rbtn_kitchen);
        srgRbtnDriver = (RadioButton) findViewById(R.id.srg_rbtn_driver);
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {
        AppUtil.doMarqueeTextView(tvTitle);

        setQRCode();
    }

    @Override
    public void initActivityActions(Bundle savedInstanceState) {
        ivBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                initActivityBackPress();
            }
        });

        llMenu.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                IntentManager.shareToAllAvailableApps(getActivity(), getString(R.string.view_share_with), "", playStoreUrl);
            }
        });

        srgFlavorType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setQRCode();
            }
        });

        tvPlayStoreUrl.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                AboutBoxUtils.openApp(getActivity(), AboutConfig.BuildType.GOOGLE, packageName);
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

    }

    @Override
    public void initActivityPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    private void setQRCode() {
        switch (srgFlavorType.getCheckedRadioButtonId()) {
            case R.id.srg_rbtn_user:
                packageName = getString(R.string.view_base_package_name) + "." + FlavorType.USER.toString().toLowerCase();
                break;
            case R.id.srg_rbtn_kitchen:
                packageName = getString(R.string.view_base_package_name) + "." + FlavorType.KITCHEN.toString().toLowerCase();
                break;
            case R.id.srg_rbtn_driver:
                packageName = getString(R.string.view_base_package_name) + "." + FlavorType.DRIVER.toString().toLowerCase();
                break;
        }

        playStoreUrl = String.format(getString(R.string.app_play_store_url), packageName);
        AppUtil.generateQRCode(playStoreUrl, ivQRCode, 250, 250);

        tvPlayStoreUrl.setText(playStoreUrl);
        tvPlayStoreUrl.setPaintFlags(tvPlayStoreUrl.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }
}