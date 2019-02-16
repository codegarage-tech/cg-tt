package tech.codegarage.tidetwist.util;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.reversecoder.library.random.RandomManager;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.library.util.AllSettingsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import io.armcha.ribble.presentation.navigationview.NavigationId;
import io.armcha.ribble.presentation.navigationview.NavigationItem;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.animation.flytocart.CircleAnimationUtil;
import tech.codegarage.tidetwist.base.BaseUpdateListener;
import tech.codegarage.tidetwist.model.AppUser;
import tech.codegarage.tidetwist.model.DriverUser;
import tech.codegarage.tidetwist.model.FoodItem;
import tech.codegarage.tidetwist.model.KitchenTime;
import tech.codegarage.tidetwist.model.KitchenUser;
import tech.codegarage.tidetwist.realm.RealmManager;
import tech.codegarage.tidetwist.retrofit.APIResponse;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class AppUtil {

    private static String TAG = AppUtil.class.getSimpleName();

    public static <T> ArrayList<T> convertToArrayList(T[] objectArray){
        return new ArrayList<T>(Arrays.asList(objectArray));
    }

    public static boolean hasNavigationBar(Context context) {
        boolean hasSoftwareKeys = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display d = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);
            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);
            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;

            hasSoftwareKeys = (realWidth > displayWidth) || (realHeight > displayHeight);
        } else {
            boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            hasSoftwareKeys = !hasMenuKey && !hasBackKey;
        }
        return hasSoftwareKeys;
    }

    public static int getNavigationBarHeight(Context context) {
        int navigationBarHeight = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display d = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);
            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);
            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;

            navigationBarHeight = (realHeight > displayHeight) ? (realHeight - displayHeight) : 0;
        }
        return navigationBarHeight;
    }

    public static String get12HourTime(int hourOfDay, int minute, int second) {
        int hour = hourOfDay;
        int minutes = minute;
        String timeSet = "";
        if (hour > 12) {
            hour -= 12;
            timeSet = "PM";
        } else if (hour == 0) {
            hour += 12;
            timeSet = "AM";
        } else if (hour == 12) {
            timeSet = "PM";
        } else {
            timeSet = "AM";
        }

        String hr = "";
        if (hour < 10)
            hr = "0" + hour;
        else
            hr = String.valueOf(hour);
        String min = "";
        if (minutes < 10)
            min = "0" + minutes;
        else
            min = String.valueOf(minutes);
        String sec = "";
        if (second < 10)
            sec = "0" + second;
        else
            sec = String.valueOf(second);

        // Append in a StringBuilder
        String aTime = new StringBuilder()
                .append(hr).append(':')
                .append(min).append(':')
                .append(sec)
                .append(" ")
                .append(timeSet)
                .toString();
        return aTime;
    }

    public static boolean isSimSupport(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            return !(tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT);
        }
        return false;
    }

    public static String convertToCamelCase(String str) {
        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }

    public static List<NavigationItem> getUserMenu() {
        List<NavigationItem> navigationItems = new ArrayList<>();
        navigationItems.add(new NavigationItem(NavigationId.HOME, R.drawable.ic_menu_home, false, R.color.color_grey_800));
        navigationItems.add(new NavigationItem(NavigationId.ORDER, R.drawable.ic_menu_order, false, R.color.color_grey_800));
        navigationItems.add(new NavigationItem(NavigationId.FAVORITE, R.drawable.ic_menu_favourite, false, R.color.color_grey_800));
        navigationItems.add(new NavigationItem(NavigationId.NOTIFICATION, R.drawable.ic_menu_notification, false, R.color.color_grey_800));
        navigationItems.add(new NavigationItem(NavigationId.SETTINGS, R.drawable.ic_menu_settings, false, R.color.color_grey_800));

        return navigationItems;
    }

    public static List<NavigationItem> getKitchenMenu() {
        List<NavigationItem> navigationItems = new ArrayList<>();
        navigationItems.add(new NavigationItem(NavigationId.ORDER, R.drawable.ic_menu_order, false, R.color.color_grey_800));
        navigationItems.add(new NavigationItem(NavigationId.FOODS, R.drawable.ic_menu_food, false, R.color.color_grey_800));
        navigationItems.add(new NavigationItem(NavigationId.NOTIFICATION, R.drawable.ic_menu_notification, false, R.color.color_grey_800));
        navigationItems.add(new NavigationItem(NavigationId.SETTINGS, R.drawable.ic_menu_settings, false, R.color.color_grey_800));
        navigationItems.add(new NavigationItem(NavigationId.LOGOUT, R.drawable.ic_menu_logout, false, R.color.color_grey_800));

        return navigationItems;
    }

    public static List<NavigationItem> getDriverMenu() {
        List<NavigationItem> navigationItems = new ArrayList<>();
        navigationItems.add(new NavigationItem(NavigationId.ORDER, R.drawable.ic_menu_order, false, R.color.color_grey_800));
        navigationItems.add(new NavigationItem(NavigationId.NOTIFICATION, R.drawable.ic_menu_notification, false, R.color.color_grey_800));
        navigationItems.add(new NavigationItem(NavigationId.SETTINGS, R.drawable.ic_menu_settings, false, R.color.color_grey_800));
        navigationItems.add(new NavigationItem(NavigationId.LOGOUT, R.drawable.ic_menu_logout, false, R.color.color_grey_800));

        return navigationItems;
    }

    public static void generateQRCode(String text, ImageView imageView, int width, int height) {
        try {
            if (!AllSettingsManager.isNullOrEmpty(text)) {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, width, height);
                imageView.setImageBitmap(bitmap);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static <C> List<C> asList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> arrayList = new ArrayList<C>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

    //Toolbar
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int toPx(Context context, int value) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (value * density);
    }

    public static void doMarqueeTextView(TextView textView) {
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setSingleLine(true);
        textView.setMarqueeRepeatLimit(-1);
        textView.setSelected(true);
    }

    public static String getAppVersion(Context context) {
        String appVersion = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersion;
    }

    public static int getGridSpanCount(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        float screenWidth = displayMetrics.widthPixels;
        float cellWidth = activity.getResources().getDimension(R.dimen.item_product_width);
        return Math.round(screenWidth / cellWidth);
    }

    public static String formatLocationInfo(String myString) {
        String location = "";
        if (myString != null && myString.trim().length() > 0) {
            location = myString.startsWith(",") ? myString.substring(1).trim().replaceAll(", ,", ",") : myString.replaceAll(", ,", ",");
        }
        return location;
    }

    public static <T> void loadImage(Context context, ImageView imageView, T imageSource, boolean isGif, boolean isRoundedImage, boolean isPlaceHolder) {
        try {
            RequestManager requestManager = Glide.with(context);
            RequestBuilder requestBuilder = isGif ? requestManager.asGif() : requestManager.asBitmap();

            //Dynamic loading without caching while update need for each time loading
//            requestOptions.signature(new ObjectKey(System.currentTimeMillis()));
            //If Cache needed
//            requestOptions.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

            //If Cache needed
            RequestOptions requestOptionsCache = new RequestOptions();
            requestOptionsCache.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
            requestBuilder.apply(requestOptionsCache);

            //For placeholder
            if (isPlaceHolder) {
                RequestOptions requestOptionsPlaceHolder = new RequestOptions();
                requestOptionsPlaceHolder.placeholder(R.mipmap.ic_launcher);
                requestBuilder.apply(requestOptionsPlaceHolder);
            }

            //For error
            RequestOptions requestOptionsError = new RequestOptions();
            requestOptionsError.error(R.mipmap.ic_launcher);
            requestBuilder.apply(requestOptionsError);

            //For rounded image
            if (isRoundedImage) {
                RequestOptions requestOptionsRounded = new RequestOptions();
                requestOptionsRounded.circleCrop();
                requestOptionsRounded.autoClone();
                requestBuilder.apply(requestOptionsRounded);
            }

            //Generic image source
            T mImageSource = null;
            if (imageSource instanceof String) {
                if (!AllSettingsManager.isNullOrEmpty((String) imageSource)) {
                    mImageSource = imageSource;
                }
            } else if (imageSource instanceof Integer) {
                mImageSource = imageSource;
            }
            requestBuilder.load((mImageSource != null) ? mImageSource : R.mipmap.ic_launcher);

            //Load into image view
            requestBuilder.into(imageView);

//            Glide
//                    .with(context)
//                    .asBitmap()
//                    .load((mImageSource != null) ? mImageSource : R.mipmap.ic_launcher)
//                    .apply(requestOptions)
//                    .into(imageView);

        } catch (Exception e) {
        }
    }

    public static ValueAnimator flashView(final View viewGroup, long time) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                viewGroup.setAlpha((Float) animation.getAnimatedValue());
            }
        });
        animator.setDuration(time);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(-1);
        animator.start();
        return animator;
    }

    public static KitchenTime getCurrentKitchenTime(List<KitchenTime> kitchenTimes) {
        if (kitchenTimes != null && kitchenTimes.size() > 0) {
            Calendar c = Calendar.getInstance();
            int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

            String kitchenTime = "";
            if (timeOfDay >= 6 && timeOfDay < 10) {
                kitchenTime = "breakfast";
            } else if (timeOfDay >= 10 && timeOfDay < 12) {
                kitchenTime = "morning snacks";
            } else if (timeOfDay >= 12 && timeOfDay < 16) {
                kitchenTime = "lunch";
            } else if (timeOfDay >= 16 && timeOfDay < 19) {
                kitchenTime = "evening snacks";
            } else if (timeOfDay >= 19 && timeOfDay < 22) {
                kitchenTime = "dinner";
            }

            for (KitchenTime mKitchenTime : kitchenTimes) {
                if (mKitchenTime.getPrepare_time().equalsIgnoreCase(kitchenTime)) {
                    return mKitchenTime;
                }
            }
        }
        return null;
    }

    public static void makeFlyAnimation(Activity activity, View sourceView, View sourceViewCopy, View destinationView, int timeMilliSecond, Animator.AnimatorListener animatorListener) {
        new CircleAnimationUtil().attachActivity(activity)
                .setTargetView(sourceView)
                .setTargetViewCopy(sourceViewCopy)
                .setMoveDuration(timeMilliSecond)
                .setDestView(destinationView)
                .setAnimationListener(animatorListener).startAnimation();
    }

    public static RotateAnimation makeRotateAnimation(final View view, final int rotationCount, final BaseUpdateListener baseUpdateListener) {
        int previousDegrees = 0;
        int degrees = 360;
        final RotateAnimation animation = new RotateAnimation(previousDegrees, degrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillEnabled(true);
        animation.setFillAfter(true);
        animation.setDuration(1500);
        animation.setAnimationListener(new Animation.AnimationListener() {

            int count = 0;

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (rotationCount > 0) {
                    count++;
                    if (count == rotationCount) {
                        if (baseUpdateListener != null) {
                            baseUpdateListener.onUpdate(true);
                        }
                    } else {
                        view.startAnimation(animation);
                    }
                } else {
                    view.startAnimation(animation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
        return animation;
    }

    public static void makeFlyAnimation(Activity activity, View sourceView, Bitmap sourceViewBitmap, View destinationView, int timeMilliSecond, Animator.AnimatorListener animatorListener) {

        //Create a copy view
        ImageView animImg = new ImageView(activity);
//        Bitmap bm = ((BitmapDrawable) sourceView.getDrawable()).getBitmap();
        animImg.setImageBitmap(sourceViewBitmap);

        ViewGroup anim_mask_layout = CircleAnimationUtil.createAnimLayout(activity);
        anim_mask_layout.addView(animImg);

        int[] startXY = new int[2];
        sourceView.getLocationInWindow(startXY);
        final View v = CircleAnimationUtil.addViewToAnimLayout(activity, animImg, startXY, true);
        if (v == null) {
            return;
        }

        new CircleAnimationUtil().attachActivity(activity)
                .setTargetView(sourceView)
                .setTargetViewCopy(v)
                .setMoveDuration(timeMilliSecond)
                .setDestView(destinationView)
                .setAnimationListener(animatorListener).startAnimation();
    }

    public static FoodItem getFoodItem(List<FoodItem> foodItems, FoodItem foodItem) {
        for (int i = 0; i < foodItems.size(); i++) {
            if (foodItems.get(i).getProduct_id().equalsIgnoreCase(foodItem.getProduct_id())) {
                return foodItems.get(i);
            }
        }
        return null;
    }

    public static int getFoodItemPosition(List<FoodItem> foodItems, FoodItem foodItem) {
        for (int i = 0; i < foodItems.size(); i++) {
            if (foodItems.get(i).getProduct_id().equalsIgnoreCase(foodItem.getProduct_id())) {
                return i;
            }
        }
        return -1;
    }

    public static void storeSelectedFoodItem(Activity activity, FoodItem foodItem) {
        RealmManager mRealmManager = RealmManager.with(activity);
        mRealmManager.insertOrUpdate(foodItem);
        Log.d(AppUtil.class.getSimpleName(), "onOrderNowClick>>> storeSelectedFoodItem: " + ((FoodItem) mRealmManager.getData(FoodItem.class, AllConstants.TABLE_KEY_FOOD_ITEM, foodItem.getProduct_id())).toString());
    }

    public static void deleteSelectedFoodItem(Activity activity, FoodItem foodItem) {
        RealmManager mRealmManager = RealmManager.with(activity);
        mRealmManager.deleteData(FoodItem.class, AllConstants.TABLE_KEY_FOOD_ITEM, foodItem.getProduct_id());
    }

    public static void deleteAllStoredFoodItems(Activity activity) {
        RealmManager mRealmManager = RealmManager.with(activity);
        mRealmManager.deleteAllData(FoodItem.class);
    }

    public static void removedUnselectedFoodItems(Activity activity) {
        for (FoodItem foodItem : getAllStoredFoodItems(activity)) {
            if (foodItem.getItem_quantity() == 0) {
                deleteSelectedFoodItem(activity, foodItem);
            }
        }
    }

    public static FoodItem getStoredFoodItem(Activity activity, FoodItem foodItem) {
        RealmManager mRealmManager = RealmManager.with(activity);
        FoodItem mFoodItem = ((FoodItem) mRealmManager.getData(FoodItem.class, AllConstants.TABLE_KEY_FOOD_ITEM, foodItem.getProduct_id()));
        Log.d(AppUtil.class.getSimpleName(), "onOrderNowClick>>> getStoredFoodItem: " + mFoodItem.toString());
        return mFoodItem;
    }

    public static List<FoodItem> getAllStoredFoodItems(Activity activity) {
        RealmManager mRealmManager = RealmManager.with(activity);
        List<FoodItem> data = mRealmManager.getAllListData(FoodItem.class);
        return (data != null ? data : new ArrayList<FoodItem>());
    }

    public static boolean isFoodItemStored(Activity activity, FoodItem foodItem) {
        RealmManager mRealmManager = RealmManager.with(activity);
        boolean isExist = mRealmManager.isDataExist(FoodItem.class, AllConstants.TABLE_KEY_FOOD_ITEM, foodItem.getProduct_id());
        Log.d(AppUtil.class.getSimpleName(), "onOrderNowClick>>> isFoodItemStored: " + isExist);
        return isExist;
    }

    public static boolean hasStoredFoodItem(Activity activity) {
        RealmManager mRealmManager = RealmManager.with(activity);
        boolean isExist = mRealmManager.hasData(FoodItem.class);
        Log.d(AppUtil.class.getSimpleName(), "onOrderNowClick>>> hasStoredFoodItem: " + isExist);
        return isExist;
    }

    public static float getItemTotalPrice(FoodItem foodItem) {
        float price = 0.0f, totalPrice = 0.0f;
        if (foodItem != null) {
            try {
                if (!AllSettingsManager.isNullOrEmpty(foodItem.getPrice())) {
                    price = Float.parseFloat(foodItem.getPrice());
                    totalPrice = foodItem.getItem_quantity() * price;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                totalPrice = 0.0f;
            }
        }
        return totalPrice;
    }

    public static float getSubtotalPrice(Activity activity) {
        float subTotalPrice = 0.0f;
        try {
            List<FoodItem> foodItems = AppUtil.getAllStoredFoodItems(activity);
            for (int i = 0; i < foodItems.size(); i++) {
                FoodItem mFoodItem = foodItems.get(i);
                float itemPrice = getItemTotalPrice(mFoodItem);
                Log.d(AppUtil.class.getSimpleName(), AppUtil.class.getSimpleName() + ">> " + "total(itemPrice): " + mFoodItem.getPrice());
                Log.d(AppUtil.class.getSimpleName(), AppUtil.class.getSimpleName() + ">> " + "total(itemQuantity): " + mFoodItem.getItem_quantity());
                Log.d(AppUtil.class.getSimpleName(), AppUtil.class.getSimpleName() + ">> " + "total(priceWithQuantity): " + itemPrice);
                subTotalPrice = subTotalPrice + itemPrice;
                Log.d(AppUtil.class.getSimpleName(), AppUtil.class.getSimpleName() + ">> " + "total(subTotalPrice): " + subTotalPrice);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return subTotalPrice;
    }

    public static float getPromotionalDiscountPrice(float subtotal, float promotionalDiscount) {
        float mDiscountPrice = 0.0f;
        if (promotionalDiscount > 0) {
            mDiscountPrice = ((subtotal * promotionalDiscount) / 100);
        }

        return mDiscountPrice;
    }

    public static float getTotalPrice(float subtotal, float promotionalDiscount, float shippingCost) {
        float tempSubTotalPrice = subtotal - getPromotionalDiscountPrice(subtotal, promotionalDiscount);
//        if (promotionalDiscount > 0) {
//            float discount = ((subtotal * promotionalDiscount) / 100);
//            tempSubTotalPrice = subtotal - discount;
//        } else {
//            tempSubTotalPrice = subtotal;
//        }

        return tempSubTotalPrice + shippingCost;
    }

    public static float formatFloat(float value) {
        float twoDigitsFloat = 0.0f;
        try {
//            DecimalFormat decimalFormat = new DecimalFormat("#.##");
//            twoDigitsFloat = Float.valueOf(decimalFormat.format(value));
            twoDigitsFloat = Float.parseFloat(String.format("%.2f", value));
        } catch (Exception ex) {
            twoDigitsFloat = 0.0f;
            ex.printStackTrace();
        }
        return twoDigitsFloat;
    }

    public static AppUser getAppUser(Context context) {
        AppUser mAppUser = null;
        String appUser = SessionManager.getStringSetting(context, AllConstants.SESSION_KEY_USER);
        if (!AllSettingsManager.isNullOrEmpty(appUser)) {
            mAppUser = APIResponse.getResponseObject(appUser, AppUser.class);
        }
        return mAppUser;
    }

    public static KitchenUser getKitchenUser(Context context) {
        KitchenUser mKitchenUser = null;
        String kitchenUser = SessionManager.getStringSetting(context, AllConstants.SESSION_KEY_KITCHEN);
        if (!AllSettingsManager.isNullOrEmpty(kitchenUser)) {
            mKitchenUser = APIResponse.getResponseObject(kitchenUser, KitchenUser.class);
        }
        return mKitchenUser;
    }

    public static DriverUser getDriverUser(Context context) {
        DriverUser mDriverUser = null;
        String driverUser = SessionManager.getStringSetting(context, AllConstants.SESSION_KEY_DRIVER);
        if (!AllSettingsManager.isNullOrEmpty(driverUser)) {
            mDriverUser = APIResponse.getResponseObject(driverUser, DriverUser.class);
        }
        return mDriverUser;
    }

    public static boolean isDebug(Context context) {
//        return BuildConfig.DEBUG;
        return ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
    }

    public static int getHomeBanner(Context context) {
        TypedArray images = context.getResources().obtainTypedArray(R.array.home_banner);
        int image = images.getResourceId(RandomManager.getRandom(1, 10), -1);
        images.recycle();
        return image;
    }
}