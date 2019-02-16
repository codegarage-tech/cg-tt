package tech.codegarage.tidetwist.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.library.util.AllSettingsManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.adapter.CuisineListAdapter;
import tech.codegarage.tidetwist.base.BaseActivity;
import tech.codegarage.tidetwist.model.Cuisine;
import tech.codegarage.tidetwist.model.ResponseOfflineCuisine;
import tech.codegarage.tidetwist.retrofit.APIClient;
import tech.codegarage.tidetwist.retrofit.APIInterface;
import tech.codegarage.tidetwist.retrofit.APIResponse;
import tech.codegarage.tidetwist.util.AllConstants;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.Logger;
import tech.codegarage.tidetwist.view.CanaroTextView;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class CuisineListActivity extends BaseActivity {

    //Toolbar
    private ImageView ivBack;
    private CanaroTextView tvTitle;
    private LinearLayout llMenu;

    private RecyclerView rvCuisine;
    private CuisineListAdapter cuisineListAdapter;
    private APIInterface apiInterface;

    private GetAllCuisinesTask getAllCuisinesTask;

    @Override
    public String[] initActivityPermissions() {
        return new String[]{};
    }

    @Override
    public int initActivityLayout() {
        return R.layout.activity_cuisine_list;
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
        tvTitle.setText(getString(R.string.title_activity_cuisine_list));
        llMenu = (LinearLayout) findViewById(R.id.ll_menu);
        rvCuisine = (RecyclerView) findViewById(R.id.rv_cuisine);
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {
        apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        cuisineListAdapter = new CuisineListAdapter(getActivity());
        rvCuisine.setLayoutManager(new GridLayoutManager(this, AppUtil.getGridSpanCount(getActivity())));
        rvCuisine.setAdapter(cuisineListAdapter);

        if (!NetworkManager.isConnected(getActivity())) {
            loadOfflineCuisineData();
        } else {
            getAllCuisinesTask = new GetAllCuisinesTask(getActivity(), apiInterface);
            getAllCuisinesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void initCuisineData(List<Cuisine> cuisines) {
        if (cuisineListAdapter != null) {
            cuisineListAdapter.addAll(cuisines);
            cuisineListAdapter.notifyDataSetChanged();
        }
    }

    private void loadOfflineCuisineData() {
        String offlineKitchenCuisine = "";
        if (!AllSettingsManager.isNullOrEmpty(SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_CUISINES))) {
            offlineKitchenCuisine = SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_CUISINES);
            Logger.d(TAG, "CuisinesData(Session): " + offlineKitchenCuisine);
        } else {
            offlineKitchenCuisine = AllConstants.DEFAULT_CUISINES;
            Logger.d(TAG, "CuisinesData(default): " + offlineKitchenCuisine);
        }

        //Load cuisine data
        ResponseOfflineCuisine responseOfflineCuisine = APIResponse.getResponseObject(offlineKitchenCuisine, ResponseOfflineCuisine.class);
        initCuisineData(responseOfflineCuisine.getData());
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
    public void initActivityOnResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public void initActivityBackPress() {
        finish();
    }


    @Override
    public void initActivityDestroyTasks() {
        if (getAllCuisinesTask != null && getAllCuisinesTask.getStatus() == AsyncTask.Status.RUNNING) {
            getAllCuisinesTask.cancel(true);
        }
    }

    @Override
    public void initActivityPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    /************************
     * Server communication *
     ************************/
    private class GetAllCuisinesTask extends AsyncTask<String, Integer, Response> {

        Context mContext;
        APIInterface mApiInterface;

        public GetAllCuisinesTask(Context context, APIInterface apiInterface) {
            mContext = context;
            mApiInterface = apiInterface;
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
            loadOfflineCuisineData();
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Call<APIResponse<List<Cuisine>>> call = apiInterface.apiGetAllCuisines();

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

                        //Load cuisine data
                        initCuisineData(data.getData());

                        //Store cuisine data into the session
                        SessionManager.setStringSetting(getActivity(), AllConstants.SESSION_KEY_CUISINES, data.toString());
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_info_found) + ",\n" + getResources().getString(R.string.toast_loaded_offline_data), Toast.LENGTH_SHORT).show();
                        loadOfflineCuisineData();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_could_not_retrieve_info) + ",\n" + getResources().getString(R.string.toast_loaded_offline_data), Toast.LENGTH_SHORT).show();
                    loadOfflineCuisineData();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}