package tech.codegarage.tidetwist.activity;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.NoDescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.jaeger.library.StatusBarUtil;
import com.reversecoder.library.bang.SmallBang;
import com.reversecoder.library.bang.SmallBangListener;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
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
import tech.codegarage.tidetwist.adapter.ReviewListAdapter;
import tech.codegarage.tidetwist.base.BaseActivity;
import tech.codegarage.tidetwist.model.AppUser;
import tech.codegarage.tidetwist.model.FoodItem;
import tech.codegarage.tidetwist.model.Image;
import tech.codegarage.tidetwist.model.ParamDoFavorite;
import tech.codegarage.tidetwist.model.Review;
import tech.codegarage.tidetwist.retrofit.APIClient;
import tech.codegarage.tidetwist.retrofit.APIInterface;
import tech.codegarage.tidetwist.retrofit.APIResponse;
import tech.codegarage.tidetwist.util.AllConstants;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.Logger;
import tech.codegarage.tidetwist.view.CanaroTextView;

import static tech.codegarage.tidetwist.util.AllConstants.INTENT_KEY_IS_KITCHEN_OPEN;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class FoodItemDetailActivity extends BaseActivity {

    //Toolbar
    private ImageView ivBack;
    private CanaroTextView tvTitle;
    private ImageView ivCart;
    private RelativeLayout rlCart;
    private TextView tvCart;

    //Linear layout and ImageView
    private LinearLayout llReview, llFavorite;
    private ImageView ivFavourite;
    private AppBarLayout appBarLayout;
    private FoodItem mFoodItem;
    private boolean isKitchenOpen = true;
    private ShoppingView svAddToCart;
    private LinearLayout llAddToCart;
    private TextView tvIngredients, tvReviewCount, tvFoodRating;
    private SmallBang mSmallBang;
    private MaterialRatingBar materialRatingBar;
    //Review
    private RecyclerView rvReview;
    private ReviewListAdapter reviewListAdapter;

    //Image slider
    private SliderLayout mDemoSlider;

    //Background task
    private GetReviewListTask getReviewListTask;
    private DoFavouriteTask doFavouriteTask;
    private APIInterface apiInterface;
    private AppUser mAppUser;

    @Override
    public String[] initActivityPermissions() {
        return new String[]{};
    }

    @Override
    public int initActivityLayout() {
        return R.layout.activity_food_item_detail;
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
            //Food item
            Parcelable mParcelableFoodItem = intent.getParcelableExtra(AllConstants.INTENT_KEY_FOOD_ITEM);
            if (mParcelableFoodItem != null) {
                mFoodItem = Parcels.unwrap(mParcelableFoodItem);
                Logger.d(TAG, TAG + " >>> " + "mFoodItem: " + mFoodItem.toString());
            }

            //Kitchen open status
            isKitchenOpen = intent.getBooleanExtra(INTENT_KEY_IS_KITCHEN_OPEN, false);
        }
    }

    @Override
    public void initActivityViews() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (CanaroTextView) findViewById(R.id.tv_title);
        tvTitle.setText(mFoodItem.getName());
        ivCart = (ImageView) findViewById(R.id.iv_cart);
        tvCart = (TextView) findViewById(R.id.tv_cart);
        rlCart = (RelativeLayout) findViewById(R.id.rl_cart);

        tvIngredients = (TextView) findViewById(R.id.tv_ingredients);
        ivFavourite = (ImageView) findViewById(R.id.iv_favourite);
        llReview = (LinearLayout) findViewById(R.id.ll_review);
        llFavorite = (LinearLayout) findViewById(R.id.ll_favourite);
        tvReviewCount = (TextView) findViewById(R.id.tv_review_count);
        tvFoodRating = (TextView) findViewById(R.id.tv_food_rating);
        svAddToCart = (ShoppingView) findViewById(R.id.sv_add_to_cart);
        llAddToCart = (LinearLayout) findViewById(R.id.ll_add_to_cart);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mDemoSlider = (SliderLayout) findViewById(R.id.slider_layout_food);
        rvReview = (RecyclerView) findViewById(R.id.rv_review);
        materialRatingBar = (MaterialRatingBar) findViewById(R.id.rb_food_item_rating);
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {
        //Set recycler view bottom margin for those device which has navigation bar
        int navigationBarHeight = AppUtil.getNavigationBarHeight(getActivity());
        if (navigationBarHeight > 0) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) rvReview.getLayoutParams();
            layoutParams.bottomMargin = navigationBarHeight;
        }

        AppUtil.doMarqueeTextView(tvTitle);
        apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        String appUser = SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_USER);
        if (!AllSettingsManager.isNullOrEmpty(appUser)) {
            mAppUser = APIResponse.getResponseObject(appUser, AppUser.class);
            Logger.d(TAG, TAG + " >>> " + "mAppUser: " + mAppUser.toString());
        }

        //Set food rating
        float star = 0.0f;
        try {
            star = Float.parseFloat(mFoodItem.getAverage_rating());
        } catch (Exception ex) {
            ex.printStackTrace();
            star = 0.0f;
        }
        Logger.d(TAG, "RatingFoodDetail: " + star);
        materialRatingBar.setRating(star);
        materialRatingBar.setIsIndicator(true);
        tvFoodRating.setText(mFoodItem.getAverage_rating() + "/" + getString(R.string.view_maximum_review_start_number));

        //Do tasks on appbar layout change
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                    //  Collapsed
                    findViewById(R.id.toolbar).setBackgroundResource(R.drawable.toolbar_bg);
                } else {
                    //Expanded
                    findViewById(R.id.toolbar).setBackgroundResource(R.drawable.toolbar_bg_transparent);
                }
            }
        });

        //Ingredients
        tvIngredients.setText((AllSettingsManager.isNullOrEmpty(mFoodItem.getIngredients())) ? "" : mFoodItem.getIngredients());

        //Review
        tvReviewCount.setText(getString(R.string.view_review_count, (AllSettingsManager.isNullOrEmpty(mFoodItem.getReview_count())) ? 0 : Integer.parseInt(mFoodItem.getReview_count())));
        reviewListAdapter = new ReviewListAdapter(getActivity());
        rvReview.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvReview.setAdapter(reviewListAdapter);
        if (!NetworkManager.isConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
            return;
        } else {
            getReviewListTask = new GetReviewListTask(getActivity());
            getReviewListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        //Image slider
        initImageSlider(mFoodItem);

        //Set favorite data
        if (mFoodItem.getIs_favourite() == 1) {
            ivFavourite.setImageResource(R.drawable.vector_favourite_fill_white);
        } else {
            ivFavourite.setImageResource(R.drawable.vector_favourite_empty_white);
        }

        //Set shopping view selected data
        if (mFoodItem.getItem_quantity() > 0) {
            svAddToCart.setTextNum(mFoodItem.getItem_quantity());
        }
        resetCounterView();

        //Stop add to cart when kitchen is closed
        if (isKitchenOpen) {
            llAddToCart.setVisibility(View.GONE);
        } else {
            llAddToCart.setVisibility(View.VISIBLE);
            llAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), getString(R.string.toast_kitchen_is_closed_now), Toast.LENGTH_SHORT).show();
                }
            });
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

        rlCart.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (!isKitchenOpen) {
                    Toast.makeText(getActivity(), getString(R.string.toast_kitchen_is_closed_now), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (AppUtil.hasStoredFoodItem(getActivity())) {
                    //Review cart items
                    Intent intentCheckout = new Intent(getActivity(), CheckoutActivity.class);
                    intentCheckout.putExtra(AllConstants.INTENT_KEY_KITCHEN_ID, mFoodItem.getManufacturer_id());
                    startActivityForResult(intentCheckout, AllConstants.INTENT_REQUEST_CODE_CHECKOUT);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.toast_please_add_to_cart_first), Toast.LENGTH_SHORT).show();
                }
            }
        });

        svAddToCart.setOnShoppingClickListener(new ShoppingView.ShoppingClickListener() {
            @Override
            public void onAddClick(int num) {
                Logger.d(TAG, "@=> " + "add.......num=> " + num);
                mFoodItem.setItem_quantity(num);
                onOrderNowClick(mFoodItem, svAddToCart);
            }

            @Override
            public void onMinusClick(int num) {
                Logger.d(TAG, "@=> " + "minus.......num=> " + num);
                mFoodItem.setItem_quantity(num);
                onOrderNowClick(mFoodItem, svAddToCart);
            }
        });

        llReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentReview = new Intent(getActivity(), AddFoodReviewActivity.class);
                intentReview.putExtra(AllConstants.INTENT_KEY_FOOD_ITEM, Parcels.wrap(mFoodItem));
                startActivityForResult(intentReview, AllConstants.INTENT_REQUEST_CODE_REVIEW);
                //               Intent intentReview = new Intent(getActivity(), CommonApiActivity.class);
//                startActivity(intentReview);
            }
        });

        llFavorite.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (!NetworkManager.isConnected(getActivity())) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                //send favorite request to the server
                if (mAppUser != null) {
                    ParamDoFavorite paramDoFavorite = new ParamDoFavorite(mFoodItem.getProduct_id(), mAppUser.getApp_user_id(), mFoodItem.getIs_favourite() == 1 ? "0" : "1");
                    doFavouriteTask = new DoFavouriteTask(getActivity(), paramDoFavorite);
                    doFavouriteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

                //Animate view for favorite
                if (mSmallBang == null) {
                    mSmallBang = SmallBang.attach2Window(FoodItemDetailActivity.this);
                }
                mSmallBang.bang(ivFavourite, new SmallBangListener() {
                    @Override
                    public void onAnimationStart() {
                    }

                    @Override
                    public void onAnimationEnd() {
                    }
                });
            }
        });
    }

    @Override
    public void initActivityOnResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AllConstants.INTENT_REQUEST_CODE_REVIEW:
                if (data != null && resultCode == RESULT_OK) {

                    Parcelable mParcelableCuisine = data.getParcelableExtra(AllConstants.INTENT_KEY_FOOD_REVIEW);
                    if (mParcelableCuisine != null) {
                        Review review = Parcels.unwrap(mParcelableCuisine);

                        if (review != null) {
                            //Update review list
                            Logger.d(TAG, TAG + " >>> " + "review: " + review.toString());
                            reviewListAdapter.insert(review, 0);
                            reviewListAdapter.notifyDataSetChanged();

                            //Update review count
                            tvReviewCount.setText(getString(R.string.view_review_count, reviewListAdapter.getCount()));
                            mFoodItem.setReview_count(reviewListAdapter.getCount() + "");
                        }
                    }
                }
                break;

            case AllConstants.INTENT_REQUEST_CODE_CHECKOUT:
                if (resultCode == Activity.RESULT_OK) {
                    //Order successfully placed and refresh the selected data
                    Toast.makeText(getActivity(), getString(R.string.toast_order_is_placed_successfully), Toast.LENGTH_SHORT).show();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    //User returned to food detail without order from checkout screen
                }

                //Need to update food item if user changes any selection from checkout screen
                refreshDataInCartView();

                //Update counter view
                resetCounterView();
                break;
        }
    }

    @Override
    public void initActivityBackPress() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(AllConstants.INTENT_KEY_FOOD_ITEM, Parcels.wrap(mFoodItem));
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void initActivityDestroyTasks() {
        //Image slider
        mDemoSlider.stopAutoCycle();

        if (getReviewListTask != null && getReviewListTask.getStatus() == AsyncTask.Status.RUNNING) {
            getReviewListTask.cancel(true);
        }

        if (doFavouriteTask != null && doFavouriteTask.getStatus() == AsyncTask.Status.RUNNING) {
            doFavouriteTask.cancel(true);
        }
    }

    @Override
    public void initActivityPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    private void initImageSlider(FoodItem foodItem) {
        List<Image> foodImages = new ArrayList<>();
        foodImages.addAll(foodItem.getImage());
//        foodImages.add(new Image("1", "", "", "https://cdn3.tmbi.com/toh/GoogleImages/Southern-Fried-Chicken-with-Gravy_exps33285_THRAA2874593C01_23_1b_RMS.jpg"));
//        foodImages.add(new Image("2", "", "", "https://i.ndtvimg.com/i/2018-02/fries_620x330_51517901541.jpg"));
//        foodImages.add(new Image("3", "", "", "http://bonesuckin.com/BSS-Recipes/wp-content/uploads/2012/05/BBQ-Fried-Chicken-BS_crop-1024x771.jpg"));
//        foodImages.add(new Image("4", "", "", "https://www.cbc.ca/food/content/images/recipes/KoreanChicken.jpg"));

        for (final Image image : foodImages) {
            TextSliderView textSliderView = new TextSliderView(getActivity());
            // initialize a SliderLayout
            textSliderView
                    .description(image.getId())
                    .descriptionVisibility(View.GONE)
                    .image(image.getImage())
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {
//                            Toast.makeText(getActivity(),slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
//                            Toast.makeText(getActivity(), image.getId(), Toast.LENGTH_SHORT).show();
                        }
                    });

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", image.getId());

            mDemoSlider.addSlider(textSliderView);
        }

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.getRandomTransform());
        mDemoSlider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Gone);
        mDemoSlider.setCustomAnimation(new NoDescriptionAnimation());
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /***********************
     * Add to cart methods *
     ***********************/
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

    /************************
     * Server communication *
     ************************/
    private class GetReviewListTask extends AsyncTask<String, Integer, Response> {

        Context mContext;

        public GetReviewListTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Call<APIResponse<List<Review>>> call = apiInterface.apiGetReviewListByFoodItem(mFoodItem.getProduct_id());
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
                    Logger.d(TAG, "APIResponse(GetReviewListTask): onResponse-server = " + result.toString());
                    APIResponse<List<Review>> data = (APIResponse<List<Review>>) result.body();
                    Logger.e("Review", data.toString() + "");

                    if (data != null && data.getStatus().equalsIgnoreCase("1")) {
                        Logger.d(TAG, "APIResponse(GetReviewListTask()): onResponse-object = " + data.toString());

                        if (data.getData().size() > 0) {
                            initReviewData(data.getData());
                        }

                        //set count review number
                        tvReviewCount.setText(getString(R.string.view_review_count, data.getData().size()));
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

    private class DoFavouriteTask extends AsyncTask<String, Integer, Response> {

        Context mContext;
        ParamDoFavorite paramFavourite;

        public DoFavouriteTask(Context context, ParamDoFavorite paramDoFavorite) {
            mContext = context;
            paramFavourite = paramDoFavorite;
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
                            ivFavourite.setImageResource(R.drawable.vector_favourite_fill_white);

                            //Update food item data for favorite info
                            mFoodItem.setIs_favourite(1);
                        } else {
                            ivFavourite.setImageResource(R.drawable.vector_favourite_empty_white);

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

    private void initReviewData(List<Review> reviews) {
        if (reviewListAdapter != null) {
            reviewListAdapter.addAll(reviews);
            reviewListAdapter.notifyDataSetChanged();
        }
    }

    private void refreshDataInCartView() {
        List<FoodItem> dbFoodItems = AppUtil.getAllStoredFoodItems(getActivity());
        FoodItem dbFoodItem = AppUtil.getFoodItem(dbFoodItems, mFoodItem);

        if (dbFoodItem != null) {
            //If this food item is exist into database, then update
            if (dbFoodItem.getItem_quantity() != mFoodItem.getItem_quantity()) {
                mFoodItem.setItem_quantity(dbFoodItem.getItem_quantity());
            }
        } else {
            //If this food item is not exist into database
            if (mFoodItem.getItem_quantity() > 0) {
                mFoodItem.setItem_quantity(0);
            }
        }

        if (mFoodItem.getItem_quantity() > 0) {
            svAddToCart.setTextNum(mFoodItem.getItem_quantity());
        }
    }
}