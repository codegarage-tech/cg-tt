package tech.codegarage.tidetwist.viewholder;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.reversecoder.library.bang.SmallBang;
import com.reversecoder.library.bang.SmallBangListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.random.RandomManager;

import org.parceler.Parcels;

import me.wangyuwei.shoppoing.ShoppingView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.activity.FoodItemDetailActivity;
import tech.codegarage.tidetwist.activity.KitchenDetailsActivity;
import tech.codegarage.tidetwist.model.FoodItem;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.Logger;

import static tech.codegarage.tidetwist.util.AllConstants.INTENT_KEY_FOOD_ITEM;
import static tech.codegarage.tidetwist.util.AllConstants.INTENT_KEY_IS_KITCHEN_OPEN;
import static tech.codegarage.tidetwist.util.AllConstants.INTENT_REQUEST_CODE_FOOD_ITEM_DETAIL;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class FoodItemViewHolder extends BaseViewHolder<FoodItem> {

    private TextView tvFoodItemName, tvFoodItemPrice, tvFoodItemCount;
    private ImageView ivFoodItem, ivFoodItemFavourite;
    private RelativeLayout rlFavourite;
    private SmallBang mSmallBang;
    private ShoppingView svAddToCart;
    private MaterialRatingBar materialRatingBar;
    private LinearLayout llAddToCart;
    private static String TAG = FoodItemViewHolder.class.getSimpleName();

    public FoodItemViewHolder(ViewGroup parent) {
        super(parent, R.layout.row_food_item);

        tvFoodItemName = (TextView) $(R.id.tv_food_item_name);
        ivFoodItem = (ImageView) $(R.id.iv_food_item);
        ivFoodItemFavourite = (ImageView) $(R.id.iv_food_item_favourite);
        rlFavourite = (RelativeLayout) $(R.id.rl_favourite);
        svAddToCart = (ShoppingView) $(R.id.sv_add_to_cart);
        tvFoodItemPrice = (TextView) $(R.id.iv_food_item_price);
        tvFoodItemCount = (TextView) $(R.id.tv_food_item_review_count);
        llAddToCart = (LinearLayout) $(R.id.ll_add_to_cart);
        materialRatingBar = (MaterialRatingBar) $(R.id.rb_food_item_rating);
    }

    @Override
    public void setData(final FoodItem data) {

        tvFoodItemName.setText(data.getName());
        tvFoodItemPrice.setText(data.getPrice() + " " + getContext().getString(R.string.view_tk));
        tvFoodItemCount.setText((data.getReview_count().equalsIgnoreCase("0")) ? data.getReview_count() : "+" + data.getReview_count());
        AppUtil.loadImage(getContext(), ivFoodItem, ((data.getImage() != null && data.getImage().size() > 0) ? data.getImage().get(RandomManager.getRandom(data.getImage().size())).getImage() : ""), false, false, true);

        //Set rating
        float star = 0.0f;
        try {
            star = Float.parseFloat(data.getAverage_rating());
        } catch (Exception ex) {
            ex.printStackTrace();
            star = 0.0f;
        }
        Logger.d(TAG, "RatingFoodItem: " + star);
        materialRatingBar.setRating(star);
        materialRatingBar.setIsIndicator(true);

        //Set favorite data
        if (data.getIs_favourite() == 1) {
            ivFoodItemFavourite.setImageResource(R.drawable.vector_favourite_fill_white);
        } else {
            ivFoodItemFavourite.setImageResource(R.drawable.vector_favourite_empty_white);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFoodItemDetail = new Intent(getContext(), FoodItemDetailActivity.class);
                intentFoodItemDetail.putExtra(INTENT_KEY_FOOD_ITEM, Parcels.wrap(data));
                intentFoodItemDetail.putExtra(INTENT_KEY_IS_KITCHEN_OPEN, ((KitchenDetailsActivity) getContext()).isKitchenOpen());
                ((KitchenDetailsActivity) getContext()).startActivityForResult(intentFoodItemDetail, INTENT_REQUEST_CODE_FOOD_ITEM_DETAIL);
            }
        });

        rlFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkManager.isConnected(getContext())) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                //send favorite request to the server
                ((KitchenDetailsActivity) getContext()).doFavorite(data, ivFoodItemFavourite);

                //Animate view for favorite
                if (mSmallBang == null) {
                    mSmallBang = SmallBang.attach2Window((KitchenDetailsActivity) getContext());
                }
                mSmallBang.bang(ivFoodItemFavourite, new SmallBangListener() {
                    @Override
                    public void onAnimationStart() {
                    }

                    @Override
                    public void onAnimationEnd() {
                    }
                });
            }
        });

        if (data.getItem_quantity() > 0) {
            svAddToCart.setTextNum(data.getItem_quantity());
        } else if (data.getItem_quantity() == 0 && svAddToCart.getText() > 0) {
            svAddToCart.setTextNum(data.getItem_quantity());
        }
        if (((KitchenDetailsActivity) (getContext())).isKitchenOpen()) {
            svAddToCart.setEnabled(true);
        } else {
            svAddToCart.setEnabled(false);
        }
        svAddToCart.setOnShoppingClickListener(new ShoppingView.ShoppingClickListener() {
            @Override
            public void onAddClick(int num) {
                Logger.d(TAG, "@=> " + "add.......num=> " + num);
                data.setItem_quantity(num);
                ((KitchenDetailsActivity) getContext()).onOrderNowClick(data, svAddToCart);
            }

            @Override
            public void onMinusClick(int num) {
                Logger.d(TAG, "@=> " + "minus.......num=> " + num);
                data.setItem_quantity(num);
                ((KitchenDetailsActivity) getContext()).onOrderNowClick(data, svAddToCart);
            }
        });

        //Stop add to cart when kitchen is closed
        if (((KitchenDetailsActivity) getContext()).isKitchenOpen()) {
            llAddToCart.setVisibility(View.GONE);
        } else {
            llAddToCart.setVisibility(View.VISIBLE);
            llAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), getContext().getString(R.string.toast_kitchen_is_closed_now), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}