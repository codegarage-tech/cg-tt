package tech.codegarage.tidetwist.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.waveswiperefresh.WaveSwipeRefreshRecyclerView;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.library.util.AllSettingsManager;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import tech.codegarage.dropdownmenuplus.DropDownMenu;
import tech.codegarage.dropdownmenuplus.interfaces.OnFilterDoneListener;
import tech.codegarage.dropdownmenuplus.model.FilterBean;
import tech.codegarage.dropdownmenuplus.model.ItemBean;
import tech.codegarage.dropdownmenuplus.model.TitleBean;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.adapter.CommonKitchenListAdapter;
import tech.codegarage.tidetwist.adapter.SearchDropDownMenuAdapter;
import tech.codegarage.tidetwist.base.BaseLocationActivity;
import tech.codegarage.tidetwist.enumeration.KitchenType;
import tech.codegarage.tidetwist.model.AppUser;
import tech.codegarage.tidetwist.model.Area;
import tech.codegarage.tidetwist.model.City;
import tech.codegarage.tidetwist.model.Cuisine;
import tech.codegarage.tidetwist.model.Kitchen;
import tech.codegarage.tidetwist.model.KitchenTime;
import tech.codegarage.tidetwist.model.ParamKitchenListByCuisine;
import tech.codegarage.tidetwist.model.ParamKitchenListByFastDelivery;
import tech.codegarage.tidetwist.model.ParamKitchenListByFavorite;
import tech.codegarage.tidetwist.model.ParamKitchenListByOffer;
import tech.codegarage.tidetwist.model.ParamKitchenListByPopular;
import tech.codegarage.tidetwist.model.ParamKitchenListBySearch;
import tech.codegarage.tidetwist.model.ParamKitchenListByTime;
import tech.codegarage.tidetwist.model.ResponseOfflineCityWithArea;
import tech.codegarage.tidetwist.model.ResponseOfflineCuisine;
import tech.codegarage.tidetwist.retrofit.APIClient;
import tech.codegarage.tidetwist.retrofit.APIInterface;
import tech.codegarage.tidetwist.retrofit.APIResponse;
import tech.codegarage.tidetwist.util.AllConstants;
import tech.codegarage.tidetwist.util.Logger;
import tech.codegarage.tidetwist.view.CanaroTextView;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class KitchenListActivity extends BaseLocationActivity {

    //Toolbar
    private ImageView ivBack;
    private CanaroTextView tvTitle;
    private LinearLayout llMenu;

    private KitchenType mKitchenType;
    private Location mCurrentLocation;
    private WaveSwipeRefreshRecyclerView rvKitchen;
    private CommonKitchenListAdapter mCommonKitchenListAdapter;
    private Cuisine mCuisine;
    private KitchenTime mKitchenTime;
    private RelativeLayout rlSuggestion;
    private TextView tvSuggestion;
    private AppUser mAppUser;

    //More loading
    private int mOffset = 0, mTotalcount = 0;
    private Handler mHandler = new Handler();

    //Background task
    private APIInterface mApiInterface;
    private GetKitchenListTask getKitchenListTask;
    private GetAllCityWithAreaTask getAllCityWithAreaTask;
    private GetAllCuisinesTask getAllCuisinesTask;

    //Dropdown menu
    private String[] titleList;
    private DropDownMenu dropDownMenu;
    private FilterBean filterBean = new FilterBean();
    private SearchDropDownMenuAdapter searchDropDownMenuAdapter;
    private ItemBean mSelectedArea, mSelectedCuisine;

    @Override
    public LOCATION_UPDATE_FREQUENCY initLocationUpdateFrequency() {
        return LOCATION_UPDATE_FREQUENCY.ONCE;
    }

    @Override
    public String[] initActivityPermissions() {
        return new String[]{};
    }

    @Override
    public int initActivityLayout() {
        return R.layout.activity_kitchen_list;
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

            String mParcelableKitchenType = intent.getStringExtra(AllConstants.INTENT_KEY_KITCHEN_TYPE);
            if (!AllSettingsManager.isNullOrEmpty(mParcelableKitchenType)) {
                mKitchenType = KitchenType.valueOf(mParcelableKitchenType);
                Logger.d(TAG, TAG + " >>> " + "mKitchenType: " + mKitchenType);
            }

            Parcelable mParcelableCuisine = intent.getParcelableExtra(AllConstants.INTENT_KEY_CUISINE);
            if (mParcelableCuisine != null) {
                mCuisine = Parcels.unwrap(mParcelableCuisine);
                Logger.d(TAG, TAG + " >>> " + "mCuisine: " + mCuisine.toString());
            }

            Parcelable mParcelableTime = intent.getParcelableExtra(AllConstants.INTENT_KEY_TIME);
            if (mParcelableTime != null) {
                mKitchenTime = Parcels.unwrap(mParcelableTime);
                Logger.d(TAG, TAG + " >>> " + "mKitchenTime: " + mKitchenTime.toString());
            }
        }
    }

    @Override
    public void initActivityViews() {
        //Toolbar
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (CanaroTextView) findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.title_activity_kitchen_list));
        llMenu = (LinearLayout) findViewById(R.id.ll_menu);

        rvKitchen = (WaveSwipeRefreshRecyclerView) findViewById(R.id.rv_kitchen);
        dropDownMenu = (DropDownMenu) findViewById(R.id.dropDownMenu);
        rlSuggestion = (RelativeLayout) findViewById(R.id.rl_suggestion);
        tvSuggestion = (TextView) findViewById(R.id.tv_sggestion);
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {
        String appUser = SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_USER);
        if (!AllSettingsManager.isNullOrEmpty(appUser)) {
            mAppUser = APIResponse.getResponseObject(appUser, AppUser.class);
        }

        mApiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        setRecyclerView();

        if (mKitchenType == KitchenType.AREA) {
            if (!NetworkManager.isConnected(getActivity())) {
                loadOfflineDropdownData();
                tvSuggestion.setText(getString(R.string.view_please_connect_internet_and_retry));
            } else {
                getAllCityWithAreaTask = new GetAllCityWithAreaTask(getActivity());
                getAllCityWithAreaTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else {
            rlSuggestion.setVisibility(View.GONE);

            //Here progress is showing because sometime it takes time to retrieve location then loading start after few time
            showProgressDialog();
        }
    }

    private void setRecyclerView() {
        mCommonKitchenListAdapter = new CommonKitchenListAdapter(getActivity());
        rvKitchen.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvKitchen.setAdapter(mCommonKitchenListAdapter);
        mCommonKitchenListAdapter.setMore(R.layout.view_more, new RecyclerArrayAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Logger.d(TAG, TAG + " >>> " + "onLoadMore(mOffset): " + mOffset);
                Logger.d(TAG, TAG + " >>> " + "onLoadMore(mTotalcount): " + mTotalcount);
                Logger.d(TAG, TAG + " >>> " + "onLoadMore(mCommonKitchenListAdapter): " + mCommonKitchenListAdapter.getCount());
                if (mOffset < mTotalcount) {
                    Logger.d(TAG, TAG + " >>> " + "onLoadMore: started loading");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setData(mKitchenType);
                        }
                    }, 2000);
                } else {
                    //No more item
                    mCommonKitchenListAdapter.addAll(new ArrayList<Kitchen>());
                }
            }
        });
        mCommonKitchenListAdapter.setNoMore(R.layout.view_nomore);
        mCommonKitchenListAdapter.setError(R.layout.view_error, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {
                mCommonKitchenListAdapter.resumeMore();
            }

            @Override
            public void onErrorClick() {
                mCommonKitchenListAdapter.resumeMore();
            }
        });
    }

    @Override
    public void initActivityActions(Bundle savedInstanceState) {
        ivBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                initActivityBackPress();
            }
        });
    }

    @Override
    public void initActivityOnResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case AllConstants.INTENT_REQUEST_CODE_KITCHEN_DETAIL:
                if (resultCode == Activity.RESULT_OK) {
                    if (intent != null) {
                        Parcelable mParcelableKitchen = intent.getParcelableExtra(AllConstants.INTENT_KEY_KITCHEN);

                        if (mParcelableKitchen != null) {
                            Kitchen mKitchen = Parcels.unwrap(mParcelableKitchen);
                            Logger.d(TAG, TAG + " >>> " + "mKitchen: " + mKitchen.toString());

                            //Now update list data for specific kitchen
                            mCommonKitchenListAdapter.updateItem(mKitchen);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void initActivityBackPress() {
        finish();
    }

    @Override
    public void initActivityDestroyTasks() {
        dismissProgressDialog();

        if (getKitchenListTask != null && getKitchenListTask.getStatus() == AsyncTask.Status.RUNNING) {
            getKitchenListTask.cancel(true);
        }

        if (getAllCityWithAreaTask != null && getAllCityWithAreaTask.getStatus() == AsyncTask.Status.RUNNING) {
            getAllCityWithAreaTask.cancel(true);
        }

        if (getAllCuisinesTask != null && getAllCuisinesTask.getStatus() == AsyncTask.Status.RUNNING) {
            getAllCuisinesTask.cancel(true);
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void initActivityPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    public void onLocationFound(Location location) {
        if (location != null) {
            mCurrentLocation = location;

            if (mKitchenType != KitchenType.AREA) {
                setData(mKitchenType);
            }
        }
    }

    private void setData(KitchenType kitchenType) {
        if (!NetworkManager.isConnected(getActivity())) {
            dismissProgressDialog();
            //This is for more loading
            mCommonKitchenListAdapter.pauseMore();
            tvSuggestion.setText(getString(R.string.view_please_connect_internet_and_retry));
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (getKitchenListTask != null && getKitchenListTask.getStatus() == AsyncTask.Status.RUNNING) {
                Logger.d(TAG, TAG + " >>> " + "setData: canceling asynctask");
                getKitchenListTask.cancel(true);
            }
            getKitchenListTask = new GetKitchenListTask(getActivity(), kitchenType);
            getKitchenListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /************************
     * Server communication *
     ************************/
    private void loadOfflineDropdownData() {
        loadOfflineCountryWithCityData();
        loadOfflineCuisineData();

        //Initialize dropdown
        initFilterDropDownView();
    }

    private void loadOfflineCountryWithCityData() {
        String offlineCityWithArea = "";
        if (!AllSettingsManager.isNullOrEmpty(SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_CITY_WITH_AREA))) {
            offlineCityWithArea = SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_CITY_WITH_AREA);
            Logger.d(TAG, "KitchenCityWithAreaData(Session): " + offlineCityWithArea);
        } else {
            offlineCityWithArea = AllConstants.DEFAULT_CITY_WITH_AREA;
            Logger.d(TAG, "KitchenCityWithAreaData(default): " + offlineCityWithArea);
        }

        ResponseOfflineCityWithArea responseOfflineCityWithArea = APIResponse.getResponseObject(offlineCityWithArea, ResponseOfflineCityWithArea.class);
        initCityData(responseOfflineCityWithArea.getData());
    }

    private void loadOfflineCuisineData() {
        String offlineCuisine = "";
        if (!AllSettingsManager.isNullOrEmpty(SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_CUISINES))) {
            offlineCuisine = SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_CUISINES);
            Logger.d(TAG, "CuisineData(Session): " + offlineCuisine);
        } else {
            offlineCuisine = AllConstants.DEFAULT_CUISINES;
            Logger.d(TAG, "CuisineData(default): " + offlineCuisine);
        }

        ResponseOfflineCuisine responseOfflineCuisine = APIResponse.getResponseObject(offlineCuisine, ResponseOfflineCuisine.class);
        initCuisineData(responseOfflineCuisine.getData());
    }

    private class GetKitchenListTask extends AsyncTask<String, Integer, Response> {

        Context mContext;
        KitchenType mKitchenType;

        private GetKitchenListTask(Context context, KitchenType kitchenType) {
            mContext = context;
            mKitchenType = kitchenType;
        }

        @Override
        protected void onPreExecute() {
            if (mOffset == 0) {
                showProgressDialog();
            }
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Call<APIResponse<List<Kitchen>>> call = null;

                switch (mKitchenType) {
                    case CUISINE:
                        ParamKitchenListByCuisine paramKitchenListByCuisine = new ParamKitchenListByCuisine(AllConstants.PER_PAGE_ITEM + "", mOffset + "", mCurrentLocation.getLatitude() + "", mCurrentLocation.getLongitude() + "", mCuisine.getId(), mAppUser.getApp_user_id());
                        Logger.d(TAG, TAG + " >>> " + "paramKitchenListByCuisine: " + paramKitchenListByCuisine.toString());
                        call = mApiInterface.apiGetAllKitchensByCuisine(paramKitchenListByCuisine);
                        break;
                    case TIME:
                        ParamKitchenListByTime paramKitchenListByTime = new ParamKitchenListByTime(AllConstants.PER_PAGE_ITEM + "", mOffset + "", mCurrentLocation.getLatitude() + "", mCurrentLocation.getLongitude() + "", mKitchenTime.getTime_id(), mAppUser.getApp_user_id());
                        Logger.d(TAG, TAG + " >>> " + "paramKitchenListByTime: " + paramKitchenListByTime.toString());
                        call = mApiInterface.apiGetAllKitchensByTime(paramKitchenListByTime);
                        break;
                    case AREA:
                        ParamKitchenListBySearch paramKitchenListBySearch = new ParamKitchenListBySearch(AllConstants.PER_PAGE_ITEM + "", mOffset + "", mSelectedCuisine.getId(), mSelectedArea.getId(), mAppUser.getApp_user_id());
                        Logger.d(TAG, TAG + " >>> " + "paramKitchenListBySearch: " + paramKitchenListBySearch.toString());
                        call = mApiInterface.apiGetAllKitchensBySearch(paramKitchenListBySearch);
                        break;
                    case POPULAR:
                        ParamKitchenListByPopular paramKitchenListByPopular = new ParamKitchenListByPopular(AllConstants.PER_PAGE_ITEM + "", mOffset + "", mCurrentLocation.getLatitude() + "", mCurrentLocation.getLongitude() + "", mAppUser.getApp_user_id());
                        Logger.d(TAG, TAG + " >>> " + "paramKitchenListByPopular: " + paramKitchenListByPopular.toString());
                        call = mApiInterface.apiGetAllKitchensByPopular(paramKitchenListByPopular);
                        break;
                    case OFFER:
                        ParamKitchenListByOffer paramKitchenListByOffer = new ParamKitchenListByOffer(AllConstants.PER_PAGE_ITEM + "", mOffset + "", mCurrentLocation.getLatitude() + "", mCurrentLocation.getLongitude() + "", mAppUser.getApp_user_id());
                        Logger.d(TAG, TAG + " >>> " + "paramKitchenListByOffer: " + paramKitchenListByOffer.toString());
                        call = mApiInterface.apiGetAllKitchensByOffer(paramKitchenListByOffer);
                        break;
                    case FAST_DELIVERY:
                        ParamKitchenListByFastDelivery paramKitchenListByFastDelivery = new ParamKitchenListByFastDelivery(AllConstants.PER_PAGE_ITEM + "", mOffset + "", mCurrentLocation.getLatitude() + "", mCurrentLocation.getLongitude() + "", mAppUser.getApp_user_id());
                        Logger.d(TAG, TAG + " >>> " + "paramKitchenListByFastDelivery: " + paramKitchenListByFastDelivery.toString());
                        call = mApiInterface.apiGetAllKitchensByDeliveryTime(paramKitchenListByFastDelivery);
                        break;
                    case FAVORITE:
                        ParamKitchenListByFavorite paramKitchenListByFavorite = new ParamKitchenListByFavorite(AllConstants.PER_PAGE_ITEM + "", mAppUser.getApp_user_id(), mOffset + "");
                        Logger.d(TAG, TAG + " >>> " + "paramKitchenListByFavorite: " + paramKitchenListByFavorite.toString());
                        call = mApiInterface.apiGetAllKitchensByFavorite(paramKitchenListByFavorite);
                        break;
                }

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
                    Logger.d(TAG, TAG + " >>> " + "APIResponse(GetKitchenListTask): onResponse-server = " + result.toString());
                    APIResponse<List<Kitchen>> data = (APIResponse<List<Kitchen>>) result.body();
                    Logger.d(TAG, TAG + " >>> " + "data: " + data.toString() + "");

                    if (data != null && data.getStatus().equalsIgnoreCase("1")) {
                        Logger.d(TAG, TAG + " >>> " + "APIResponse(GetKitchenListTask()): onResponse-object = " + data.toString());

                        //invisible suggestion view
                        rlSuggestion.setVisibility(View.GONE);

                        mCommonKitchenListAdapter.addAll(data.getData());

                        //This is needed for more loading
                        mOffset = mOffset + AllConstants.PER_PAGE_ITEM;
                        mTotalcount = data.getTotal_count();
                    } else {
                        if (mKitchenType == KitchenType.AREA) {
                            tvSuggestion.setText(getString(R.string.toast_no_info_found));
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_info_found), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (mKitchenType == KitchenType.AREA) {
                        tvSuggestion.setText(getString(R.string.toast_could_not_retrieve_info));
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private class GetAllCityWithAreaTask extends AsyncTask<String, Integer, Response> {

        Context mContext;

        public GetAllCityWithAreaTask(Context context) {
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
            loadOfflineDropdownData();
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Call<APIResponse<List<City>>> call = mApiInterface.apiGetAllCitiesWithAreas();

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
                if (result != null && result.isSuccessful()) {
                    Logger.d(TAG, "APIResponse(GetAllCityWithTask): onResponse-server = " + result.toString());
                    APIResponse<List<City>> data = (APIResponse<List<City>>) result.body();

                    if (data != null && data.getStatus().equalsIgnoreCase("1")) {
                        Logger.d(TAG, "APIResponse(GetAllCityWithTask()): onResponse-object = " + data.toString());

                        //Initialize city with area data for search tab bar
                        if (data.getData() != null && data.getData().size() > 0) {
                            initCityData(data.getData());

                            //Store times data into the session
                            SessionManager.setStringSetting(getActivity(), AllConstants.SESSION_KEY_CITY_WITH_AREA, data.toString());

                            //Call cuisine background task
                            getAllCuisinesTask = new GetAllCuisinesTask(getActivity());
                            getAllCuisinesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    } else {
                        loadOfflineDropdownData();
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_info_found) + ",\n" + getResources().getString(R.string.toast_loaded_offline_data), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loadOfflineDropdownData();
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_could_not_retrieve_info) + ",\n" + getResources().getString(R.string.toast_loaded_offline_data), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private class GetAllCuisinesTask extends AsyncTask<String, Integer, Response> {

        Context mContext;

        public GetAllCuisinesTask(Context context) {
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
            loadOfflineDropdownData();
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Call<APIResponse<List<Cuisine>>> call = mApiInterface.apiGetAllCuisines();

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
                    Logger.d(TAG, "APIResponse(GetAllCuisinesTask): onResponse-server = " + result.toString());
                    APIResponse<List<Cuisine>> data = (APIResponse<List<Cuisine>>) result.body();
                    if (data != null && data.getStatus().equalsIgnoreCase("1")) {
                        Logger.d(TAG, "APIResponse(GetAllCuisinesTask()): onResponse-object = " + data.toString());

                        //Initialize cuisine data for search tab bar
                        if (data.getData() != null && data.getData().size() > 0) {
                            initCuisineData(data.getData());

                            //Store times data into the session
                            SessionManager.setStringSetting(getActivity(), AllConstants.SESSION_KEY_CUISINES, data.toString());

                            //Initialize dropdown
                            initFilterDropDownView();
                        }
                    } else {
                        loadOfflineDropdownData();
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_info_found) + ",\n" + getResources().getString(R.string.toast_loaded_offline_data), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loadOfflineDropdownData();
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_could_not_retrieve_info) + ",\n" + getResources().getString(R.string.toast_loaded_offline_data), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    /*************************
     * Dropdown menu methods *
     *************************/
    private void initFilterDropDownView() {
        titleList = new String[]{getActivity().getString(R.string.view_area), getActivity().getString(R.string.view_cuisine)};

        dropDownMenu.visibleDropDownTab(View.VISIBLE);
        dropDownMenu.setDropDownTabBackground(getResources().getColor(R.color.color_grey_800));
        dropDownMenu.setTabDefaultTextColor(getResources().getColor(R.color.white));

        searchDropDownMenuAdapter = new SearchDropDownMenuAdapter(getActivity(), titleList, new OnFilterDoneListener() {
            @Override
            public void onFilterDone(int position, String positionTitle, String urlValue) {
                dropDownMenu.setPositionIndicatorText(position, positionTitle);
                dropDownMenu.close();
            }
        });
        searchDropDownMenuAdapter.setFilterBean(filterBean);
        dropDownMenu.setMenuAdapter(searchDropDownMenuAdapter);

        searchDropDownMenuAdapter.setOnDoubleListViewCallbackListener(new SearchDropDownMenuAdapter.OnDoubleListViewCallbackListener() {
            @Override
            public void onDoubleListViewCallback(TitleBean titleBean, ItemBean subtitleBean) {
                Logger.d(TAG, "searchDropDownMenuAdapter(onDoubleListViewCallback): " + "CityBean: " + titleBean.toString() + "\n" + "AreaBean: " + (subtitleBean == null ? "null" : subtitleBean.toString()));
                mSelectedArea = subtitleBean;

                initSearchData();
            }
        });

        searchDropDownMenuAdapter.setOnSingleGirdViewCallbackListener(new SearchDropDownMenuAdapter.OnSingleGirdViewCallbackListener() {
            @Override
            public void onSingleGridViewCallback(ItemBean item) {
                Logger.d(TAG, "searchDropDownMenuAdapter(onSingleGridViewCallback): " + "Cuisine: " + item.toString());
                mSelectedCuisine = item;

                initSearchData();
            }
        });

        //this is for showing suggestion view
        rlSuggestion.setVisibility(View.VISIBLE);
        tvSuggestion.setText(R.string.view_please_select_area_and_cuisine_from_dropdown);
    }

    private void initSearchData() {
        if (mSelectedArea == null && mSelectedCuisine == null) {
            tvSuggestion.setText(getString(R.string.view_please_select_area_and_cuisine_from_dropdown));
            return;
        }
        if (mSelectedArea == null) {
            tvSuggestion.setText(getString(R.string.view_please_select_area_from_dropdown));
            return;
        }
        if (mSelectedCuisine == null) {
            tvSuggestion.setText(getString(R.string.view_please_select_cuisine_from_dropdown));
            return;
        }
        if (!NetworkManager.isConnected(getActivity())) {
//            Toast.makeText(getActivity(), getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
            tvSuggestion.setText(getString(R.string.toast_network_error));
            return;
        }

        //Search kitchen by search
        mOffset = 0;
        mTotalcount = 0;
        mCommonKitchenListAdapter.clear();
        setData(mKitchenType);
    }

    private void initCityData(List<City> cities) {
        List<TitleBean> titleBeans = new ArrayList<>();
        List<TitleBean> subtitleBeans = new ArrayList<>();

        for (int i = 0; i < cities.size(); i++) {
            List<ItemBean> areaBeans = new ArrayList<>();

            for (int j = 0; j < cities.get(i).getArea().size(); j++) {
                //Insert area
                Area area = cities.get(i).getArea().get(j);
                ItemBean itemBean = new ItemBean(area.getZone_id(), area.getName());
                areaBeans.add(itemBean);
            }

            //Sort area
            Collections.sort(areaBeans, new Comparator<ItemBean>() {
                @Override
                public int compare(ItemBean itemBean1, ItemBean itemBean2) {
                    return itemBean1.getName().compareTo(itemBean2.getName());
                }
            });

            //Insert city
            City city = cities.get(i);
            TitleBean<ItemBean> subtitleBean = new TitleBean<ItemBean>(city.getCountry_id(), city.getName(), areaBeans);
            subtitleBeans.add(subtitleBean);
        }

        //Insert division
        TitleBean<TitleBean> titleBean = new TitleBean<TitleBean>();
        titleBean.setId("0");
        titleBean.setName(getString(R.string.view_dhaka_division));
        titleBean.setSubtitle((ArrayList<TitleBean>) subtitleBeans);
        titleBeans.add(titleBean);

        filterBean.setTitleBeans((ArrayList<TitleBean>) titleBeans);
    }

    private void initCuisineData(List<Cuisine> cuisines) {
        List<ItemBean> cuisineBeans = new ArrayList<>();
        for (int i = 0; i < cuisines.size(); i++) {
            //Insert cuisine
            Cuisine cuisine = cuisines.get(i);
            ItemBean itemBean = new ItemBean(cuisine.getId(), cuisine.getName());
            cuisineBeans.add(itemBean);
        }

        filterBean.setItemBeans((ArrayList<ItemBean>) cuisineBeans);
    }
}