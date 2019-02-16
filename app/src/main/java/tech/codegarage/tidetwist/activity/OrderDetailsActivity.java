package tech.codegarage.tidetwist.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.util.AllSettingsManager;

import org.parceler.Parcels;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import tech.codegarage.fcm.payload.Payload;
import tech.codegarage.fcm.util.DetailType;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.adapter.OrderDetailStatusAdapter;
import tech.codegarage.tidetwist.base.BaseLocationActivity;
import tech.codegarage.tidetwist.base.BaseResultReceiver;
import tech.codegarage.tidetwist.enumeration.FlavorType;
import tech.codegarage.tidetwist.enumeration.OrderStatusType;
import tech.codegarage.tidetwist.model.FcmOrderNotification;
import tech.codegarage.tidetwist.model.Order;
import tech.codegarage.tidetwist.model.OrderItem;
import tech.codegarage.tidetwist.model.OrderStatus;
import tech.codegarage.tidetwist.model.ParamUpdateOrderStatus;
import tech.codegarage.tidetwist.retrofit.APIClient;
import tech.codegarage.tidetwist.retrofit.APIInterface;
import tech.codegarage.tidetwist.retrofit.APIResponse;
import tech.codegarage.tidetwist.service.DriverJobIntentService;
import tech.codegarage.tidetwist.util.AllConstants;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.Logger;
import tech.codegarage.tidetwist.view.CanaroTextView;

import static tech.codegarage.fcm.util.FcmUtil.INTENT_KEY_MESSAGE;
import static tech.codegarage.tidetwist.base.BaseJobIntentService.KEY_TASK_RESULT;
import static tech.codegarage.tidetwist.util.AllConstants.INTENT_KEY_ORDER_ITEM;
import static tech.codegarage.tidetwist.util.AppUtil.isSimSupport;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class OrderDetailsActivity extends BaseLocationActivity {

    //Toolbar
    private ImageView ivBack;
    private CanaroTextView tvTitle;

    private DetailType mDetailType;
    private LinearLayout llOrderItem;
    private RecyclerView rvOrderDetailStatus;
    private OrderDetailStatusAdapter orderDetailStatusAdapter;
    private Order mOrder;
    private TextView tvDeliveryName, tvDeliveryAddress, tvOrderTime, tvDeliveryTime, tvDeliveryPhone, tvDriverName, tvKitchenName, tvDriverPhone, tvKitchenPhone, tvSubtotal, tvPromotionalDiscount, tvShippingCost, tvTotal;
    private LinearLayout llDeliveryPhone, llDriverPhone, llKitchenPhone, llOrderActions, llOrderLeftButton, llOrderRightButton, llOrderLeftButtonTick, llOrderRightButtonTick;
    private TextView tvOrderLeftButton, tvOrderRightButton;
    private ImageView ivUserImage, ivDriverImage, ivKitchenImage, ivOrderLeftButton, ivOrderRightButton;
    private RelativeLayout rlDeliveryInfo, rlDriverInfo, rlKitchenInfo;

    //Background tasks
    private DoUpdateOrderStatusTask doUpdateOrderStatusTask;

    @Override
    public LOCATION_UPDATE_FREQUENCY initLocationUpdateFrequency() {
        return (FlavorType.getFlavor() == FlavorType.DRIVER) ? LOCATION_UPDATE_FREQUENCY.FREQUENTLY : LOCATION_UPDATE_FREQUENCY.ONCE;
    }

    @Override
    public String[] initActivityPermissions() {
        return new String[]{Manifest.permission.CALL_PHONE};
    }

    @Override
    public int initActivityLayout() {
        return R.layout.activity_order_details;
    }

    @Override
    public void onLocationFound(Location location) {
        if (location != null) {
            //Check internet connection
            if (NetworkManager.isConnected(getActivity())) {
                switch (FlavorType.getFlavor()) {
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
                }
            }
        }
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
            String mParcelableDetailType = intent.getStringExtra(AllConstants.INTENT_KEY_DETAIL_TYPE);
            if (!AllSettingsManager.isNullOrEmpty(mParcelableDetailType)) {
                mDetailType = DetailType.valueOf(mParcelableDetailType);
                Logger.d(TAG, TAG + " >>> " + "mDetailType: " + mDetailType);

                switch (mDetailType) {
                    case REGULAR:
                        Parcelable mParcelableOrder = intent.getParcelableExtra(INTENT_KEY_ORDER_ITEM);
                        if (mParcelableOrder != null) {
                            mOrder = Parcels.unwrap(mParcelableOrder);
                            Logger.d(TAG, TAG + " >>> " + "mOrder: " + mOrder.toString());
                        }
                        break;
                    case FCM:
                        Order order = processNotificationData(intent);
                        if (order != null) {
                            mOrder = order;
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void initActivityViews() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (CanaroTextView) findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.view_order_id) + " " + mOrder.getOrder_number());

        rvOrderDetailStatus = (RecyclerView) findViewById(R.id.rv_order_detail_status);
        llOrderItem = (LinearLayout) findViewById(R.id.ll_order_item);
        tvSubtotal = (TextView) findViewById(R.id.tv_subtotal);
        tvPromotionalDiscount = (TextView) findViewById(R.id.tv_promotional_discount);
        tvShippingCost = (TextView) findViewById(R.id.tv_shipping_cost);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        llOrderActions = (LinearLayout) findViewById(R.id.ll_order_actions);
        llOrderLeftButton = (LinearLayout) findViewById(R.id.ll_order_left_button);
        llOrderRightButton = (LinearLayout) findViewById(R.id.ll_order_right_button);
        llOrderLeftButtonTick = (LinearLayout) findViewById(R.id.ll_order_left_button_tick);
        llOrderRightButtonTick = (LinearLayout) findViewById(R.id.ll_order_right_button_tick);
        tvOrderLeftButton = (TextView) findViewById(R.id.tv_order_left_button);
        tvOrderRightButton = (TextView) findViewById(R.id.tv_order_right_button);
        ivOrderLeftButton = (ImageView) findViewById(R.id.iv_order_left_button);
        ivOrderRightButton = (ImageView) findViewById(R.id.iv_order_right_button);

        rlDeliveryInfo = (RelativeLayout) findViewById(R.id.rl_delivery_info);
        rlDriverInfo = (RelativeLayout) findViewById(R.id.rl_driver_info);
        rlKitchenInfo = (RelativeLayout) findViewById(R.id.rl_kitchen_info);
        ivUserImage = (ImageView) findViewById(R.id.iv_user_image);
        ivDriverImage = (ImageView) findViewById(R.id.iv_driver_image);
        ivKitchenImage = (ImageView) findViewById(R.id.iv_kitchen_image);
        tvDeliveryName = (TextView) findViewById(R.id.tv_delivery_name);
        tvDriverName = (TextView) findViewById(R.id.tv_driver_name);
        tvKitchenName = (TextView) findViewById(R.id.tv_kitchen_name);
        tvDeliveryAddress = (TextView) findViewById(R.id.tv_delivery_address);
        tvOrderTime = (TextView) findViewById(R.id.tv_order_time);
        tvDeliveryTime = (TextView) findViewById(R.id.tv_delivery_time);
        tvDeliveryPhone = (TextView) findViewById(R.id.tv_delivery_phone);
        tvDriverPhone = (TextView) findViewById(R.id.tv_driver_phone);
        tvKitchenPhone = (TextView) findViewById(R.id.tv_kitchen_phone);
        llDeliveryPhone = (LinearLayout) findViewById(R.id.ll_delivery_phone);
        llDriverPhone = (LinearLayout) findViewById(R.id.ll_driver_phone);
        llKitchenPhone = (LinearLayout) findViewById(R.id.ll_kitchen_phone);
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {
        if (mOrder != null) {
            initOrderInfo(mOrder);

            initOrderActions(mOrder);

            initOrderDetailStatus(mOrder);

            initOrderDetailItems(mOrder.getItems());

            setPriceInformation(mOrder);

            //Register notification broadcast
            Logger.d(TAG, TAG + ">> FCM: notification update is registered in order detail");
            Payload.registerPayloadUpdate(getActivity(), notificationUpdate);
        }
    }

    private void initOrderDetailItems(List<OrderItem> orderItems) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (orderItems != null && orderItems.size() > 0) {
            llOrderItem.removeAllViews();

            for (int i = 0; i < orderItems.size(); i++) {
                OrderItem orderItem = orderItems.get(i);

                View orderItemView = layoutInflater.inflate(R.layout.row_order_detail_food_item, null);
                ImageView itemImage = ((ImageView) orderItemView.findViewById(R.id.iv_order_detail_item_image));
                AppUtil.loadImage(getActivity(), itemImage, orderItem.getImage(), false, true, true);
                ((TextView) orderItemView.findViewById(R.id.tv_order_detail_item_name)).setText(orderItem.getName());
                ((TextView) orderItemView.findViewById(R.id.tv_order_detail_item_calculation)).setText(getString(R.string.view_first_bracket_left) + orderItem.getQty() + " " + getString(R.string.view_x) + " " + orderItem.getPrice() + getString(R.string.view_first_bracket_right) + " " + getString(R.string.view_tk));
                ((TextView) orderItemView.findViewById(R.id.tv_order_detail_item_total)).setText(getItemTotalPrice(orderItem) + " " + getString(R.string.view_tk));

                llOrderItem.addView(orderItemView);
            }
        }
    }

    private void initOrderDetailStatus(Order order) {
        orderDetailStatusAdapter = new OrderDetailStatusAdapter(getActivity());
        rvOrderDetailStatus.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Disable nested scrolling
        rvOrderDetailStatus.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(rvOrderDetailStatus, false);

        rvOrderDetailStatus.setAdapter(orderDetailStatusAdapter);
        orderDetailStatusAdapter.addAll(order.getStatusLists());
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
        Intent resultIntent = new Intent();
        resultIntent.putExtra(AllConstants.INTENT_KEY_ORDER_ITEM, Parcels.wrap(mOrder));
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void initActivityDestroyTasks() {
        //Unregister notification broadcast
        Logger.d(TAG, TAG + ">> FCM: notification update is unregistered in order detail");
        Payload.unregisterPayloadUpdate(getActivity(), notificationUpdate);

        dismissProgressDialog();

        if (doUpdateOrderStatusTask != null && doUpdateOrderStatusTask.getStatus() == AsyncTask.Status.RUNNING) {
            doUpdateOrderStatusTask.cancel(true);
        }
    }

    @Override
    public void initActivityPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    /*****************************
     * Order notification update *
     *****************************/
    BroadcastReceiver notificationUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Order order = processNotificationData(intent);
            if (order != null) {
                mOrder = order;

                //Update button state for driver and kitchen
                if (FlavorType.getFlavor() != FlavorType.USER) {
                    initOrderActions(mOrder);
                }

                //Update order info
                initOrderInfo(mOrder);

                //Update order status list
                orderDetailStatusAdapter.addAll(mOrder.getStatusLists());
            }
        }
    };

    private Order processNotificationData(Intent intent) {
        Logger.d(TAG, TAG + ">> FCM: notification update is found in order detail");

        //Extract data after getting notification
        if (intent != null) {
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
            }
        }
        return null;
    }

    /**********************
     * Order Info methods *
     **********************/
    private void initOrderInfo(final Order data) {
        AppUtil.loadImage(getActivity(), ivUserImage, data.getApp_user_image(), false, true, true);
        AppUtil.loadImage(getActivity(), ivDriverImage, data.getDrivers_image(), false, true, true);
        AppUtil.loadImage(getActivity(), ivKitchenImage, data.getManufacturer_image(), false, true, true);
        tvDeliveryName.setText(data.getDelivery_name());
        tvDriverName.setText(data.getDrivers_first_name() + " " + data.getDrivers_last_name());
        tvKitchenName.setText(data.getManufacturer_name());
        tvDeliveryAddress.setText(data.getDelivery_address());
        tvOrderTime.setText(data.getOrder_datetime());
        tvDeliveryTime.setText(data.getDelivery_time());

        //Set delivery phone button listener
        if (!AllSettingsManager.isNullOrEmpty(data.getDelivery_phone())) {
            llDeliveryPhone.setVisibility(View.VISIBLE);
            tvDeliveryPhone.setText(data.getDelivery_phone().trim());
            llDeliveryPhone.setOnClickListener(new OnBaseClickListener() {
                @Override
                public void OnPermissionValidation(View view) {
                    if (isSimSupport(getActivity())) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + data.getDelivery_phone().trim()));
                        startActivity(callIntent);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.toast_your_sim_card_is_absent), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            llDeliveryPhone.setVisibility(View.GONE);
        }
        //Set driver phone button listener
        if (!AllSettingsManager.isNullOrEmpty(data.getDrivers_phone())) {
            llDriverPhone.setVisibility(View.VISIBLE);
            tvDriverPhone.setText(data.getDrivers_phone().trim());
            llDriverPhone.setOnClickListener(new OnBaseClickListener() {
                @Override
                public void OnPermissionValidation(View view) {
                    if (isSimSupport(getActivity())) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + data.getDrivers_phone().trim()));
                        startActivity(callIntent);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.toast_your_sim_card_is_absent), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            llDriverPhone.setVisibility(View.GONE);
        }
        //Set manufacturer phone button listener
        if (!AllSettingsManager.isNullOrEmpty(data.getManufacturer_phone())) {
            llKitchenPhone.setVisibility(View.VISIBLE);
            tvKitchenPhone.setText(data.getManufacturer_phone().trim());
            llKitchenPhone.setOnClickListener(new OnBaseClickListener() {
                @Override
                public void OnPermissionValidation(View view) {
                    if (isSimSupport(getActivity())) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + data.getManufacturer_phone().trim()));
                        startActivity(callIntent);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.toast_your_sim_card_is_absent), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            llKitchenPhone.setVisibility(View.GONE);
        }

        switch (FlavorType.getFlavor()) {
            case USER:
                if (isOrderAccepted(data)) {
                    rlDriverInfo.setVisibility(View.VISIBLE);
                } else {
                    rlDriverInfo.setVisibility(View.GONE);
                }
                rlDeliveryInfo.setVisibility(View.VISIBLE);
                rlKitchenInfo.setVisibility(View.VISIBLE);
                llKitchenPhone.setVisibility(View.GONE);
                llDeliveryPhone.setVisibility(View.GONE);
                break;
            case KITCHEN:
                rlDeliveryInfo.setVisibility(View.VISIBLE);
                rlDriverInfo.setVisibility(View.VISIBLE);
                rlKitchenInfo.setVisibility(View.GONE);
                llDeliveryPhone.setVisibility(View.GONE);
                break;
            case DRIVER:
                rlDeliveryInfo.setVisibility(View.VISIBLE);
                rlDriverInfo.setVisibility(View.GONE);
                rlKitchenInfo.setVisibility(View.VISIBLE);
                break;
        }
    }

    /*************************
     * Order actions methods *
     *************************/
    private void initOrderActions(Order data) {
        setButtonsData(data);
        setButtonsAction(data);
    }

    private void setButtonsData(Order data) {
        switch (FlavorType.getFlavor()) {
            case USER:
                llOrderActions.setVisibility(View.GONE);
                break;
            case KITCHEN:
                llOrderActions.setVisibility(View.VISIBLE);

                //Set button text
                tvOrderLeftButton.setText(getString(R.string.view_cooking_started));
                tvOrderRightButton.setText(getString(R.string.view_cooking_finished));

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
                llOrderActions.setVisibility(View.VISIBLE);

                //Set button text
                tvOrderLeftButton.setText(getString(R.string.view_food_picked));
                tvOrderRightButton.setText(getString(R.string.view_food_delivered));

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
                                Toast.makeText(getActivity(), getString(R.string.toast_the_task_is_completed), Toast.LENGTH_SHORT).show();
                            } else {
                                isUpdatable = true;
                            }
                        }
                        break;
                    case DRIVER:
                        mOrderStatus = getOrderStatus(data.getStatusLists(), OrderStatusType.FOOD_PICKED);
                        if (mOrderStatus != null) {
                            if (mOrderStatus.getIs_added().equalsIgnoreCase("1")) {
                                Toast.makeText(getActivity(), getString(R.string.toast_the_task_is_completed), Toast.LENGTH_SHORT).show();
                            } else {
                                OrderStatus previousStatus = getOrderStatus(data.getStatusLists(), OrderStatusType.COOKING_FINISHED);
                                if (previousStatus != null) {
                                    if (previousStatus.getIs_added().equalsIgnoreCase("1")) {
                                        isUpdatable = true;
                                    } else {
                                        Toast.makeText(getActivity(), getString(R.string.toast_cooking_is_not_finished_yet), Toast.LENGTH_SHORT).show();
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

                    doUpdateOrderStatusTask = new DoUpdateOrderStatusTask(mOrder, mOrderStatus);
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
                                Toast.makeText(getActivity(), getString(R.string.toast_the_task_is_completed), Toast.LENGTH_SHORT).show();
                            } else {
                                OrderStatus previousStatus = getOrderStatus(data.getStatusLists(), OrderStatusType.COOKING_STARTED);
                                if (previousStatus != null) {
                                    if (previousStatus.getIs_added().equalsIgnoreCase("1")) {
                                        isUpdatable = true;
                                    } else {
                                        Toast.makeText(getActivity(), getString(R.string.toast_cooking_is_not_started_yet), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                        break;
                    case DRIVER:
                        mOrderStatus = getOrderStatus(data.getStatusLists(), OrderStatusType.FOOD_DELIVERED);
                        if (mOrderStatus != null) {
                            if (mOrderStatus.getIs_added().equalsIgnoreCase("1")) {
                                Toast.makeText(getActivity(), getString(R.string.toast_the_task_is_completed), Toast.LENGTH_SHORT).show();
                            } else {
                                OrderStatus previousStatus = getOrderStatus(data.getStatusLists(), OrderStatusType.FOOD_PICKED);
                                if (previousStatus != null) {
                                    if (previousStatus.getIs_added().equalsIgnoreCase("1")) {
                                        isUpdatable = true;
                                    } else {
                                        Toast.makeText(getActivity(), getString(R.string.toast_food_is_not_picked_yet), Toast.LENGTH_SHORT).show();
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

    public boolean isOrderAccepted(final Order data) {
        OrderStatus orderStatusAccepted = getOrderStatus(data.getStatusLists(), OrderStatusType.ORDER_ACCEPTED);
        if (orderStatusAccepted != null && orderStatusAccepted.getIs_added().equalsIgnoreCase("1")) {
            return true;
        } else {
            return false;
        }
    }

    /*************************
     * Food item calculation *
     *************************/
    private void setPriceInformation(Order order) {
        float subtotal = formatFloat(getSubtotalPrice(order.getItems()));
        float shippingCost = formatFloat(Float.parseFloat(getString(R.string.view_sixty)));
        float total = getTotalPrice(subtotal, Float.parseFloat(order.getPromotional_discount_percentage()), shippingCost);
        tvSubtotal.setText(subtotal + " " + getString(R.string.view_tk));
        tvPromotionalDiscount.setText(getString(R.string.view_first_bracket_left) + order.getPromotional_discount_percentage() + getString(R.string.view_percentage) + getString(R.string.view_first_bracket_right) + getPromotionalDiscountPrice(subtotal, Float.parseFloat(order.getPromotional_discount_percentage())) + " " + getString(R.string.view_tk));
        tvShippingCost.setText(shippingCost + " " + getString(R.string.view_tk));
        tvTotal.setText(total + " " + getString(R.string.view_tk));
    }

    private float getItemTotalPrice(OrderItem orderItem) {
        float totalPrice = 0.0f;
        if (orderItem != null) {
            if (!AllSettingsManager.isNullOrEmpty(orderItem.getQty()) && !AllSettingsManager.isNullOrEmpty(orderItem.getPrice())) {
                try {
                    int mQuantity = Integer.parseInt(orderItem.getQty());
                    float mPrice = Float.parseFloat(orderItem.getPrice());
                    totalPrice = mQuantity * mPrice;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    totalPrice = 0.0f;
                }
            }
        }
        return totalPrice;
    }

    private float getSubtotalPrice(List<OrderItem> orderItems) {
        float subTotalPrice = 0.0f;
        if (orderItems != null && orderItems.size() > 0) {
            try {
                List<OrderItem> mOrderItems = orderItems;
                for (int i = 0; i < mOrderItems.size(); i++) {
                    OrderItem mOrderItem = mOrderItems.get(i);
                    float itemPrice = getItemTotalPrice(mOrderItem);
                    Logger.d(TAG, TAG + ">> " + "total(itemPrice): " + mOrderItem.getPrice());
                    Logger.d(TAG, TAG + ">> " + "total(itemQuantity): " + mOrderItem.getQty());
                    Logger.d(TAG, TAG + ">> " + "total(priceWithQuantity): " + itemPrice);
                    subTotalPrice = subTotalPrice + itemPrice;
                    Logger.d(TAG, TAG + ">> " + "total(subTotalPrice): " + subTotalPrice);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                subTotalPrice = 0.0f;
            }
        }
        return subTotalPrice;
    }

    private float getPromotionalDiscountPrice(float subtotal, float promotionalDiscount) {
        float mDiscountPrice = 0.0f;
        if (promotionalDiscount > 0) {
            mDiscountPrice = ((subtotal * promotionalDiscount) / 100);
        }

        return mDiscountPrice;
    }

    private float getTotalPrice(float subtotal, float promotionalDiscount, float shippingCost) {
        float tempSubTotalPrice = subtotal - getPromotionalDiscountPrice(subtotal, promotionalDiscount);
//        if (promotionalDiscount > 0) {
//            float discount = ((subtotal * promotionalDiscount) / 100);
//            tempSubTotalPrice = subtotal - discount;
//        } else {
//            tempSubTotalPrice = subtotal;
//        }

        return tempSubTotalPrice + shippingCost;
    }

    private float formatFloat(float value) {
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
                APIInterface mApiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

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
                dismissProgressDialog();

                if (result != null && result.isSuccessful()) {
                    Logger.d(TAG, "APIResponse(DoUpdateOrderStatusTask): onResponse-server = " + result.toString());
                    APIResponse data = (APIResponse) result.body();
                    Logger.d(TAG, "APIResponse(DoUpdateOrderStatusTask): data = " + data.toString() + "");

                    if (data != null) {
                        Logger.d(TAG, "APIResponse(DoUpdateOrderStatusTask()): onResponse-object = " + data.toString());

                        if (data.getStatus().equalsIgnoreCase("1")) {
                            //Updated order status into the order list
                            Logger.d(TAG, "APIResponse(DoUpdateOrderStatusTask): updating order status into order detail");
                            Logger.d(TAG, "APIResponse(DoUpdateOrderStatusTask): mOrder = " + mOrder.toString() + "");
                            Logger.d(TAG, "APIResponse(DoUpdateOrderStatusTask): mOrderStatus = " + mOrderStatus.toString() + "");

                            //Update order detail status list
                            mOrderStatus.setIs_added("1");
                            orderDetailStatusAdapter.notifyDataSetChanged();

                            Logger.d(TAG, "APIResponse(DoUpdateOrderStatusTask): updated mOrder = " + mOrder.toString() + "");
                            Logger.d(TAG, "APIResponse(DoUpdateOrderStatusTask): updated mOrderStatus = " + mOrderStatus.toString() + "");

                            //Update action button state
                            if (FlavorType.getFlavor() != FlavorType.USER) {
                                initOrderActions(mOrder);
                            }
                        }
                        Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.toast_no_info_found), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}