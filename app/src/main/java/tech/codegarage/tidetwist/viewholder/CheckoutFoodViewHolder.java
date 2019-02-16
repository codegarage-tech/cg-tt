package tech.codegarage.tidetwist.viewholder;

import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.reversecoder.library.bang.SmallBang;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.random.RandomManager;
import com.reversecoder.library.util.AllSettingsManager;

import me.wangyuwei.shoppoing.ShoppingView;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.activity.CheckoutActivity;
import tech.codegarage.tidetwist.adapter.CheckoutFoodAdapter;
import tech.codegarage.tidetwist.dialog.RemoveFoodFromCartDialog;
import tech.codegarage.tidetwist.model.FoodItem;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.Logger;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class CheckoutFoodViewHolder extends BaseViewHolder<FoodItem> {

    private TextView tvFoodItemName, tvFoodItemPrice;
    private ImageView ivFoodItem, ivFoodItemFavourite;
    private RelativeLayout rlFavourite;
    private SmallBang mSmallBang;
    private ShoppingView svAddToCart;
    private LinearLayout llRemoveFoodItem;
    private static String TAG = CheckoutFoodViewHolder.class.getSimpleName();

    public CheckoutFoodViewHolder(ViewGroup parent) {
        super(parent, R.layout.row_checkout_food);

        tvFoodItemName = (TextView) $(R.id.tv_checkout_food_name);
        tvFoodItemPrice = (TextView) $(R.id.tv_checkout_food_price);
        ivFoodItem = (ImageView) $(R.id.iv_checkout_food_image);
        svAddToCart = (ShoppingView) $(R.id.sv_add_to_cart);
        llRemoveFoodItem = (LinearLayout) $(R.id.ll_remove_food);
    }

    @Override
    public void setData(final FoodItem data) {
        tvFoodItemName.setText(data.getName());
        AppUtil.loadImage(getContext(), ivFoodItem, ((data.getImage() != null && data.getImage().size() > 0) ? data.getImage().get(RandomManager.getRandom(data.getImage().size())).getImage() : ""), false,true, true);
        tvFoodItemPrice.setText(getContext().getString(R.string.view_first_bracket_left) + data.getItem_quantity() + " " + getContext().getString(R.string.view_x) + " " + data.getPrice() + getContext().getString(R.string.view_first_bracket_right) + " " + getContext().getString(R.string.view_equal) + " " + getTotalPrice(data) + " " + getContext().getString(R.string.view_tk));

        if (data.getItem_quantity() > 0) {
            svAddToCart.setTextNum(data.getItem_quantity());
        } else if (data.getItem_quantity() == 0 && svAddToCart.getText() > 0) {
            svAddToCart.setTextNum(data.getItem_quantity());
        }
        svAddToCart.setOnShoppingClickListener(new ShoppingView.ShoppingClickListener() {
            @Override
            public void onAddClick(int num) {
                Logger.d(TAG, "@=> " + "add.......num=> " + num);
                data.setItem_quantity(num);
                onOrderNowClick(data);
            }

            @Override
            public void onMinusClick(int num) {
                Logger.d(TAG, "@=> " + "minus.......num=> " + num);
                data.setItem_quantity(num);
                onOrderNowClick(data);
            }
        });

        llRemoveFoodItem.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                RemoveFoodFromCartDialog removeFoodFromCartDialog = new RemoveFoodFromCartDialog(((CheckoutActivity) getContext()), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Delete the food from database
                                if (AppUtil.isFoodItemStored((CheckoutActivity) getContext(), data)) {
                                    AppUtil.deleteSelectedFoodItem((CheckoutActivity) getContext(), data);
                                }

                                //Remove from list
                                CheckoutFoodAdapter checkoutFoodAdapter = ((CheckoutFoodAdapter) getOwnerAdapter());
                                if (checkoutFoodAdapter != null) {
                                    checkoutFoodAdapter.removeItem(data);
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                            case DialogInterface.BUTTON_NEUTRAL:
                                break;
                        }
                    }
                });
                removeFoodFromCartDialog.initView().show();
            }
        });
    }

    private float getTotalPrice(FoodItem foodItem) {
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

    private FoodItem onOrderNowClick(final FoodItem item) {
        Logger.d(TAG, "onOrderNowClick: " + "count: " + item.getItem_quantity());
        CheckoutActivity mActivity = ((CheckoutActivity) getContext());
        tvFoodItemPrice.setText(getContext().getString(R.string.view_first_bracket_left) + item.getItem_quantity() + " " + getContext().getString(R.string.view_x) + " " + item.getPrice() + getContext().getString(R.string.view_first_bracket_right) + " " + getContext().getString(R.string.view_equal) + " " + getTotalPrice(item) + " " + getContext().getString(R.string.view_tk));

        if (item.getItem_quantity() == 0) {
            if (AppUtil.isFoodItemStored(mActivity, item)) {
                //Delete the food from database
                AppUtil.deleteSelectedFoodItem(mActivity, item);
            }
        } else if (item.getItem_quantity() == 1) {
            if (AppUtil.isFoodItemStored(mActivity, item)) {
                //Update data into database
                AppUtil.storeSelectedFoodItem(mActivity, item);
            } else {
                //Add item into database
                AppUtil.storeSelectedFoodItem(mActivity, item);
            }
        } else {
            //Update data into database
            AppUtil.storeSelectedFoodItem(mActivity, item);
        }
        return item;
    }
}