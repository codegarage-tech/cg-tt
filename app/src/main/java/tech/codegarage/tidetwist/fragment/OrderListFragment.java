package tech.codegarage.tidetwist.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.jude.easyrecyclerview.waveswiperefresh.WaveSwipeRefreshLayout;
import com.jude.easyrecyclerview.waveswiperefresh.WaveSwipeRefreshRecyclerView;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.util.AllSettingsManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import tech.codegarage.fcm.payload.Payload;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.adapter.OrderListAdapter;
import tech.codegarage.tidetwist.base.BaseFragment;
import tech.codegarage.tidetwist.enumeration.FlavorType;
import tech.codegarage.tidetwist.model.AppUser;
import tech.codegarage.tidetwist.model.DriverUser;
import tech.codegarage.tidetwist.model.FcmOrderNotification;
import tech.codegarage.tidetwist.model.KitchenUser;
import tech.codegarage.tidetwist.model.Order;
import tech.codegarage.tidetwist.retrofit.APIClient;
import tech.codegarage.tidetwist.retrofit.APIInterface;
import tech.codegarage.tidetwist.retrofit.APIResponse;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.Logger;

import static tech.codegarage.fcm.util.FcmUtil.INTENT_KEY_MESSAGE;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class OrderListFragment extends BaseFragment {

    private WaveSwipeRefreshRecyclerView rvOrder;
    private OrderListAdapter orderListAdapter;
    private AppUser mAppUser;
    private KitchenUser mKitchenUser;
    private DriverUser mDriverUser;
    private boolean isSwipeRefreshTask = false;

    //Background task
    private APIInterface mApiInterface;
    private GetOrderListTask getOrderListTask;

    public static OrderListFragment newInstance() {
        OrderListFragment fragment = new OrderListFragment();
        return fragment;
    }

    @Override
    public int initFragmentLayout() {
        return R.layout.fragment_order_list;
    }

    @Override
    public void initFragmentBundleData(Bundle bundle) {
    }

    @Override
    public void initFragmentViews(View parentView) {
        rvOrder = (WaveSwipeRefreshRecyclerView) parentView.findViewById(R.id.rv_order);
    }

    @Override
    public void initFragmentViewsData() {
        mApiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        switch (FlavorType.getFlavor()) {
            case USER:
                mAppUser = AppUtil.getAppUser(getActivity());
                Logger.d(TAG, TAG + " >>> " + "mAppUser: " + mAppUser.toString());
                break;
            case KITCHEN:
                mKitchenUser = AppUtil.getKitchenUser(getActivity());
                Logger.d(TAG, TAG + " >>> " + "mKitchenUser: " + mKitchenUser.toString());
                break;
            case DRIVER:
                mDriverUser = AppUtil.getDriverUser(getActivity());
                Logger.d(TAG, TAG + " >>> " + "mDriverUser: " + mDriverUser.toString());
                break;
        }

        orderListAdapter = new OrderListAdapter(getActivity());
        rvOrder.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvOrder.setAdapter(orderListAdapter);
        rvOrder.setRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!NetworkManager.isConnected(getActivity())) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    rvOrder.setRefreshing(false);
                    return;
                }

                if (getOrderListTask != null && getOrderListTask.getStatus() == AsyncTask.Status.RUNNING) {
                    getOrderListTask.cancel(true);
                }

                isSwipeRefreshTask = true;
                getOrderListTask = new GetOrderListTask(getActivity());
                getOrderListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        if (!NetworkManager.isConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
            return;
        }
        getOrderListTask = new GetOrderListTask(getActivity());
        getOrderListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //Register notification broadcast
        Logger.d(TAG, TAG + ">> FCM: notification update is registered in order list");
        Payload.registerPayloadUpdate(getActivity(), notificationUpdate);
    }

    @Override
    public void initFragmentActions() {

    }

    @Override
    public void initFragmentBackPress() {

    }

    @Override
    public void initFragmentUpdate(Object object) {

    }

    @Override
    public void initFragmentOnResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onDestroy() {
        if (getOrderListTask != null && getOrderListTask.getStatus() == AsyncTask.Status.RUNNING) {
            getOrderListTask.cancel(true);
        }

        //Unregister notification broadcast
        Logger.d(TAG, TAG + ">> FCM: notification update is unregistered in order list");
        Payload.unregisterPayloadUpdate(getActivity(), notificationUpdate);

        super.onDestroy();
    }

    /*****************************
     * Order notification update *
     *****************************/
    BroadcastReceiver notificationUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Order order = processNotificationData(intent);
            if (order != null) {
                //Update order list
                Logger.d(TAG, TAG + " >>> " + "fcmOrderNotification: Updating order list");
                orderListAdapter.updateItem(order);
            }
        }
    };

    private Order processNotificationData(Intent intent) {
        Logger.d(TAG, TAG + ">> FCM: notification update is found in order fragment");

        //Extract data after getting notification
        if (intent != null) {
            Logger.d(TAG, TAG + ">> FCM: found intent");
            String fcmNotificationData = intent.getStringExtra(INTENT_KEY_MESSAGE);
            if (!AllSettingsManager.isNullOrEmpty(fcmNotificationData)) {
                Logger.d(TAG, TAG + " >>> " + "FCM(fcmOrderNotification-string): " + fcmNotificationData.toString());
                FcmOrderNotification fcmOrderNotification = APIResponse.getResponseObject(fcmNotificationData, FcmOrderNotification.class);
                Logger.d(TAG, TAG + " >>> " + "FCM(fcmOrderNotification): " + fcmOrderNotification.toString());

                //Pick order item
                if (fcmOrderNotification != null) {
                    Order order = fcmOrderNotification.getOrder();
                    Logger.d(TAG, TAG + " >>> " + "FCM(order): " + order.toString());
                    return order;
                }
            } else {
                Logger.d(TAG, TAG + ">> FCM: could not retrieve intent data");
            }
        } else {
            Logger.d(TAG, TAG + ">> FCM: found null intent");
        }
        return null;
    }

    public void updateOrderList(Order order) {
        Logger.d(TAG, TAG + " >>> " + "back from order detail: Updating order list");
        orderListAdapter.updateItem(order);
    }

    /************************
     * Server communication *
     ************************/
    private class GetOrderListTask extends AsyncTask<String, Integer, Response> {

        Context mContext;

        public GetOrderListTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            if (!isSwipeRefreshTask) {
                showProgressDialog();
            }
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Call<APIResponse<List<Order>>> call = null;
                switch (FlavorType.getFlavor()) {
                    case USER:
                        call = mApiInterface.apiGetOrderListsByUser(mAppUser.getApp_user_id());
                        break;
                    case KITCHEN:
                        call = mApiInterface.apiGetOrderListsByKitchen(mKitchenUser.getManufacturer_id());
                        break;
                    case DRIVER:
                        call = mApiInterface.apiGetOrderListsByDriver(mDriverUser.getId());
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
                if (!isSwipeRefreshTask) {
                    dismissProgressDialog();
                }
                isSwipeRefreshTask = false;
                rvOrder.setRefreshing(false);

                if (result != null && result.isSuccessful()) {
                    Logger.d(TAG, "APIResponse(GetOrderListTask): onResponse-server = " + result.toString());
                    APIResponse<List<Order>> data = (APIResponse<List<Order>>) result.body();
                    Logger.d(TAG, "APIResponse(GetOrderListTask): data = " + data.toString() + "");

                    if (data != null && data.getStatus().equalsIgnoreCase("1")) {
                        Logger.d(TAG, "APIResponse(GetOrderListTask()): onResponse-object = " + data.toString());
                        if (data.getData().size() > 0) {
                            orderListAdapter.clear();
                            orderListAdapter.addAll(data.getData());
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