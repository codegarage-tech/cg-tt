package tech.codegarage.tidetwist.viewholder;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.reversecoder.library.event.OnSingleClickListener;

import org.parceler.Parcels;

import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.activity.KitchenListActivity;
import tech.codegarage.tidetwist.enumeration.KitchenType;
import tech.codegarage.tidetwist.model.KitchenTime;
import tech.codegarage.tidetwist.util.AllConstants;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.Logger;

import static tech.codegarage.tidetwist.util.AllConstants.FLASHING_DEFAULT_DELAY;
import static tech.codegarage.tidetwist.util.AllConstants.INTENT_KEY_KITCHEN_TYPE;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class FeatureViewHolder extends BaseViewHolder<KitchenTime> {

    private static String TAG = FeatureViewHolder.class.getSimpleName();
    private TextView titleView;
    private ImageView ivKitchenTime;

    public FeatureViewHolder(ViewGroup parent) {
        super(parent, R.layout.row_feature);

        titleView = (TextView) $(R.id.tv_snappy_title);
        ivKitchenTime = (ImageView) $(R.id.iv_snappy);
    }

    @Override
    public void setData(final KitchenTime kitchenTime) {
        //Set data
        titleView.setText(kitchenTime.getPrepare_time().toUpperCase());
        AppUtil.loadImage(getContext(), ivKitchenTime, kitchenTime.getImage(), false, true, true);

        ValueAnimator valueAnimator;
        if (kitchenTime.isSelected()) {
            //Check if previous animation started
            Object animator = itemView.getTag();
            if (animator != null) {
                valueAnimator = (ValueAnimator) animator;
                if (valueAnimator.isStarted()) {
                    valueAnimator.end();
                }
                valueAnimator.start();
            } else {
                valueAnimator = ValueAnimator.ofFloat(0f, 1f);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        itemView.findViewById(R.id.content_view).setAlpha((Float) animation.getAnimatedValue());
                    }
                });
                valueAnimator.setDuration(FLASHING_DEFAULT_DELAY);
                valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
                valueAnimator.setRepeatCount(-1);
                valueAnimator.start();
            }
            itemView.setTag(valueAnimator);
        } else {
            //Check if previous animation started
            Object animator = itemView.getTag();
            if (animator != null) {
                valueAnimator = (ValueAnimator) animator;
                if (valueAnimator.isStarted()) {
                    valueAnimator.end();
                }
                itemView.setTag(valueAnimator);
            }
        }

        //Set click listener
        itemView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Logger.d(TAG, "kitchenTime: " + kitchenTime.toString());

                if (kitchenTime.getPrepare_time().equalsIgnoreCase(getContext().getString(R.string.view_fast_delivery))) {
                    Intent intentFastDelivery = new Intent(getContext(), KitchenListActivity.class);
                    intentFastDelivery.putExtra(INTENT_KEY_KITCHEN_TYPE, KitchenType.FAST_DELIVERY.name());
                    getContext().startActivity(intentFastDelivery);
                } else if (kitchenTime.getPrepare_time().equalsIgnoreCase(getContext().getString(R.string.view_popular))) {
                    Intent intentSearch = new Intent(getContext(), KitchenListActivity.class);
                    intentSearch.putExtra(INTENT_KEY_KITCHEN_TYPE, KitchenType.POPULAR.name());
                    getContext().startActivity(intentSearch);
                } else if (kitchenTime.getPrepare_time().equalsIgnoreCase(getContext().getString(R.string.view_offer))) {
                    Intent intentSearch = new Intent(getContext(), KitchenListActivity.class);
                    intentSearch.putExtra(INTENT_KEY_KITCHEN_TYPE, KitchenType.OFFER.name());
                    getContext().startActivity(intentSearch);
                } else {
                    Intent intentKitchenTime = new Intent(getContext(), KitchenListActivity.class);
                    intentKitchenTime.putExtra(AllConstants.INTENT_KEY_KITCHEN_TYPE, KitchenType.TIME.name());
                    intentKitchenTime.putExtra(AllConstants.INTENT_KEY_TIME, Parcels.wrap(kitchenTime));
                    getContext().startActivity(intentKitchenTime);
                }
            }
        });
    }
}