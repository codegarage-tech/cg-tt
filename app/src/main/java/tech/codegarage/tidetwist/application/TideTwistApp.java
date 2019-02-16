package tech.codegarage.tidetwist.application;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.aacreations.aacustomfont.AACustomFont;

import tech.codegarage.fcm.util.FcmManager;
import tech.codegarage.tidetwist.activity.OrderDetailsActivity;
import tech.codegarage.tidetwist.enumeration.FlavorType;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.Logger;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class TideTwistApp extends Application {

    private static Context mContext;
    private static final String CANARO_EXTRA_BOLD_PATH = "fonts/canaro_extra_bold.otf";
    public static Typeface canaroExtraBold;

    @Override
    public void onCreate() {
        super.onCreate();

        if (mContext == null) {
            mContext = this;
        }

        //Initialize logger
        new Logger.Builder()
                .isLoggable(AppUtil.isDebug(mContext))
                .build();

        //For using vector drawable
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        //Initialize font
        initTypeface();
        AACustomFont.getInstance(mContext)
                .setAlias("myFont", "canaro_extra_bold.otf")
                .setDefaultFontName("myFont");

        //Multidex initialization
        MultiDex.install(this);

        //Initialize FCM content class
        FcmManager.initFcmManager(mContext, FlavorType.getFlavor().name(), new FcmManager.FcmBuilder().setOrderContentClass(OrderDetailsActivity.class).buildGcmManager());
    }

    private void initTypeface() {
        canaroExtraBold = Typeface.createFromAsset(getAssets(), CANARO_EXTRA_BOLD_PATH);
    }

    public static Context getGlobalContext() {
        return mContext;
    }
}