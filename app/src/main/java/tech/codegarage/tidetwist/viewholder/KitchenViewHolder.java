package tech.codegarage.tidetwist.viewholder;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.reversecoder.library.random.RandomManager;

import org.parceler.Parcels;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.activity.KitchenDetailsActivity;
import tech.codegarage.tidetwist.activity.KitchenListActivity;
import tech.codegarage.tidetwist.model.Kitchen;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.Logger;

import static tech.codegarage.tidetwist.util.AllConstants.INTENT_KEY_KITCHEN;
import static tech.codegarage.tidetwist.util.AllConstants.INTENT_REQUEST_CODE_KITCHEN_DETAIL;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class KitchenViewHolder extends BaseViewHolder<Kitchen> {

    private String TAG = "KitchenViewHolder";
    private TextView tvKitchenName, tvCuisineName, tvKitchenTime;
    private ImageView ivKitchen;
    private MaterialRatingBar rbKitchensRating;
    private RelativeLayout rlClosed;

    public KitchenViewHolder(ViewGroup parent) {
        super(parent, R.layout.row_kitchen_list_item);

        tvKitchenName = (TextView) $(R.id.tv_kitchen_name);
        tvCuisineName = (TextView) $(R.id.tv_kitchen_cuisine);
        tvKitchenTime = (TextView) $(R.id.tv_kitchen_time);
        ivKitchen = (ImageView) $(R.id.iv_kitchen);
        rbKitchensRating = (MaterialRatingBar) $(R.id.rb_kitchens_rating);
        rlClosed = (RelativeLayout) $(R.id.rl_closed);
    }

    @Override
    public void setData(final Kitchen data) {
        //Check if kitchen is closed or open
        if (data.getIs_opened().equalsIgnoreCase("0")) {
            rlClosed.setVisibility(View.VISIBLE);
        } else {
            rlClosed.setVisibility(View.GONE);
        }

        tvKitchenName.setText(data.getName());
        tvCuisineName.setText(data.getCuisine());
        tvKitchenTime.setText(data.getOpening_schedule());
        AppUtil.loadImage(getContext(), ivKitchen, ((data.getImage() != null && data.getImage().size() > 0) ? data.getImage().get(RandomManager.getRandom(data.getImage().size())).getImage() : ""), false, false, true);

        //Set rating
        float star = 0.0f;
        try {
            star = Float.parseFloat(data.getAverage_rating());
        } catch (Exception ex) {
            ex.printStackTrace();
            star = 0.0f;
        }
        Logger.d(TAG, "RatingKitchen: " + star);
        rbKitchensRating.setRating(star);
        rbKitchensRating.setIsIndicator(true);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentKitchenDetail = new Intent(getContext(), KitchenDetailsActivity.class);
                intentKitchenDetail.putExtra(INTENT_KEY_KITCHEN, Parcels.wrap(data));
                ((KitchenListActivity) getContext()).startActivityForResult(intentKitchenDetail, INTENT_REQUEST_CODE_KITCHEN_DETAIL);
            }
        });
    }
}
