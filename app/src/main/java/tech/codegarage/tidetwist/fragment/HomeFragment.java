package tech.codegarage.tidetwist.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.library.util.AllSettingsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.activity.CuisineListActivity;
import tech.codegarage.tidetwist.activity.HomeActivity;
import tech.codegarage.tidetwist.activity.KitchenListActivity;
import tech.codegarage.tidetwist.adapter.FeatureListAdapter;
import tech.codegarage.tidetwist.base.BaseFragment;
import tech.codegarage.tidetwist.enumeration.KitchenType;
import tech.codegarage.tidetwist.model.AppUser;
import tech.codegarage.tidetwist.model.KitchenTime;
import tech.codegarage.tidetwist.model.ResponseOfflineKitchenTime;
import tech.codegarage.tidetwist.retrofit.APIClient;
import tech.codegarage.tidetwist.retrofit.APIInterface;
import tech.codegarage.tidetwist.retrofit.APIResponse;
import tech.codegarage.tidetwist.util.AllConstants;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.Logger;

import static tech.codegarage.tidetwist.util.AllConstants.INTENT_KEY_KITCHEN_TYPE;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class HomeFragment extends BaseFragment {

    private Button btnExploreNowArea, btnExploreNowCuisine;
    private TextView tvGreetings, tvUserName, tvMealTime;
    private AppUser mAppUser;
    private ImageView ivHomeBanner;

    //Time recycler view
    private RecyclerView rvFeature;
    private FeatureListAdapter featureListAdapter;

    //Background task
    private GetKitchenTimeTask getKitchenTimeTask;
    private APIInterface mApiInterface;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public int initFragmentLayout() {
        return R.layout.fragment_home;
    }

    @Override
    public void initFragmentBundleData(Bundle bundle) {
    }

    @Override
    public void initFragmentViews(View parentView) {
        ivHomeBanner = (ImageView) parentView.findViewById(R.id.iv_home_banner);
        tvGreetings = (TextView) parentView.findViewById(R.id.tv_greetings);
        tvUserName = (TextView) parentView.findViewById(R.id.tv_user_name);
        tvMealTime = (TextView) parentView.findViewById(R.id.tv_meal_time);
        btnExploreNowArea = (Button) parentView.findViewById(R.id.btn_explore_now_area);
        btnExploreNowCuisine = (Button) parentView.findViewById(R.id.btn_explore_now_cuisine);
        rvFeature = (RecyclerView) parentView.findViewById(R.id.rv_feature);
    }

    @Override
    public void initFragmentViewsData() {
        mApiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);
        //Snappy Time recycler view
        if (!NetworkManager.isConnected(getActivity())) {
            loadOfflineTimeData();
        } else {
            getKitchenTimeTask = new GetKitchenTimeTask(getActivity());
            getKitchenTimeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void initFragmentActions() {
        btnExploreNowArea.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Intent intentAreaSearch = new Intent(getActivity(), KitchenListActivity.class);
                intentAreaSearch.putExtra(INTENT_KEY_KITCHEN_TYPE, KitchenType.AREA.name());
                getActivity().startActivity(intentAreaSearch);
            }
        });
        btnExploreNowCuisine.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Intent intentCuisine = new Intent(getActivity(), CuisineListActivity.class);
                getActivity().startActivity(intentCuisine);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getKitchenTimeTask != null && getKitchenTimeTask.getStatus() == AsyncTask.Status.RUNNING) {
            getKitchenTimeTask.cancel(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        updateGreetingsMessage();
    }

    private void updateGreetingsMessage() {
        AppUtil.loadImage(getActivity(), ivHomeBanner, AppUtil.getHomeBanner(getActivity()), true, false, false);

        mAppUser = AppUtil.getAppUser(getActivity());

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 6 && timeOfDay < 10) {
            tvGreetings.setText(getString(R.string.view_text_good) + " " + getString(R.string.view_time_morning) + ", ");
            tvMealTime.setText(getString(R.string.view_text_its_time_for) + " " + getString(R.string.view_meal_breakfast) + ".");
        } else if (timeOfDay >= 10 && timeOfDay < 12) {
            tvGreetings.setText(getString(R.string.view_text_good) + " " + getString(R.string.view_time_morning) + ", ");
            tvMealTime.setText(getString(R.string.view_text_its_time_for) + " " + getString(R.string.view_meal_morning_snacks) + ".");
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            tvGreetings.setText(getString(R.string.view_text_good) + " " + getString(R.string.view_time_afternoon) + ", ");
            tvMealTime.setText(getString(R.string.view_text_its_time_for) + " " + getString(R.string.view_meal_lunch) + ".");
        } else if (timeOfDay >= 16 && timeOfDay < 19) {
            tvGreetings.setText(getString(R.string.view_text_good) + " " + getString(R.string.view_time_evening) + ", ");
            tvMealTime.setText(getString(R.string.view_text_its_time_for) + " " + getString(R.string.view_meal_evening_snacks) + ".");
        } else if (timeOfDay >= 19 && timeOfDay < 22) {
            tvGreetings.setText(getString(R.string.view_text_good) + " " + getString(R.string.view_time_night) + ", ");
            tvMealTime.setText(getString(R.string.view_text_its_time_for) + " " + getString(R.string.view_meal_dinner) + ".");
        } else if ((timeOfDay >= 22 || timeOfDay < 6 || timeOfDay == 0)) {
            tvGreetings.setText(getString(R.string.view_text_good) + " " + getString(R.string.view_time_night) + ", ");
            tvMealTime.setText(getString(R.string.view_text_its_time_for) + " " + getString(R.string.view_sleeping) + ".");
        }
        tvUserName.setText(mAppUser.getName() + "!");

        //Move to current time item
        if (featureListAdapter != null) {
            featureListAdapter.setCurrentKitchenTime();
        }
    }

    /********************************
     * Feature RecyclerView methods *
     ********************************/
    private void loadOfflineTimeData() {
        String offlineKitchenTime = "";
        if (!AllSettingsManager.isNullOrEmpty(SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_TIMES))) {
            offlineKitchenTime = SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_TIMES);
            Logger.d(TAG, "KitchenTimeData(Session): " + offlineKitchenTime);
        } else {
            offlineKitchenTime = AllConstants.DEFAULT_KITCHEN_TIMES;
            Logger.d(TAG, "KitchenTimeData(default): " + offlineKitchenTime);
        }

        ResponseOfflineKitchenTime responseOfflineKitchenTime = APIResponse.getResponseObject(offlineKitchenTime, ResponseOfflineKitchenTime.class);
        initFeatureRecyclerView(responseOfflineKitchenTime.getData());
    }

    private void initFeatureRecyclerView(List<KitchenTime> kitchenTimes) {
        //Sort list except "All" item
        List<KitchenTime> mKitchenTimes = new ArrayList<>();
        if (kitchenTimes != null && kitchenTimes.size() > 0) {
            for (KitchenTime kitchenTime : kitchenTimes) {
                if (!kitchenTime.getPrepare_time().toLowerCase().equalsIgnoreCase("all")) {
                    mKitchenTimes.add(kitchenTime);
                }
            }
        }

        //Add three items into list
        KitchenTime featureCuisine = new KitchenTime("420", getString(R.string.view_fast_delivery), "", "https://image.freepik.com/free-vector/food-delivery-logo-with-motorbike-design_1447-30.jpg");
        mKitchenTimes.add(featureCuisine);
        KitchenTime featurePopular = new KitchenTime("421", getString(R.string.view_popular), "", "https://d33g3rbxseytqx.cloudfront.net/wp-content/uploads/2016/12/Most-Popluar-Blog-Posts-of-2016.jpg");
        mKitchenTimes.add(featurePopular);
        KitchenTime featureOffer = new KitchenTime("422", getString(R.string.view_offer), "", "https://www.thenewforestinn.co.uk/wp-content/uploads/2018/05/special-offer.jpg");
        mKitchenTimes.add(featureOffer);

        featureListAdapter = new FeatureListAdapter(getActivity());
        rvFeature.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvFeature.setAdapter(featureListAdapter);
        featureListAdapter.addAll(mKitchenTimes);
        featureListAdapter.setCurrentKitchenTime();
    }

    /************************
     * Server communication *
     ************************/
    private class GetKitchenTimeTask extends AsyncTask<String, Integer, Response> {

        Context mContext;

        private GetKitchenTimeTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            ProgressDialog progressDialog = ((HomeActivity) getActivity()).showProgressDialog();
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
            loadOfflineTimeData();
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Call<APIResponse<List<KitchenTime>>> call = mApiInterface.apiGetAllKitchenTimes();
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
                ((HomeActivity) getActivity()).dismissProgressDialog();

                if (result != null && result.isSuccessful()) {
                    Logger.d(TAG, "APIResponse(GetKitchenTimeTask): onResponse-server = " + result.toString());
                    APIResponse<List<KitchenTime>> data = (APIResponse<List<KitchenTime>>) result.body();
                    Logger.d("KitchenTimedata", data.toString() + "");

                    if (data != null && data.getStatus().equalsIgnoreCase("1")) {
                        Logger.d(TAG, "APIResponse(GetKitchenTimeTask()): onResponse-object = " + data.toString());

                        initFeatureRecyclerView(data.getData());

                        //Store times data into the session
                        SessionManager.setStringSetting(getActivity(), AllConstants.SESSION_KEY_TIMES, data.toString());
                    } else {
                        loadOfflineTimeData();
                    }
                } else {
                    loadOfflineTimeData();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}