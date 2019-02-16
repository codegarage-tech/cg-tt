package tech.codegarage.tidetwist.viewholder;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.reversecoder.library.event.OnSingleClickListener;

import org.parceler.Parcels;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import tech.codegarage.fcm.util.DetailType;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.activity.HomeActivity;
import tech.codegarage.tidetwist.activity.OrderDetailsActivity;
import tech.codegarage.tidetwist.adapter.OrderListAdapter;
import tech.codegarage.tidetwist.enumeration.FlavorType;
import tech.codegarage.tidetwist.enumeration.OrderStatusType;
import tech.codegarage.tidetwist.model.Order;
import tech.codegarage.tidetwist.model.OrderStatus;
import tech.codegarage.tidetwist.model.ParamUpdateOrderStatus;
import tech.codegarage.tidetwist.retrofit.APIClient;
import tech.codegarage.tidetwist.retrofit.APIInterface;
import tech.codegarage.tidetwist.retrofit.APIResponse;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.DateManager;
import tech.codegarage.tidetwist.util.Logger;

import static tech.codegarage.tidetwist.util.AllConstants.INTENT_KEY_DETAIL_TYPE;
import static tech.codegarage.tidetwist.util.AllConstants.INTENT_KEY_ORDER_ITEM;
import static tech.codegarage.tidetwist.util.AllConstants.INTENT_REQUEST_CODE_ORDER_ITEM_DETAIL;
import static tech.codegarage.tidetwist.util.DateManager.yyyy_MM_dd_hh_mm_ss;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class OrderListViewHolder extends BaseViewHolder<Order> {

    private String TAG = OrderListViewHolder.class.getSimpleName();
    private TextView tvOrderId, tvOrderTimestamp, tvLocationSource, tvLocationDestination;
    private ImageView ivKitchen;
    private LinearLayout llOrderLeftButton, llOrderRightButton, llOrderLeftButtonTick, llOrderRightButtonTick;
    private TextView tvOrderLeftButton, tvOrderRightButton;
    private ImageView ivOrderLeftButton, ivOrderRightButton;
    private DoUpdateOrderStatusTask doUpdateOrderStatusTask;

    public OrderListViewHolder(ViewGroup parent) {
        super(parent, R.layout.row_order_list_item);

        ivKitchen = (ImageView) $(R.id.iv_kitchen);
        tvOrderId = (TextView) $(R.id.tv_order_id);
        tvOrderTimestamp = (TextView) $(R.id.tv_order_timestamp);
        tvLocationSource = (TextView) $(R.id.tv_location_source);
        tvLocationDestination = (TextView) $(R.id.tv_location_destination);
        llOrderLeftButton = (LinearLayout) $(R.id.ll_order_left_button);
        llOrderRightButton = (LinearLayout) $(R.id.ll_order_right_button);
        llOrderLeftButtonTick = (LinearLayout) $(R.id.ll_order_left_button_tick);
        llOrderRightButtonTick = (LinearLayout) $(R.id.ll_order_right_button_tick);
        tvOrderLeftButton = (TextView) $(R.id.tv_order_left_button);
        tvOrderRightButton = (TextView) $(R.id.tv_order_right_button);
        ivOrderLeftButton = (ImageView) $(R.id.iv_order_left_button);
        ivOrderRightButton = (ImageView) $(R.id.iv_order_right_button);
    }

    @Override
    public void setData(final Order data) {
        //Set button properties
        setButtonsData(data);

        //Set button actions
        setButtonsAction(data);

        AppUtil.loadImage(getContext(), ivKitchen, data.getManufacturer_image(), false, true, true);
        tvOrderId.setText(data.getOrder_number());
        tvOrderTimestamp.setText(DateManager.getFormattedTimestamp(DateManager.convertDateTimeToMillisecond(data.getOrder_datetime(), yyyy_MM_dd_hh_mm_ss)));
        tvLocationSource.setText(data.getManufacturer_name());
        tvLocationDestination.setText(data.getDelivery_address());

        itemView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intentOrderDetail = new Intent(getContext(), OrderDetailsActivity.class);
                intentOrderDetail.putExtra(INTENT_KEY_ORDER_ITEM, Parcels.wrap(data));
                intentOrderDetail.putExtra(INTENT_KEY_DETAIL_TYPE, DetailType.REGULAR.name());
                ((HomeActivity) getContext()).startActivityForResult(intentOrderDetail, INTENT_REQUEST_CODE_ORDER_ITEM_DETAIL);
            }
        });
    }

    private void setButtonsData(Order data) {
        switch (FlavorType.getFlavor()) {
            case USER:
                llOrderLeftButton.setVisibility(View.GONE);
                llOrderRightButton.setVisibility(View.GONE);
                break;
            case KITCHEN:
                llOrderLeftButton.setVisibility(View.VISIBLE);
                llOrderRightButton.setVisibility(View.VISIBLE);

                //Set button text
                tvOrderLeftButton.setText(getContext().getString(R.string.view_cooking_started));
                tvOrderRightButton.setText(getContext().getString(R.string.view_cooking_finished));

                //Set cooking started state
                OrderStatus orderStatusCookingStarted = getOrderStatus(data.getStatusLists(), OrderStatusType.COOKING_STARTED);
                if (orderStatusCookingStarted != null && orderStatusCookingStarted.getIs_added().equalsIgnoreCase("1")) {
                    ivOrderLeftButton.setBackgroundResource(R.drawable.vector_accepted);
                } else {
                    ivOrderLeftButton.setBackgroundResource(R.drawable.vector_accepted_grey);
                }

                //Set cooking finished state
                OrderStatus orderStatusCookingFinished = getOrderStatus(data.getStatusLists(), OrderStatusType.COOKING_FINISHED);
                if (orderStatusCookingFinished != null && orderStatusCookingFinished.getIs_added().equalsIgnoreCase("1")) {
                    ivOrderRightButton.setBackgroundResource(R.drawable.vector_accepted);
                } else {
                    ivOrderRightButton.setBackgroundResource(R.drawable.vector_accepted_grey);
                }
                break;
            case DRIVER:
                llOrderLeftButton.setVisibility(View.VISIBLE);
                llOrderRightButton.setVisibility(View.VISIBLE);

                //Set button text
                tvOrderLeftButton.setText(getContext().getString(R.string.view_food_picked));
                tvOrderRightButton.setText(getContext().getString(R.string.view_food_delivered));

                //Set food picked state
                OrderStatus orderStatusFoodPicked = getOrderStatus(data.getStatusLists(), OrderStatusType.FOOD_PICKED);
                if (orderStatusFoodPicked != null && orderStatusFoodPicked.getIs_added().equalsIgnoreCase("1")) {
                    ivOrderLeftButton.setBackgroundResource(R.drawable.vector_accepted);
                } else {
                    ivOrderLeftButton.setBackgroundResource(R.drawable.vector_accepted_grey);
                }

                //Set food delivered state
                OrderStatus orderStatusFoodDelivered = getOrderStatus(data.getStatusLists(), OrderStatusType.FOOD_DELIVERED);
                if (orderStatusFoodDelivered != null && orderStatusFoodDelivered.getIs_added().equalsIgnoreCase("1")) {
                    ivOrderRightButton.setBackgroundResource(R.drawable.vector_accepted);
                } else {
                    ivOrderRightButton.setBackgroundResource(R.drawable.vector_accepted_grey);
                }
                break;
        }
    }

    private void setButtonsAction(final Order data) {
        llOrderLeftButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {

                OrderStatus mOrderStatus = null;
                boolean isUpdatable = false;

                switch (FlavorType.getFlavor()) {
                    case USER:
                        break;
                    case KITCHEN:
                        //Cooking started state
                        mOrderStatus = getOrderStatus(data.getStatusLists(), OrderStatusType.COOKING_STARTED);
                        if (mOrderStatus != null) {
                            if (mOrderStatus.getIs_added().equalsIgnoreCase("1")) {
                                Toast.makeText(getContext(), getContext().getString(R.string.toast_the_task_is_completed), Toast.LENGTH_SHORT).show();
                            } else {
                                isUpdatable = true;
                            }
                        }
                        break;
                    case DRIVER:
                        mOrderStatus = getOrderStatus(data.getStatusLists(), OrderStatusType.FOOD_PICKED);
                        if (mOrderStatus != null) {
                            if (mOrderStatus.getIs_added().equalsIgnoreCase("1")) {
                                Toast.makeText(getContext(), getContext().getString(R.string.toast_the_task_is_completed), Toast.LENGTH_SHORT).show();
                            } else {
                                OrderStatus previousStatus = getOrderStatus(data.getStatusLists(), OrderStatusType.COOKING_FINISHED);
                                if (previousStatus != null) {
                                    if (previousStatus.getIs_added().equalsIgnoreCase("1")) {
                                        isUpdatable = true;
                                    } else {
                                        Toast.makeText(getContext(), getContext().getString(R.string.toast_cooking_is_not_finished_yet), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                        break;
                }

                //Send server request
                if (mOrderStatus != null && isUpdatable) {
                    if (doUpdateOrderStatusTask != null && doUpdateOrderStatusTask.getStatus() == AsyncTask.Status.RUNNING) {
                        doUpdateOrderStatusTask.cancel(true);
                    }

                    doUpdateOrderStatusTask = new DoUpdateOrderStatusTask(data, mOrderStatus);
                    doUpdateOrderStatusTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });

        llOrderRightButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {

                OrderStatus mOrderStatus = null;
                boolean isUpdatable = false;

                switch (FlavorType.getFlavor()) {
                    case USER:
                        break;
                    case KITCHEN:
                        //Cooking finished state
                        mOrderStatus = getOrderStatus(data.getStatusLists(), OrderStatusType.COOKING_FINISHED);
                        if (mOrderStatus != null) {
                            if (mOrderStatus.getIs_added().equalsIgnoreCase("1")) {
                                Toast.makeText(getContext(), getContext().getString(R.string.toast_the_task_is_completed), Toast.LENGTH_SHORT).show();
                            } else {
                                OrderStatus previousStatus = getOrderStatus(data.getStatusLists(), OrderStatusType.COOKING_STARTED);
                                if (previousStatus != null) {
                                    if (previousStatus.getIs_added().equalsIgnoreCase("1")) {
                                        isUpdatable = true;
                                    } else {
                                        Toast.makeText(getContext(), getContext().getString(R.string.toast_cooking_is_not_started_yet), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                        break;
                    case DRIVER:
                        mOrderStatus = getOrderStatus(data.getStatusLists(), OrderStatusType.FOOD_DELIVERED);
                        if (mOrderStatus != null) {
                            if (mOrderStatus.getIs_added().equalsIgnoreCase("1")) {
                                Toast.makeText(getContext(), getContext().getString(R.string.toast_the_task_is_completed), Toast.LENGTH_SHORT).show();
                            } else {
                                OrderStatus previousStatus = getOrderStatus(data.getStatusLists(), OrderStatusType.FOOD_PICKED);
                                if (previousStatus != null) {
                                    if (previousStatus.getIs_added().equalsIgnoreCase("1")) {
                                        isUpdatable = true;
                                    } else {
                                        Toast.makeText(getContext(), getContext().getString(R.string.toast_food_is_not_picked_yet), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                        break;
                }

                //Send server request
                if (mOrderStatus != null && isUpdatable) {
                    if (doUpdateOrderStatusTask != null && doUpdateOrderStatusTask.getStatus() == AsyncTask.Status.RUNNING) {
                        doUpdateOrderStatusTask.cancel(true);
                    }

                    doUpdateOrderStatusTask = new DoUpdateOrderStatusTask(data, mOrderStatus);
                    doUpdateOrderStatusTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });
    }

    private OrderStatus getOrderStatus(List<OrderStatus> orderStatuses, OrderStatusType orderStatusType) {
        if (orderStatuses.size() > 0) {
            for (OrderStatus orderStatus : orderStatuses) {
                if (OrderStatusType.valueOf(orderStatus.getStatus()) == orderStatusType) {
                    return orderStatus;
                }
            }
        }
        return null;
    }

    /************************
     * Server communication *
     ************************/
    private class DoUpdateOrderStatusTask extends AsyncTask<String, Integer, Response> {

        Order mOrder;
        OrderStatus mOrderStatus;

        public DoUpdateOrderStatusTask(Order order, OrderStatus orderStatus) {
            mOrder = order;
            mOrderStatus = orderStatus;
        }

        @Override
        protected void onPreExecute() {
            ProgressDialog progressDialog = ((HomeActivity) getContext()).showProgressDialog();
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
                APIInterface mApiInterface = APIClient.getClient(getContext()).create(APIInterface.class);

                ParamUpdateOrderStatus mParamUpdateOrderStatus = new ParamUpdateOrderStatus(mOrder.getOrder_id(), mOrderStatus.getId());
                Logger.d(TAG, TAG + " >>> " + "mParamUpdateOrderStatus: " + mParamUpdateOrderStatus.toString());
                Call<APIResponse> call = mApiInterface.apiUpdateOrderStatus(mParamUpdateOrderStatus);
                Logger.d(TAG, TAG + " >>> call" + call.toString() + "");

                Response response = call.execute();
                Logger.d(TAG, TAG + " >>> response" + call.toString() + "");
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
                ((HomeActivity) getContext()).dismissProgressDialog();

                if (result != null && result.isSuccessful()) {
                    Logger.d(TAG, "APIResponse(DoUpdateOrderStatusTask): onResponse-server = " + result.toString());
                    APIResponse data = (APIResponse) result.body();
                    Logger.d(TAG, "APIResponse(DoUpdateOrderStatusTask): data = " + data.toString() + "");

                    if (data != null) {
                        Logger.d(TAG, "APIResponse(DoUpdateOrderStatusTask()): onResponse-object = " + data.toString());

                        if (data.getStatus().equalsIgnoreCase("1")) {
                            //Updated order status into the order list
                            Logger.d(TAG, "APIResponse(DoUpdateOrderStatusTask): updating order status into order list");
                            Logger.d(TAG, "APIResponse(DoUpdateOrderStatusTask): mOrder = " + mOrder.toString() + "");
                            Logger.d(TAG, "APIResponse(DoUpdateOrderStatusTask): mOrderStatus = " + mOrderStatus.toString() + "");

                            mOrderStatus.setIs_added("1");
                            ((OrderListAdapter) getOwnerAdapter()).notifyDataSetChanged();

                            Logger.d(TAG, "APIResponse(DoUpdateOrderStatusTask): updated mOrder = " + mOrder.toString() + "");
                            Logger.d(TAG, "APIResponse(DoUpdateOrderStatusTask): updated mOrderStatus = " + mOrderStatus.toString() + "");
                        }
                        Toast.makeText(getContext(), data.getMsg(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.toast_no_info_found), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}