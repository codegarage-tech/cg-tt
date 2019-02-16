//package tech.codegarage.tidetwist.fragment;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.view.animation.BounceInterpolator;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.nshmura.snappysmoothscroller.SnapType;
//import com.nshmura.snappysmoothscroller.SnappyLayoutManager;
//import com.nshmura.snappysmoothscroller.SnappyLinearLayoutManager;
//import com.nshmura.snappysmoothscroller.Utils;
//import com.reversecoder.library.event.OnSingleClickListener;
//import com.reversecoder.library.network.NetworkManager;
//import com.reversecoder.library.storage.SessionManager;
//import com.reversecoder.library.util.AllSettingsManager;
//
//import org.parceler.Parcels;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Response;
//import tech.codegarage.tidetwist.R;
//import tech.codegarage.tidetwist.activity.CuisineListActivity;
//import tech.codegarage.tidetwist.activity.HomeActivity;
//import tech.codegarage.tidetwist.activity.KitchenListActivity;
//import tech.codegarage.tidetwist.activity.VoiceSearchActivity;
//import tech.codegarage.tidetwist.adapter.FeatureAdapter;
//import tech.codegarage.tidetwist.adapter.TimeAdapter;
//import tech.codegarage.tidetwist.base.BaseFragment;
//import tech.codegarage.tidetwist.enumeration.KitchenType;
//import tech.codegarage.tidetwist.model.AppUser;
//import tech.codegarage.tidetwist.model.Feature;
//import tech.codegarage.tidetwist.model.KitchenTime;
//import tech.codegarage.tidetwist.model.ResponseOfflineKitchenTime;
//import tech.codegarage.tidetwist.retrofit.APIClient;
//import tech.codegarage.tidetwist.retrofit.APIInterface;
//import tech.codegarage.tidetwist.retrofit.APIResponse;
//import tech.codegarage.tidetwist.util.AllConstants;
//import tech.codegarage.tidetwist.util.AppUtil;
//import tech.codegarage.tidetwist.util.Logger;
//
//import static tech.codegarage.tidetwist.util.AllConstants.INTENT_KEY_KITCHEN_TYPE;
//import static tech.codegarage.tidetwist.util.AllConstants.SESSION_KEY_USER;
//
///**
// * @author Md. Rashadul Alam
// * Email: rashed.droid@gmail.com
// */
//public class HomeFragmentOld extends BaseFragment {
//
//    private RelativeLayout rlFastDelivery;
//    private LinearLayout llAreaSearch, llVoiceSearch;
//    private TextView tvGreetings, tvMealTime;
//    private AppUser mAppUser;
//    private ImageView ivHomeBanner;
//
//    //Time Snap recycler view
//    private RecyclerView rvTime;
//    private RecyclerView.LayoutManager timeLayoutManager;
//    private TimeAdapter timeAdapter;
//
//    //Feature Snap recycler view
//    private FeatureAdapter featureAdapter;
//    private RecyclerView.LayoutManager featureLayoutManager;
//    private RecyclerView rvFeature;
//    private List<Feature> featureList = new ArrayList<>();
//
//    //Background task
//    private GetKitchenTimeTask getKitchenTimeTask;
//    private APIInterface mApiInterface;
//
//    public static HomeFragmentOld newInstance() {
////        Bundle args = new Bundle();
////        args.putParcelable(INTENT_KEY_FOOD_CHECKOUT, Parcels.wrap(checkoutItem));
//        HomeFragmentOld fragment = new HomeFragmentOld();
////        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public int initFragmentLayout() {
//        return R.layout.fragment_home_old;
//    }
//
//    @Override
//    public void initFragmentBundleData(Bundle bundle) {
////        if (bundle != null) {
////            Parcelable mParcelableCheckoutItem = bundle.getParcelable(INTENT_KEY_FOOD_CHECKOUT);
////            if (mParcelableCheckoutItem != null) {
////                mCheckoutItem = Parcels.unwrap(mParcelableCheckoutItem);
////                Logger.d(TAG, TAG + " >>> " + "mCheckoutItem: " + mCheckoutItem.toString());
////            }
////        }
//    }
//
//    @Override
//    public void initFragmentViews(View parentView) {
//        ivHomeBanner = (ImageView)parentView.findViewById(R.id.iv_home_banner);
//        tvGreetings = (TextView) parentView.findViewById(R.id.tv_greetings);
//        tvMealTime = (TextView) parentView.findViewById(R.id.tv_meal_time);
//        llAreaSearch = (LinearLayout) parentView.findViewById(R.id.ll_area_search);
//        llVoiceSearch = (LinearLayout) parentView.findViewById(R.id.ll_voice_search);
//        rlFastDelivery = (RelativeLayout) parentView.findViewById(R.id.rl_fast_delivery);
//        rvTime = (RecyclerView) parentView.findViewById(R.id.rv_time);
//        rvFeature = (RecyclerView) parentView.findViewById(R.id.rv_feature);
//    }
//
//    @Override
//    public void initFragmentViewsData() {
//        String appUser = SessionManager.getStringSetting(getActivity(), SESSION_KEY_USER);
//        if (!AllSettingsManager.isNullOrEmpty(appUser)) {
//            mAppUser = APIResponse.getResponseObject(appUser, AppUser.class);
//
//            if (mAppUser != null) {
//                setShowTimeOfDay(mAppUser);
//            }
//        }
//
//        mApiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);
//        //Snappy Time recycler view
//        if (!NetworkManager.isConnected(getActivity())) {
//            loadOfflineTimeData();
//        } else {
//            getKitchenTimeTask = new GetKitchenTimeTask(getActivity());
//            getKitchenTimeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }
//
//        //Snappy Feature recycler view
//        //add feature item
//        Feature featureCuisine = new Feature(0, getString(R.string.view_cuisine), getString(R.string.view_cuisine_subtitle), "https://truffle-assets.imgix.net/5f5c384a-111-icecreamconecupcakes-dishland2.jpg");
//        featureList.add(featureCuisine);
//        Feature featurePopular = new Feature(1, getString(R.string.view_popular), getString(R.string.view_popular_subtitle), "http://a2zproductreviews.com/wp-content/uploads/2016/11/food-drinks.jpg");
//        featureList.add(featurePopular);
//        Feature featureOffer = new Feature(2, getString(R.string.view_offer), getString(R.string.view_offer_subtitle), "https://www.rewardsnetwork.com/wp-content/uploads/2016/12/IndianFood_Main.jpg");
//        featureList.add(featureOffer);
//
//        initFeatureSnappySmoothScroller(featureList);
//    }
//
//    @Override
//    public void initFragmentActions() {
//        llAreaSearch.setOnClickListener(new OnSingleClickListener() {
//            @Override
//            public void onSingleClick(View view) {
//                Intent intentAreaSearch = new Intent(getActivity(), KitchenListActivity.class);
//                intentAreaSearch.putExtra(INTENT_KEY_KITCHEN_TYPE, KitchenType.AREA.name());
//                getActivity().startActivity(intentAreaSearch);
//            }
//        });
//
//        llVoiceSearch.setOnClickListener(new OnSingleClickListener() {
//            @Override
//            public void onSingleClick(View view) {
//                Intent intentVoiceSearch = new Intent(getActivity(), VoiceSearchActivity.class);
//                getActivity().startActivity(intentVoiceSearch);
//            }
//        });
//
//        rlFastDelivery.setOnClickListener(new OnSingleClickListener() {
//            @Override
//            public void onSingleClick(View view) {
//                Intent intentFastDelivery = new Intent(getActivity(), KitchenListActivity.class);
//                intentFastDelivery.putExtra(INTENT_KEY_KITCHEN_TYPE, KitchenType.FAST_DELIVERY.name());
//                getActivity().startActivity(intentFastDelivery);
//                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });
//    }
//
//    @Override
//    public void initFragmentBackPress() {
//
//    }
//
//    @Override
//    public void initFragmentOnResult(int requestCode, int resultCode, Intent data) {
//
//    }
//
//    @Override
//    public void initFragmentUpdate(Object object) {
//
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (getKitchenTimeTask != null && getKitchenTimeTask.getStatus() == AsyncTask.Status.RUNNING) {
//            getKitchenTimeTask.cancel(true);
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        AppUtil.loadImage(getActivity(), ivHomeBanner, AppUtil.getHomeBanner(getActivity()), true, false, false);
//    }
//
//    private void setShowTimeOfDay(AppUser appUser) {
//        Calendar c = Calendar.getInstance();
//        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
//
//        if (timeOfDay >= 5 && timeOfDay < 10) {
//            tvGreetings.setText(getString(R.string.view_text_good) + " " + getString(R.string.view_time_morning) + ", " + getString(R.string.view_text_mr) + " " + mAppUser.getName() + "!");
//            tvMealTime.setText(getString(R.string.view_text_its_time_for) + " " + getString(R.string.view_meal_breakfast));
//        } else if (timeOfDay >= 10 && timeOfDay < 12) {
//            tvGreetings.setText(getString(R.string.view_text_good) + " " + getString(R.string.view_time_morning) + ", " + getString(R.string.view_text_mr) + " " + mAppUser.getName() + "!");
//            tvMealTime.setText(getString(R.string.view_text_its_time_for) + " " + getString(R.string.view_meal_morning_snacks));
//        } else if (timeOfDay >= 12 && timeOfDay < 16) {
//            tvGreetings.setText(getString(R.string.view_text_good) + " " + getString(R.string.view_time_afternoon) + ", " + getString(R.string.view_text_mr) + " " + mAppUser.getName() + "!");
//            tvMealTime.setText(getString(R.string.view_text_its_time_for) + " " + getString(R.string.view_meal_lunch));
//        } else if (timeOfDay >= 16 && timeOfDay < 19) {
//            tvGreetings.setText(getString(R.string.view_text_good) + " " + getString(R.string.view_time_evening) + ", " + getString(R.string.view_text_mr) + " " + mAppUser.getName() + "!");
//            tvMealTime.setText(getString(R.string.view_text_its_time_for) + " " + getString(R.string.view_meal_evening_snacks));
//        } else if (timeOfDay >= 19 && timeOfDay < 24) {
//            tvGreetings.setText(getString(R.string.view_text_good) + " " + getString(R.string.view_time_night) + ", " + getString(R.string.view_text_mr) + " " + mAppUser.getName() + "!");
//            tvMealTime.setText(getString(R.string.view_text_its_time_for) + " " + getString(R.string.view_meal_dinner));
//        } else if ((timeOfDay >= 0 && timeOfDay < 4)) {
//            tvGreetings.setText(getString(R.string.view_text_good) + " " + getString(R.string.view_time_night) + ", " + getString(R.string.view_text_mr) + " " + mAppUser.getName() + "!");
//            tvMealTime.setText(getString(R.string.view_text_its_time_for) + " " + getString(R.string.view_sleeping));
//        }
//    }
//
//    /*********************************
//     * Time SnapRecyclerView methods *
//     *********************************/
//    private void loadOfflineTimeData() {
//        String offlineKitchenTime = "";
//        if (!AllSettingsManager.isNullOrEmpty(SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_TIMES))) {
//            offlineKitchenTime = SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_TIMES);
//            Logger.d(TAG, "KitchenTimeData(Session): " + offlineKitchenTime);
//        } else {
//            offlineKitchenTime = AllConstants.DEFAULT_KITCHEN_TIMES;
//            Logger.d(TAG, "KitchenTimeData(default): " + offlineKitchenTime);
//        }
//
//        ResponseOfflineKitchenTime responseOfflineKitchenTime = APIResponse.getResponseObject(offlineKitchenTime, ResponseOfflineKitchenTime.class);
//        initTimeSnappySmoothScroller(responseOfflineKitchenTime.getData());
//    }
//
//    private void initTimeSnappySmoothScroller(List<KitchenTime> kitchenTimes) {
//        timeAdapter = new TimeAdapter(getActivity());
//        timeAdapter.setItems(kitchenTimes, AppUtil.getCurrentKitchenTime(kitchenTimes));
//        timeAdapter.setListener(new TimeAdapter.OnItemClickListener() {
//            @Override
//            public void onClickItem(TimeAdapter.ViewHolder holder) {
////                moveToTime(holder.getAdapterPosition());
//
//                Intent intentKitchenTime = new Intent(getActivity(), KitchenListActivity.class);
//                intentKitchenTime.putExtra(AllConstants.INTENT_KEY_KITCHEN_TYPE, KitchenType.TIME.name());
//                intentKitchenTime.putExtra(AllConstants.INTENT_KEY_TIME, Parcels.wrap(timeAdapter.getKitchenTime(holder.getAdapterPosition())));
//                getActivity().startActivity(intentKitchenTime);
//            }
//        });
//
//        timeLayoutManager = new SnappyLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//
//        rvTime.setLayoutManager(timeLayoutManager);
//        rvTime.setAdapter(timeAdapter);
//        timeAdapter.setVertical(false);
//        //Move to current time item
//        moveToTime(timeAdapter.getItemPosition(AppUtil.getCurrentKitchenTime(kitchenTimes)));
//
//        //position
//        ((SnappyLayoutManager) timeLayoutManager).setSnapType(SnapType.START);
//
//        //Interpolator
//        ((SnappyLayoutManager) timeLayoutManager).setSnapDuration(1100);
//        ((SnappyLayoutManager) timeLayoutManager).setSnapInterpolator(new BounceInterpolator());
//    }
//
//    private void moveToTime(int position) {
//        rvTime.smoothScrollToPosition(position);
//        timeAdapter.setSelectedPosition(position);
//        Utils.resetSelected(rvTime, position);
//    }
//
//    /************************************
//     * Feature SnapRecyclerView methods *
//     ************************************/
//    private void initFeatureSnappySmoothScroller(List<Feature> features) {
//        featureAdapter = new FeatureAdapter(getActivity());
//        featureAdapter.setItems(features, features.get(0));
//        featureAdapter.setListener(new FeatureAdapter.OnItemClickListener() {
//            @Override
//            public void onClickItem(FeatureAdapter.ViewHolder holder, Feature feature) {
////                moveToFeature(holder.getAdapterPosition());
//                if (feature.getTitle().equalsIgnoreCase(getString(R.string.view_cuisine))) {
//                    Intent intentCuisine = new Intent(getActivity(), CuisineListActivity.class);
//                    getActivity().startActivity(intentCuisine);
//                } else if (feature.getTitle().equalsIgnoreCase(getString(R.string.view_popular))) {
//                    Intent intentSearch = new Intent(getActivity(), KitchenListActivity.class);
//                    intentSearch.putExtra(INTENT_KEY_KITCHEN_TYPE, KitchenType.POPULAR.name());
//                    getActivity().startActivity(intentSearch);
//                } else if (feature.getTitle().equalsIgnoreCase(getString(R.string.view_offer))) {
//                    Intent intentSearch = new Intent(getActivity(), KitchenListActivity.class);
//                    intentSearch.putExtra(INTENT_KEY_KITCHEN_TYPE, KitchenType.OFFER.name());
//                    getActivity().startActivity(intentSearch);
//                }
//            }
//        });
//
//        featureLayoutManager = new SnappyLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//
//        rvFeature.setLayoutManager(featureLayoutManager);
//        rvFeature.setAdapter(featureAdapter);
//        featureAdapter.setVertical(false);
//        //Move to current time item
//        moveToFeature(0);
//
//        //position
//        ((SnappyLayoutManager) featureLayoutManager).setSnapType(SnapType.START);
//
//        //Interpolator
//        ((SnappyLayoutManager) featureLayoutManager).setSnapDuration(1100);
//        ((SnappyLayoutManager) featureLayoutManager).setSnapInterpolator(new BounceInterpolator());
//    }
//
//    private void moveToFeature(int position) {
//        rvFeature.smoothScrollToPosition(position);
//        featureAdapter.setSelectedPosition(position);
//        Utils.resetSelected(rvFeature, position);
//    }
//
//    /************************
//     * Server communication *
//     ************************/
//    private class GetKitchenTimeTask extends AsyncTask<String, Integer, Response> {
//
//        Context mContext;
//
//        private GetKitchenTimeTask(Context context) {
//            mContext = context;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            ProgressDialog progressDialog = ((HomeActivity) getActivity()).showProgressDialog();
//            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface dialog) {
//                    cancel(true);
//                }
//            });
//        }
//
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//            Logger.d(TAG, TAG + ">> Background task is cancelled");
//            loadOfflineTimeData();
//        }
//
//        @Override
//        protected Response doInBackground(String... params) {
//            try {
//                Call<APIResponse<List<KitchenTime>>> call = mApiInterface.apiGetAllKitchenTimes();
//                Response response = call.execute();
//                if (response.isSuccessful()) {
//                    return response;
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Response result) {
//            try {
//                ((HomeActivity) getActivity()).dismissProgressDialog();
//
//                if (result != null && result.isSuccessful()) {
//                    Logger.d(TAG, "APIResponse(GetKitchenTimeTask): onResponse-server = " + result.toString());
//                    APIResponse<List<KitchenTime>> data = (APIResponse<List<KitchenTime>>) result.body();
//                    Logger.d("KitchenTimedata", data.toString() + "");
//
//                    if (data != null && data.getStatus().equalsIgnoreCase("1")) {
//                        Logger.d(TAG, "APIResponse(GetKitchenTimeTask()): onResponse-object = " + data.toString());
//
//                        initTimeSnappySmoothScroller(data.getData());
//
//                        //Store times data into the session
//                        SessionManager.setStringSetting(getActivity(), AllConstants.SESSION_KEY_TIMES, data.toString());
//                    } else {
////                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_info_found), Toast.LENGTH_SHORT).show();
//                        loadOfflineTimeData();
//                    }
//                } else {
////                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
//                    loadOfflineTimeData();
//                }
//            } catch (Exception exception) {
//                exception.printStackTrace();
//            }
//        }
//    }
//}