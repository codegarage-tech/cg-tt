package tech.codegarage.tidetwist.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eggheadgames.aboutbox.AboutConfig;
import com.eggheadgames.aboutbox.IAnalytic;
import com.eggheadgames.aboutbox.IDialog;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.jaeger.library.StatusBarUtil;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.library.util.AllSettingsManager;

import org.parceler.Parcels;

import java.util.List;

import io.armcha.ribble.presentation.navigationview.NavigationId;
import io.armcha.ribble.presentation.navigationview.NavigationItem;
import io.armcha.ribble.presentation.navigationview.adapter.NavigationViewAdapter;
import io.armcha.ribble.presentation.navigationview.adapter.RecyclerArrayAdapter;
import io.armcha.ribble.presentation.utils.extensions.ViewExKt;
import io.armcha.ribble.presentation.widget.AnimatedImageView;
import io.armcha.ribble.presentation.widget.AnimatedTextView;
import retrofit2.Call;
import retrofit2.Response;
import tech.codegarage.fcm.payload.Payload;
import tech.codegarage.fcm.util.FcmUtil;
import tech.codegarage.fcm.util.OnTokenUpdateListener;
import tech.codegarage.fcm.util.TokenFetcher;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.base.BaseLocationActivity;
import tech.codegarage.tidetwist.base.BaseResultReceiver;
import tech.codegarage.tidetwist.enumeration.FlavorType;
import tech.codegarage.tidetwist.enumeration.KitchenType;
import tech.codegarage.tidetwist.fragment.HomeFragment;
import tech.codegarage.tidetwist.fragment.NotificationListFragment;
import tech.codegarage.tidetwist.fragment.OrderListFragment;
import tech.codegarage.tidetwist.fragment.SettingsFragment;
import tech.codegarage.tidetwist.geocoding.LocationAddressListener;
import tech.codegarage.tidetwist.geocoding.ReverseGeocoderTask;
import tech.codegarage.tidetwist.geocoding.UserLocationAddress;
import tech.codegarage.tidetwist.model.AppUser;
import tech.codegarage.tidetwist.model.DriverUser;
import tech.codegarage.tidetwist.model.KitchenUser;
import tech.codegarage.tidetwist.model.Order;
import tech.codegarage.tidetwist.model.ParamAppUser;
import tech.codegarage.tidetwist.model.ParamDriverLogout;
import tech.codegarage.tidetwist.model.ParamDriverRegistration;
import tech.codegarage.tidetwist.model.ParamKitchenLogout;
import tech.codegarage.tidetwist.model.ParamKitchenRegistration;
import tech.codegarage.tidetwist.retrofit.APIClient;
import tech.codegarage.tidetwist.retrofit.APIInterface;
import tech.codegarage.tidetwist.retrofit.APIResponse;
import tech.codegarage.tidetwist.service.DriverJobIntentService;
import tech.codegarage.tidetwist.util.AllConstants;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.FragmentUtilsManager;
import tech.codegarage.tidetwist.util.Logger;

import static tech.codegarage.tidetwist.base.BaseJobIntentService.KEY_TASK_RESULT;
import static tech.codegarage.tidetwist.util.AllConstants.SESSION_KEY_DRIVER;
import static tech.codegarage.tidetwist.util.AllConstants.SESSION_KEY_KITCHEN;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class HomeActivity extends BaseLocationActivity {

    //Navigation drawer view
    private String TRANSLATION_X_KEY = "TRANSLATION_X_KEY";
    private String CARD_ELEVATION_KEY = "CARD_ELEVATION_KEY";
    private String SCALE_KEY = "SCALE_KEY";
    private NavigationViewAdapter navViewAdapter;
    private RecyclerView navRecyclerView;
    private NavigationView navView;
    private DrawerLayout drawerLayout;
    private CardView contentHome;
    private AnimatedImageView arcMenuImage;
    private AnimatedTextView toolbarTitle;
    private NavigationItem lastSelectedItem;

    //Header view
    private ImageView userAvatar;
    private TextView userName;
    private TextView userInfo;
    private AppUser mAppUser;
    private KitchenUser mKitchenUser;
    private DriverUser mDriverUser;

    //Background task
    private APIInterface mApiInterface;
    private ReverseGeocoderTask currentLocationTask;
    private UserLocationAddress mLocationAddress;
    private TokenFetcher tokenFetcher;
    private UpdateTokenTask updateTokenTask;
    private DoLogoutTask doLogoutTask;

    @Override
    public LOCATION_UPDATE_FREQUENCY initLocationUpdateFrequency() {
        return (FlavorType.getFlavor() != FlavorType.KITCHEN) ? LOCATION_UPDATE_FREQUENCY.FREQUENTLY : LOCATION_UPDATE_FREQUENCY.ONCE;
    }

    @Override
    public String[] initActivityPermissions() {
        return new String[]{};
    }

    @Override
    public int initActivityLayout() {
        return R.layout.activity_home;
    }

    @Override
    public void onLocationFound(Location location) {
        if (location != null) {

            //Check internet connection
            if (NetworkManager.isConnected(getActivity())) {
                switch (FlavorType.getFlavor()) {
                    case USER:
                        if (FragmentUtilsManager.getVisibleSupportFragment(HomeActivity.this, lastSelectedItem.getNavigationId().name()) instanceof HomeFragment) {
                            //Request reverse geocoding for address
                            if ((currentLocationTask != null) && (currentLocationTask.getStatus() == AsyncTask.Status.RUNNING)) {
                                currentLocationTask.cancel(true);
                            }

                            currentLocationTask = new ReverseGeocoderTask(getActivity(), new LocationAddressListener() {
                                @Override
                                public void getLocationAddress(UserLocationAddress locationAddress) {
                                    if (locationAddress != null) {
                                        mLocationAddress = locationAddress;
                                        Logger.d(TAG, "UserLocationAddress: " + mLocationAddress.toString());
//                                        String addressText = String.format("%s, %s, %s, %s", locationAddress.getStreetAddress(), locationAddress.getCity(), locationAddress.getState(), locationAddress.getCountry());

                                        //Set address to the toolbar
                                        setToolBarTitle(mLocationAddress.getAddressLine());
                                    }
                                }
                            });
                            currentLocationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, location);
                        }
                        break;
                    case DRIVER:
                        if (NetworkManager.isConnected(getActivity())) {
                            DriverJobIntentService.enqueueJob(getActivity(), DriverJobIntentService.class, location, DriverJobIntentService.DRIVER_JOB_TYPE.LOCATION_UPDATE.name(), new BaseResultReceiver.ResultReceiverCallBack() {
                                @Override
                                public void onResultReceived(int resultCode, Bundle resultData) {
                                    String responseString = resultData.getString(KEY_TASK_RESULT);
                                    if (!AllSettingsManager.isNullOrEmpty(responseString)) {
                                        APIResponse updateResponse = APIResponse.getResponseObject(responseString, APIResponse.class);
                                        if (updateResponse != null) {
                                            Logger.d(TAG, TAG + ">> DriverJobIntentService(updateResponse): " + updateResponse.toString());
                                        }
                                    }
                                }
                            });
                        } else {
                            Logger.d(TAG, TAG + ">> DriverJobIntentService: there is not internet, so driver service is not starting");
                        }
                        break;
                    case KITCHEN:
                        break;
                }
            }
        }
    }

    @Override
    public void initStatusBarView() {
        StatusBarUtil.setTransparent(getActivity());

//        Window window = getWindow();
//        WindowManager.LayoutParams winParams = window.getAttributes();
//        winParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        window.setAttributes(winParams);
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        //Set top margin of toolbar
//        setToolBarTopMargin(StatusBarUtil.getStatusBarHeight(getActivity()));
    }

    @Override
    public void initNavigationBarView() {

    }

    @Override
    public void initIntentData(Bundle savedInstanceState, Intent intent) {

    }

    @Override
    public void initActivityViews() {
        //toolbar view
        toolbarTitle = (AnimatedTextView) findViewById(R.id.toolbarTitle);
        arcMenuImage = (AnimatedImageView) findViewById(R.id.arcImage);

        navView = (NavigationView) findViewById(R.id.navigation_view);
        navRecyclerView = (RecyclerView) navView.findViewById(R.id.navigation_view_recycler_view);
        userAvatar = (ImageView) navView.findViewById(R.id.userAvatar);
        userName = (TextView) navView.findViewById(R.id.userName);
        userInfo = (TextView) navView.findViewById(R.id.userInfo);
        contentHome = (CardView) findViewById(R.id.mainView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {
        mApiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        //Set app user information
        updateUserInfo();

        //Remove selected food items if there any previous selected item
        AppUtil.deleteAllStoredFoodItems(getActivity());

        initNavigationDrawer();

        initAboutPage();

        initTokenForFCM();
    }

    @Override
    public void initActivityActions(Bundle savedInstanceState) {

    }

    @Override
    public void initActivityOnResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case AllConstants.INTENT_REQUEST_CODE_ORDER_ITEM_DETAIL:
                if (resultCode == Activity.RESULT_OK) {
                    if (intent != null) {
                        Parcelable mParcelableOrder = intent.getParcelableExtra(AllConstants.INTENT_KEY_ORDER_ITEM);

                        if (mParcelableOrder != null) {
                            //Got updated order data from order detail
                            Order mOrder = Parcels.unwrap(mParcelableOrder);
                            Logger.d(TAG, TAG + " >>> " + "mOrder: " + mOrder.toString());

                            //Updated order list fragment
                            OrderListFragment orderListFragment = (OrderListFragment) FragmentUtilsManager.getVisibleSupportFragment(HomeActivity.this, getString(R.string.ribble_menu_item_order));
                            if (orderListFragment != null) {
                                Logger.d(TAG, TAG + " >>> " + "Updating order list");
                                orderListFragment.updateOrderList(mOrder);
                            }
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void initActivityBackPress() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
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
        if (updateTokenTask != null && updateTokenTask.getStatus() == AsyncTask.Status.RUNNING) {
            updateTokenTask.cancel(true);
        }
        if (doLogoutTask != null && doLogoutTask.getStatus() == AsyncTask.Status.RUNNING) {
            doLogoutTask.cancel(true);
        }
    }

    @Override
    public void initActivityPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    public void onResume() {
        super.onResume();

        //Update app user information
        updateUserInfo();
    }

    private void updateUserInfo() {
        mAppUser = AppUtil.getAppUser(getActivity());
        mKitchenUser = AppUtil.getKitchenUser(getActivity());
        mDriverUser = AppUtil.getDriverUser(getActivity());

        switch (FlavorType.getFlavor()) {
            case USER:
                if (mAppUser != null) {
                    Logger.d(TAG, TAG + " >>> " + "mAppUser: " + mAppUser.toString());
                    AppUtil.loadImage(getActivity(), userAvatar, mAppUser.getImage(), false, true, false);
                    userName.setText(mAppUser.getName());
                }
                break;
            case KITCHEN:
                if (mKitchenUser != null) {
                    Logger.d(TAG, TAG + " >>> " + "mKitchenUser: " + mKitchenUser.toString());
                    AppUtil.loadImage(getActivity(), userAvatar, mKitchenUser.getImage(), false, true, false);
                    userName.setText(mKitchenUser.getName());
                }
                break;
            case DRIVER:
                if (mDriverUser != null) {
                    Logger.d(TAG, TAG + " >>> " + "mDriverUser: " + mDriverUser.toString());
                    AppUtil.loadImage(getActivity(), userAvatar, mDriverUser.getImage(), false, true, false);
                    userName.setText(mDriverUser.getFirst_name() + " " + mDriverUser.getLast_name());
                }
                break;
        }
    }

    /*****************************
     * Navigation drawer methods *
     *****************************/
    private void initNavigationDrawer() {
        //Initialize navigation menu
        navViewAdapter = new NavigationViewAdapter(getActivity());
        navViewAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                handleNavigationItemClick(navViewAdapter.getItem(position));
            }
        });
        navRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        navRecyclerView.setAdapter(navViewAdapter);
        navRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        switch (FlavorType.getFlavor()) {
            case USER:
                navViewAdapter.addAll(AppUtil.getUserMenu());
                //For the very first time when activity is launched, need to initialize main fragment
                lastSelectedItem = navViewAdapter.selectNavigationItem(NavigationId.HOME);
                changeFragment(lastSelectedItem, new HomeFragment());
                break;
            case KITCHEN:
                navViewAdapter.addAll(AppUtil.getKitchenMenu());
                //For the very first time when activity is launched, need to initialize main fragment
                lastSelectedItem = navViewAdapter.selectNavigationItem(NavigationId.ORDER);
                changeFragment(lastSelectedItem, new OrderListFragment());
                break;
            case DRIVER:
                navViewAdapter.addAll(AppUtil.getDriverMenu());
                //For the very first time when activity is launched, need to initialize main fragment
                lastSelectedItem = navViewAdapter.selectNavigationItem(NavigationId.ORDER);
                changeFragment(lastSelectedItem, new OrderListFragment());
                break;
        }

        //Initialize drawer
        drawerLayout.setDrawerElevation(0f);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                float moveFactor = navView.getWidth() * slideOffset;
                contentHome.setTranslationX(moveFactor);
                ViewExKt.setScale(contentHome, 1 - slideOffset / 4);
                contentHome.setCardElevation(slideOffset * AppUtil.toPx(HomeActivity.this, 10));
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                setArrowIcon();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                setHamburgerIcon();
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);

                if (newState == DrawerLayout.STATE_SETTLING) {
                    if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        // Drawer started opening
                    } else {
                        // Drawer started closing
                    }
                }
            }
        });

        //Initialize menu action
        setHamburgerIcon();
    }

    private void handleNavigationItemClick(final NavigationItem item) {
        if (lastSelectedItem != null && lastSelectedItem.getNavigationId() != item.getNavigationId()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (item.getNavigationId()) {
                        case HOME:
                            changeFragment(item, new HomeFragment());
                            break;
                        case ORDER:
                            changeFragment(item, new OrderListFragment());
                            break;
                        case FAVORITE:
                            Intent intentFavorite = new Intent(getActivity(), KitchenListActivity.class);
                            intentFavorite.putExtra(AllConstants.INTENT_KEY_KITCHEN_TYPE, KitchenType.FAVORITE.name());
                            startActivity(intentFavorite);
                            break;
                        case NOTIFICATION:
                            changeFragment(item, new NotificationListFragment());
                            break;
                        case SETTINGS:
                            changeFragment(item, new SettingsFragment());
                            break;
                        case LOGOUT:
                            doLogout();
                            break;
                    }
                }
            }, AllConstants.NAVIGATION_DRAWER_CLOSE_DELAY);
        }

        //Close drawer for any type of navigation item click
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void changeFragment(NavigationItem item, Fragment fragment) {
        setToolBarTitle((item.getNavigationId() == NavigationId.HOME) ? (mLocationAddress != null ? mLocationAddress.getAddressLine() : NavigationId.HOME.name()) : item.getNavigationId().name());

        lastSelectedItem = item;
        navViewAdapter.selectNavigationItem(lastSelectedItem.getNavigationId());
        FragmentUtilsManager.changeSupportFragment(HomeActivity.this, fragment, lastSelectedItem.getNavigationId().name());
    }

    private void setArrowIcon() {
        arcMenuImage.setAnimatedImage(R.drawable.arrow_left, 0L);
        arcMenuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initActivityBackPress();
            }
        });
    }

    private void setHamburgerIcon() {
        arcMenuImage.setAnimatedImage(R.drawable.hamb, 0L);
        arcMenuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void setToolBarTitle(String title) {
        toolbarTitle.setAnimatedText(title, 0L);

        //For marquee address
        AppUtil.doMarqueeTextView(toolbarTitle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putFloat(TRANSLATION_X_KEY, contentHome.getTranslationX());
            outState.putFloat(CARD_ELEVATION_KEY, ViewExKt.getScale(contentHome));
            outState.putFloat(SCALE_KEY, contentHome.getCardElevation());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        if (savedState != null) {
            contentHome.setTranslationX(savedState.getFloat(TRANSLATION_X_KEY));
            ViewExKt.setScale(contentHome, savedState.getFloat(CARD_ELEVATION_KEY));
            contentHome.setCardElevation(savedState.getFloat(SCALE_KEY));
        }
    }

    /**************************
     * Methods for about page *
     **************************/
    private void initAboutPage() {
        AboutConfig aboutConfig = AboutConfig.getInstance();
        aboutConfig.appName = getString(R.string.app_name);
        aboutConfig.appIcon = R.mipmap.ic_launcher;
        aboutConfig.version = AppUtil.getAppVersion(getActivity());
        aboutConfig.author = getString(R.string.app_author);
        aboutConfig.aboutLabelTitle = getString(R.string.mal_title_about);
        aboutConfig.packageName = getApplicationContext().getPackageName();
        aboutConfig.buildType = AboutConfig.BuildType.GOOGLE;

        aboutConfig.facebookUserName = getString(R.string.app_publisher_facebook_id);
        aboutConfig.twitterUserName = getString(R.string.app_publisher_twitter_id);
        aboutConfig.webHomePage = getString(R.string.app_publisher_profile_website);

        // app publisher for "Try Other Apps" item
        aboutConfig.appPublisherId = getString(R.string.app_publisher_id);

        // if pages are stored locally, then you need to override aboutConfig.dialog to be able use custom WebView
        aboutConfig.companyHtmlPath = getString(R.string.app_publisher_company_html_path);
        aboutConfig.privacyHtmlPath = getString(R.string.app_publisher_privacy_html_path);
        aboutConfig.acknowledgmentHtmlPath = getString(R.string.app_publisher_acknowledgment_html_path);

        aboutConfig.dialog = new IDialog() {
            @Override
            public void open(AppCompatActivity appCompatActivity, String url, String tag) {
                // handle custom implementations of WebView. It will be called when user click to web items. (Example: "Privacy", "Acknowledgments" and "About")
            }
        };

        aboutConfig.analytics = new IAnalytic() {
            @Override
            public void logUiEvent(String s, String s1) {
                // handle log events.
            }

            @Override
            public void logException(Exception e, boolean b) {
                // handle exception events.
            }
        };
        // set it only if aboutConfig.analytics is defined.
        aboutConfig.logUiEventName = "Log";

        // Contact Support email details
        aboutConfig.emailAddress = getString(R.string.app_author_email);
        aboutConfig.emailSubject = "[" + getString(R.string.app_name) + "]" + "[" + AppUtil.getAppVersion(getActivity()) + "]" + " " + getString(R.string.app_contact_subject);
        aboutConfig.emailBody = getString(R.string.app_contact_body);
    }

    /****************************************
     * Methods for firebase cloud Messaging *
     ****************************************/
    private void initTokenForFCM() {
        if (NetworkManager.isConnected(getActivity())) {
            if (tokenFetcher != null && tokenFetcher.getStatus() == AsyncTask.Status.RUNNING) {
                tokenFetcher.cancel(true);
            }
            tokenFetcher = new TokenFetcher(getActivity(), new OnTokenUpdateListener() {
                @Override
                public void onTokenUpdate(String update) {
                    if (!AllSettingsManager.isNullOrEmpty(update)) {

                        //Update is found now update the previous token
                        boolean isUpdateFound = false;
                        switch (FlavorType.getFlavor()) {
                            case USER:
                                if ((mAppUser != null) && (!mAppUser.getFcm_token().equalsIgnoreCase(update))) {
                                    Logger.d(TAG, TAG + ">> " + "New token is found and server request is sending");
                                    isUpdateFound = true;
                                } else {
                                    Logger.d(TAG, TAG + ">> " + "New token is not found and server request is not sending");
                                }
                                break;
                            case KITCHEN:
                                if ((mKitchenUser != null) && (!mKitchenUser.getFcm_token().equalsIgnoreCase(update))) {
                                    Logger.d(TAG, TAG + ">> " + "New token is found and server request is sending");
                                    isUpdateFound = true;
                                } else {
                                    Logger.d(TAG, TAG + ">> " + "New token is not found and server request is not sending");
                                }
                                break;
                            case DRIVER:
                                if ((mDriverUser != null) && (!mDriverUser.getFcm_token().equalsIgnoreCase(update))) {
                                    Logger.d(TAG, TAG + ">> " + "New token is found and server request is sending");
                                    isUpdateFound = true;
                                } else {
                                    Logger.d(TAG, TAG + ">> " + "New token is not found and server request is not sending");
                                }
                                break;
                        }

                        //Send server request for updating token
                        if (isUpdateFound) {
                            if (updateTokenTask != null && updateTokenTask.getStatus() == AsyncTask.Status.RUNNING) {
                                updateTokenTask.cancel(true);
                            }

                            updateTokenTask = new UpdateTokenTask(getActivity(), update);
                            updateTokenTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    }
                }
            });
            tokenFetcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private class UpdateTokenTask extends AsyncTask<String, Integer, Response> {

        private Context mContext;
        private String mToken = "";

        private UpdateTokenTask(Context context, String token) {
            mContext = context;
            mToken = token;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Response response = null;

                switch (FlavorType.getFlavor()) {
                    case USER:
                        ParamAppUser paramAppUser = new ParamAppUser(mAppUser.getApp_user_id(), mAppUser.getName(), mAppUser.getPhone(), mAppUser.getEmail(), mAppUser.getLat(), mAppUser.getLng(), mAppUser.getAddress(), "", mToken);
                        Logger.d(TAG, TAG + " >>> APIResponse(paramAppUser): " + paramAppUser.toString());
                        Call<APIResponse<List<AppUser>>> callUser = mApiInterface.apiCreateAppUser(paramAppUser);
                        response = callUser.execute();
                        break;
                    case KITCHEN:
                        //Here we need to send password field null always otherwise api will change password, here password is in MD5 format
                        ParamKitchenRegistration paramKitchenRegistration = new ParamKitchenRegistration(mKitchenUser.getManufacturer_id(), mKitchenUser.getName(), mKitchenUser.getAddress(), mKitchenUser.getZone_id(), mKitchenUser.getLat(), mKitchenUser.getLng(), mKitchenUser.getContact_person_email(), null, mKitchenUser.getContact_person_phone(), mToken);
                        Logger.d(TAG, TAG + ">> APIResponse(paramKitchenRegistration): " + paramKitchenRegistration.toString() + "");
                        Call<APIResponse<List<KitchenUser>>> callKitchen = mApiInterface.apiRequestForKitchenRegistration(paramKitchenRegistration);
                        response = callKitchen.execute();
                        break;
                    case DRIVER:
                        //Here we need to send password field null always otherwise api will change password
                        ParamDriverRegistration paramDriverRegistration = new ParamDriverRegistration(mDriverUser.getId(), mDriverUser.getFirst_name(), mDriverUser.getLast_name(), mDriverUser.getPhone(), mDriverUser.getEmail(), mDriverUser.getLat(), mDriverUser.getLng(), mDriverUser.getAddress(), null, mToken, null);
                        Logger.d(TAG, TAG + ">> APIResponse(paramDriverRegistration): " + paramDriverRegistration.toString() + "");
                        Call<APIResponse<List<DriverUser>>> callDriver = mApiInterface.apiRequestForDriverRegistration(paramDriverRegistration);
                        response = callDriver.execute();
                        break;
                }

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
                if (result != null && result.isSuccessful()) {
                    switch (FlavorType.getFlavor()) {
                        case USER:
                            Logger.d(TAG, TAG + " >>> " + "APIResponse(DoCreateUser): onResponse-server = " + result.toString());
                            APIResponse<List<AppUser>> dataUser = (APIResponse<List<AppUser>>) result.body();

                            if (dataUser != null && dataUser.getStatus().equalsIgnoreCase("1")) {
                                Logger.d(TAG, TAG + " >>> " + "APIResponse(DoCreateUser()): onResponse-object = " + dataUser.toString());

                                if (dataUser.getData().size() == 1) {
                                    String jsonAppUser = APIResponse.getResponseString(dataUser.getData().get(0));
                                    Logger.d(TAG, TAG + " >>> " + "APIResponse(DoCreateUser()): app-user = " + jsonAppUser);
                                    Logger.d(TAG, TAG + " >>> " + "AppUser(home-response): " + jsonAppUser);
                                    SessionManager.setStringSetting(getActivity(), AllConstants.SESSION_KEY_USER, jsonAppUser);
                                }
                            }
                            break;
                        case KITCHEN:
                            Logger.d(TAG, "APIResponse(DoKichenSignUpTask): onResponse-server = " + result.toString());
                            APIResponse<List<KitchenUser>> dataKitchen = (APIResponse<List<KitchenUser>>) result.body();

                            if (dataKitchen != null && dataKitchen.getStatus().equalsIgnoreCase("1")) {
                                Logger.d(TAG, TAG + " >>> " + "APIResponse(DoKichenSignUpTask()): onResponse-object = " + dataKitchen.toString());

                                if (dataKitchen.getData().size() == 1) {
                                    Logger.d(TAG, "APIResponse(DoKichenSignUpTask()): onResponse-object = " + dataKitchen.toString());
                                    String jsonKitchenUser = APIResponse.getResponseString(dataKitchen.getData().get(0));
                                    Logger.d(TAG, TAG + " >>> " + "APIResponse(DoKichenSignUpTask): kitchen-user = " + jsonKitchenUser);
                                    SessionManager.setStringSetting(getActivity(), SESSION_KEY_KITCHEN, jsonKitchenUser);
                                }
                            }
                            break;
                        case DRIVER:
                            Logger.d(TAG, "APIResponse(DoDriverSignUpTask): onResponse-server = " + result.toString());
                            APIResponse<List<DriverUser>> dataDriver = (APIResponse<List<DriverUser>>) result.body();

                            if (dataDriver != null && dataDriver.getStatus().equalsIgnoreCase("1")) {
                                Logger.d(TAG, TAG + " >>> " + "APIResponse(DoDriverSignUpTask()): onResponse-object = " + dataDriver.toString());

                                if (dataDriver.getData().size() == 1) {
                                    Logger.d(TAG, "APIResponse(DoDriverSignUpTask()): onResponse-object = " + dataDriver.getData().get(0).toString());
                                    String jsonDriverUser = APIResponse.getResponseString(dataDriver.getData().get(0));
                                    Logger.d(TAG, TAG + " >>> " + "APIResponse(DoDriverSignUpTask): kitchen-user = " + jsonDriverUser);
                                    SessionManager.setStringSetting(getActivity(), SESSION_KEY_DRIVER, jsonDriverUser);
                                }
                            }
                            break;
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    /**********************
     * Methods for logout *
     **********************/
    private void doLogout() {
        if (!NetworkManager.isConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        if (doLogoutTask != null && doLogoutTask.getStatus() == AsyncTask.Status.RUNNING) {
            doLogoutTask.cancel(true);
        }
        doLogoutTask = new DoLogoutTask(getActivity());
        doLogoutTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class DoLogoutTask extends AsyncTask<String, Integer, Response> {

        private Context mContext;

        public DoLogoutTask(Context context) {
            mContext = context;
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
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Call<APIResponse> call = null;

                switch (FlavorType.getFlavor()) {
                    case KITCHEN:
                        ParamKitchenLogout paramKitchenLogout = new ParamKitchenLogout(mKitchenUser.getManufacturer_id());
                        Logger.d(TAG, TAG + " >>> " + "paramKitchenLogout: " + paramKitchenLogout.toString());
                        call = mApiInterface.apiKitchenLogout(paramKitchenLogout);
                        break;
                    case DRIVER:
                        ParamDriverLogout paramDriverLogout = new ParamDriverLogout(mDriverUser.getId());
                        Logger.d(TAG, TAG + " >>> " + "paramDriverLogout: " + paramDriverLogout.toString());
                        call = mApiInterface.apiDriverLogout(paramDriverLogout);
                        break;
                }

                Response response = call.execute();
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
                    Logger.d(TAG, "APIResponse(DoLogoutTask): onResponse-server = " + result.toString());
                    APIResponse data = (APIResponse) result.body();
                    if (data != null) {
                        Logger.d(TAG, "APIResponse(DoLogoutTask()): onResponse-object = " + data.toString());
                        Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();

                        if (data.getStatus().equalsIgnoreCase("1")) {
                            //Unregister fcm for getting no notifications
                            FcmUtil.saveIsFcmRegistered(getActivity(), 0);

                            //Clear all existing notifications
                            Payload.removeAll(getActivity());

                            //Navigate to the login screen
                            Intent intentLogout = null;
                            switch (FlavorType.getFlavor()) {
                                case KITCHEN:
                                    //Remove existing user
                                    SessionManager.removeSetting(getActivity(), SESSION_KEY_KITCHEN);

                                    intentLogout = new Intent(getActivity(), KitchenLoginActivity.class);
                                    break;
                                case DRIVER:
                                    //Remove existing user
                                    SessionManager.removeSetting(getActivity(), SESSION_KEY_DRIVER);

                                    intentLogout = new Intent(getActivity(), DriverLoginActivity.class);
                                    break;
                            }
                            startActivity(intentLogout);
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