package tech.codegarage.tidetwist.activity;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.random.RandomManager;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.library.util.AllSettingsManager;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import me.wangyuwei.shoppoing.ShoppingView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import retrofit2.Call;
import retrofit2.Response;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.adapter.FoodCategoryViewPagerAdapter;
import tech.codegarage.tidetwist.base.BaseActivity;
import tech.codegarage.tidetwist.dialog.DiscardCartDialog;
import tech.codegarage.tidetwist.fragment.FoodItemFragment;
import tech.codegarage.tidetwist.model.AppUser;
import tech.codegarage.tidetwist.model.FoodCategory;
import tech.codegarage.tidetwist.model.FoodItem;
import tech.codegarage.tidetwist.model.Kitchen;
import tech.codegarage.tidetwist.model.ParamDoFavorite;
import tech.codegarage.tidetwist.retrofit.APIClient;
import tech.codegarage.tidetwist.retrofit.APIInterface;
import tech.codegarage.tidetwist.retrofit.APIResponse;
import tech.codegarage.tidetwist.util.AllConstants;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.FragmentUtilsManager;
import tech.codegarage.tidetwist.util.Logger;
import tech.codegarage.tidetwist.view.CanaroTextView;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class KitchenDetailsActivity extends BaseActivity {

    //Toolbar
    private ImageView ivBack;
    private CanaroTextView tvTitle, tvKitchenName;
    private RelativeLayout rlCart;
    private ImageView ivCart;
    private TextView tvCart;

    //ViewPager and tab
    private TabLayout tabLayoutFoodCategory;
    private ViewPager viewPagerFoodCategory;
    private FoodCategoryViewPagerAdapter viewPagerAdapterFoodCategory;

    private AppBarLayout appBarLayout;
    private Kitchen mKitchen;
    private ImageView ivKitchenImage, ivOpen, ivClosed;
    private TextView tvKitchenCuisine, tvKitchenTime, tvKitchenRating;
    private MaterialRatingBar rbKitchensRating;

    //Background task
    private DoFavouriteTask doFavouriteTask;
    private APIInterface apiInterface;
    private AppUser mAppUser;

    @Override
    public String[] initActivityPermissions() {
        return new String[]{};
    }

    @Override
    public int initActivityLayout() {
        return R.layout.activity_kitchen_detail;
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
            Parcelable mParcelable = intent.getParcelableExtra(AllConstants.INTENT_KEY_KITCHEN);
            if (mParcelable != null) {
                mKitchen = Parcels.unwrap(mParcelable);
                Logger.d(TAG, TAG + " >>> " + "mKitchen: " + mKitchen.toString());
            }
        }
    }

    @Override
    public void initActivityViews() {
        //Toolbar
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (CanaroTextView) findViewById(R.id.tv_title);
        tvTitle.setVisibility(View.GONE);
        tvTitle.setText(mKitchen.getName());
        ivCart = (ImageView) findViewById(R.id.iv_cart);
        tvCart = (TextView) findViewById(R.id.tv_cart);
        rlCart = (RelativeLayout) findViewById(R.id.rl_cart);

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        tabLayoutFoodCategory = (TabLayout) findViewById(R.id.tab_layout_food_category);
        viewPagerFoodCategory = (ViewPager) findViewById(R.id.view_pager_food_cateogry);
        ivKitchenImage = (ImageView) findViewById(R.id.iv_kitchen);
        ivOpen = (ImageView) findViewById(R.id.iv_open);
        ivClosed = (ImageView) findViewById(R.id.iv_closed);
        tvKitchenName = (CanaroTextView) findViewById(R.id.tv_kitchen_name);
        tvKitchenCuisine = (TextView) findViewById(R.id.tv_kitchen_cuisine);
        tvKitchenTime = (TextView) findViewById(R.id.tv_kitchen_time);
        tvKitchenRating = (TextView) findViewById(R.id.tv_kitchen_rating);
        rbKitchensRating = (MaterialRatingBar) findViewById(R.id.rb_kitchens_rating);
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {
        //Set viewpager bottom margin for those device which has navigation bar
        int navigationBarHeight = AppUtil.getNavigationBarHeight(getActivity());
        if (navigationBarHeight > 0) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) viewPagerFoodCategory.getLayoutParams();
            layoutParams.bottomMargin = navigationBarHeight;
        }

        AppUtil.doMarqueeTextView(tvTitle);
        apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        String appUser = SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_USER);
        if (!AllSettingsManager.isNullOrEmpty(appUser)) {
            mAppUser = APIResponse.getResponseObject(appUser, AppUser.class);
            Logger.d(TAG, TAG + " >>> " + "mAppUser: " + mAppUser.toString());
        }

        AppUtil.loadImage(getActivity(), ivKitchenImage, ((mKitchen.getImage() != null && mKitchen.getImage().size() > 0) ? mKitchen.getImage().get(RandomManager.getRandom(mKitchen.getImage().size())).getImage() : ""), false, false, true);
        tvKitchenName.setText(mKitchen.getName());
        tvKitchenCuisine.setText(mKitchen.getCuisine());
        tvKitchenTime.setText(mKitchen.getOpening_schedule());

        //Check if kitchen is closed or open
        if (mKitchen.getIs_opened().equalsIgnoreCase("0")) {
            ivClosed.setVisibility(View.VISIBLE);
            ivOpen.setVisibility(View.GONE);
        } else {
            ivOpen.setVisibility(View.VISIBLE);
            ivClosed.setVisibility(View.GONE);
        }

        //Set kitchen rating
        float star = 0.0f;
        try {
            star = Float.parseFloat(mKitchen.getAverage_rating());
        } catch (Exception ex) {
            ex.printStackTrace();
            star = 0.0f;
        }
        Logger.d(TAG, "RatingKitchen: " + star);
        rbKitchensRating.setRating(star);
        rbKitchensRating.setIsIndicator(true);
        tvKitchenRating.setText(mKitchen.getAverage_rating() + "/" + getString(R.string.view_maximum_review_start_number));

        //Do tasks on appbar layout change
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                    //  Collapsed
                    findViewById(R.id.toolbar).setBackgroundResource(R.drawable.toolbar_bg);
                    tvTitle.setVisibility(View.VISIBLE);
                } else {
                    //Expanded
                    findViewById(R.id.toolbar).setBackgroundResource(R.drawable.toolbar_bg_transparent);
                    tvTitle.setVisibility(View.GONE);
                }
            }
        });

        initViewPager(initTabLayoutData());
    }

    @Override
    public void initActivityActions(Bundle savedInstanceState) {
        ivBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                initActivityBackPress();
            }
        });

        rlCart.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (!isKitchenOpen()) {
                    Toast.makeText(getActivity(), getString(R.string.toast_kitchen_is_closed_now), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (AppUtil.hasStoredFoodItem(getActivity())) {
                    //Review cart items
                    Intent intentCheckout = new Intent(getActivity(), CheckoutActivity.class);
                    intentCheckout.putExtra(AllConstants.INTENT_KEY_KITCHEN_ID, mKitchen.getManufacturer_id());
                    startActivityForResult(intentCheckout, AllConstants.INTENT_REQUEST_CODE_CHECKOUT);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_add_to_cart_first), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void initActivityOnResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case AllConstants.INTENT_REQUEST_CODE_FOOD_ITEM_DETAIL:
                if (resultCode == Activity.RESULT_OK) {
                    if (intent != null) {
                        Parcelable mParcelableFoodItem = intent.getParcelableExtra(AllConstants.INTENT_KEY_FOOD_ITEM);

                        if (mParcelableFoodItem != null) {
                            FoodItem mFoodItem = Parcels.unwrap(mParcelableFoodItem);
                            Logger.d(TAG, TAG + " >>> " + "mFoodItem: " + mFoodItem.toString());

                            //Need to update current fragment if user changes of favorite info
                            syncFavoriteInfoInSelectedTab(mFoodItem);

                            //Update data into kitchen
                            for (int i = 0; i < mKitchen.getItems().size(); i++) {
                                List<FoodCategory> categories = mKitchen.getItems().get(i).getFood_category();
                                for (int j = 0; j < categories.size(); j++) {
                                    List<FoodItem> foodItems = categories.get(j).getFood_items();
                                    int position = AppUtil.getFoodItemPosition(foodItems, mFoodItem);
                                    if (position != -1) {
                                        foodItems.remove(position);
                                        foodItems.add(position, mFoodItem);
                                    }
                                }
                            }
                        }

                        //Need to update current fragment if user changes any selection from food detail screen
                        syncCartInfoInSelectedTab();

                        //Update cart count
                        resetCounterView();
                    }
                }
                break;

            case AllConstants.INTENT_REQUEST_CODE_CHECKOUT:
                if (resultCode == Activity.RESULT_OK) {
                    //Order successfully placed and refresh the selected data
                    Toast.makeText(getActivity(), getString(R.string.toast_order_is_placed_successfully), Toast.LENGTH_SHORT).show();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    //User returned to kitchen detail without order from checkout screen
                }

                //Need to update current fragment if user changes any selection from checkout screen
                syncCartInfoInSelectedTab();

                //Update counter view
                resetCounterView();
                break;
        }
    }

    @Override
    public void initActivityBackPress() {
        if (AppUtil.hasStoredFoodItem(getActivity())) {
            DiscardCartDialog discardCartDialog = new DiscardCartDialog(getActivity(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            AppUtil.deleteAllStoredFoodItems(getActivity());
                            closeKitchenDetail();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                        case DialogInterface.BUTTON_NEUTRAL:
                            break;
                    }
                }
            });
            discardCartDialog.initView().show();
        } else {
            closeKitchenDetail();
        }
    }

    private void closeKitchenDetail() {
        //Discard selection update while backing from kitchen detail
        for (int i = 0; i < mKitchen.getItems().size(); i++) {
            List<FoodCategory> categories = mKitchen.getItems().get(i).getFood_category();
            for (int j = 0; j < categories.size(); j++) {
                List<FoodItem> foodItems = categories.get(j).getFood_items();
                for (int k = 0; k < foodItems.size(); k++) {
                    if (foodItems.get(k).getItem_quantity() > 0) {
                        foodItems.get(k).setItem_quantity(0);
                    }
                }
            }
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra(AllConstants.INTENT_KEY_KITCHEN, Parcels.wrap(mKitchen));
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void initActivityDestroyTasks() {
        if (doFavouriteTask != null && doFavouriteTask.getStatus() == AsyncTask.Status.RUNNING) {
            doFavouriteTask.cancel(true);
        }
    }

    @Override
    public void initActivityPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    private List<FoodCategory> initTabLayoutData() {
        List<FoodCategory> foodCategories = new ArrayList<>();

        //Merge all food category of different cuisine
        for (int i = 0; i < mKitchen.getItems().size(); i++) {
            foodCategories.addAll(mKitchen.getItems().get(i).getFood_category());
        }

        return foodCategories;
    }

    private void initViewPager(List<FoodCategory> foodCategories) {
        viewPagerAdapterFoodCategory = new FoodCategoryViewPagerAdapter(getSupportFragmentManager(), foodCategories);
        viewPagerFoodCategory.setAdapter(viewPagerAdapterFoodCategory);
        viewPagerFoodCategory.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayoutFoodCategory));

        tabLayoutFoodCategory.setupWithViewPager(viewPagerFoodCategory);
        tabLayoutFoodCategory.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //Update data of selected tab from database. This is needed when user change some selected food item count
                //from checkout screen, then it needs to update selection also in kitchen list.
                syncCartInfoInSelectedTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        //For the very first time selected food category
//        if (mFoodCategories.size() > 0) {
//            mSelectedFoodCategory = mFoodCategories.get(0);
//        }
    }

    public void onOrderNowClick(final FoodItem item, ShoppingView shoppingView) {
        Logger.d(TAG, "onOrderNowClick: " + "count: " + item.getItem_quantity());

        if (item.getItem_quantity() == 0) {
            if (AppUtil.isFoodItemStored(getActivity(), item)) {
                //Delete the food from database
                AppUtil.deleteSelectedFoodItem(getActivity(), item);
            }
        } else if (item.getItem_quantity() == 1) {
            if (AppUtil.isFoodItemStored(getActivity(), item)) {
                //Update data into database
                AppUtil.storeSelectedFoodItem(getActivity(), item);
            } else {
                //Add item into database
                AppUtil.storeSelectedFoodItem(getActivity(), item);

                //make fly animation for adding item
                AppUtil.makeFlyAnimation(getActivity(), shoppingView, shoppingView.getAddIcon(), ivCart, 1000, new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        resetCounterView();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        } else {
            //Update data into database
            AppUtil.storeSelectedFoodItem(getActivity(), item);
        }

        //Reset counter view into toolbar
        resetCounterView();
    }

    public void resetCounterView() {
        List<FoodItem> data = AppUtil.getAllStoredFoodItems(getActivity());
        if (data.size() > 0) {
            tvCart.setText(data.size() + "");
            tvCart.setVisibility(View.VISIBLE);
        } else {
            tvCart.setVisibility(View.GONE);
        }
    }

    public boolean isKitchenOpen() {
        if (mKitchen.getIs_opened().equalsIgnoreCase("0")) {
            return false;
        } else {
            return true;
        }
    }

    /************************
     * Fragment update task *
     ************************/
    private void syncCartInfoInSelectedTab() {
        FoodItemFragment foodItemFragment = (FoodItemFragment) FragmentUtilsManager.getVisibleViewPagerSupportFragment(KitchenDetailsActivity.this, viewPagerFoodCategory);
        if (foodItemFragment != null) {
            Logger.d(TAG, TAG + " >>> " + "current fragment: " + foodItemFragment.getTag());
            foodItemFragment.updateSelectedFoodItems();
        }
    }

    private void syncFavoriteInfoInSelectedTab(FoodItem foodItem) {
        FoodItemFragment foodItemFragment = (FoodItemFragment) FragmentUtilsManager.getVisibleViewPagerSupportFragment(KitchenDetailsActivity.this, viewPagerFoodCategory);
        if (foodItemFragment != null) {
            Logger.d(TAG, TAG + " >>> " + "current fragment: " + foodItemFragment.getTag());
            foodItemFragment.updateSpecificFood(foodItem);
        }
    }

    /************************
     * Server communication *
     ************************/
    public void doFavorite(FoodItem foodItem, ImageView imageView) {
        if (mAppUser != null) {
            ParamDoFavorite paramDoFavorite = new ParamDoFavorite(foodItem.getProduct_id(), mAppUser.getApp_user_id(), foodItem.getIs_favourite() == 1 ? "0" : "1");
            doFavouriteTask = new DoFavouriteTask(getActivity(), paramDoFavorite, foodItem, imageView);
            doFavouriteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private class DoFavouriteTask extends AsyncTask<String, Integer, Response> {

        Context mContext;
        ParamDoFavorite paramFavourite;
        FoodItem mFoodItem;
        ImageView mImageView;

        public DoFavouriteTask(Context context, ParamDoFavorite paramDoFavorite, FoodItem foodItem, ImageView imageView) {
            mContext = context;
            paramFavourite = paramDoFavorite;
            mFoodItem = foodItem;
            mImageView = imageView;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Logger.d(TAG, TAG + " >>> " + "paramFavourite: " + paramFavourite.toString());
                Call<APIResponse> call = apiInterface.apiDoFavouriteFoodItem(paramFavourite);

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
                if (result != null && result.isSuccessful()) {
                    Logger.d(TAG, "APIResponse(DoFavouriteTask): onResponse-server = " + result.toString());
                    APIResponse data = (APIResponse) result.body();
                    Logger.d("DoFavouriteTask", data.toString() + "");

                    if (data != null && data.getStatus().equalsIgnoreCase("1")) {
                        Logger.d(TAG, "APIResponse(DoFavouriteTask()): onResponse-object = " + data.toString());

                        if (paramFavourite.getMake_favourite().equalsIgnoreCase("1")) {
                            mImageView.setImageResource(R.drawable.vector_favourite_fill_white);

                            //Update food item data for favorite info
                            mFoodItem.setIs_favourite(1);
                        } else {
                            mImageView.setImageResource(R.drawable.vector_favourite_empty_white);

                            //Update food item data for favorite info
                            mFoodItem.setIs_favourite(0);
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