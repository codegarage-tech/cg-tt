package tech.codegarage.tidetwist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.library.util.AllSettingsManager;

import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.base.BaseUpdateListener;
import tech.codegarage.tidetwist.enumeration.FlavorType;
import tech.codegarage.tidetwist.model.DriverUser;
import tech.codegarage.tidetwist.model.KitchenUser;
import tech.codegarage.tidetwist.util.AllConstants;
import tech.codegarage.tidetwist.util.AppUtil;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class SplashActivity extends AppCompatActivity {

    private TextView tvAppVersion;
    private ImageView ivAppLogo, ivAppLogoFlavor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initStatusBar();
        initActivityViews();
        initActivityViewsData();
    }

    private void initStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
        StatusBarUtil.setTransparent(SplashActivity.this);
    }

    public void initActivityViews() {
        tvAppVersion = (TextView) findViewById(R.id.tv_app_version);
        ivAppLogo = (ImageView) findViewById(R.id.iv_app_logo);
        ivAppLogoFlavor = (ImageView) findViewById(R.id.iv_app_logo_flavor);
    }

    public void initActivityViewsData() {
        //Set app version
        String appVersion = AppUtil.getAppVersion(SplashActivity.this);
        if (!AllSettingsManager.isNullOrEmpty(appVersion)) {
            tvAppVersion.setText("Version: " + appVersion);
        }

        //Set flavor icon
        switch (FlavorType.getFlavor()) {
            case USER:
                ivAppLogoFlavor.setBackgroundResource(R.drawable.vector_user_empty_white);
                break;
            case KITCHEN:
                ivAppLogoFlavor.setBackgroundResource(R.drawable.vector_kitchen_empty_white);
                break;
            case DRIVER:
                ivAppLogoFlavor.setBackgroundResource(R.drawable.vector_driver_empty_white);
                break;
        }

        //Rotate app logo
        AppUtil.makeRotateAnimation(ivAppLogo, 3, new BaseUpdateListener() {
            @Override
            public void onUpdate(Object update) {
                if ((boolean) update) {
                    //Navigate to the next screen
                    navigateNextScreen();
                }
            }
        });
    }

    private void navigateNextScreen() {
        FlavorType flavorType = FlavorType.getFlavor();

        switch (flavorType) {
            case USER:
                Intent intentAppUser;
                if (AllSettingsManager.isNullOrEmpty(SessionManager.getStringSetting(SplashActivity.this, AllConstants.SESSION_KEY_USER))) {
                    intentAppUser = new Intent(SplashActivity.this, AppUserRegistrationActivity.class);
                } else {
                    intentAppUser = new Intent(SplashActivity.this, HomeActivity.class);
                }
                startActivity(intentAppUser);
                finish();
                break;
            case KITCHEN:
                Intent intentAppKitchen;
                KitchenUser kitchenUser = AppUtil.getKitchenUser(SplashActivity.this);
                if (kitchenUser != null && kitchenUser.getIs_approved().equalsIgnoreCase("1")) {
                    intentAppKitchen = new Intent(SplashActivity.this, HomeActivity.class);
                } else {
                    intentAppKitchen = new Intent(SplashActivity.this, KitchenLoginActivity.class);
                }
                startActivity(intentAppKitchen);
                finish();
                break;
            case DRIVER:
                Intent intentAppDriver;
                DriverUser driverUser = AppUtil.getDriverUser(SplashActivity.this);
                if (driverUser != null && driverUser.getIs_available().equalsIgnoreCase("1")) {
                    intentAppDriver = new Intent(SplashActivity.this, HomeActivity.class);
                } else {
                    intentAppDriver = new Intent(SplashActivity.this, DriverLoginActivity.class);
                }
                startActivity(intentAppDriver);
                finish();
                break;
        }
    }
}