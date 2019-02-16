package tech.codegarage.tidetwist.viewholder;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import org.parceler.Parcels;

import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.activity.KitchenListActivity;
import tech.codegarage.tidetwist.enumeration.KitchenType;
import tech.codegarage.tidetwist.model.Cuisine;
import tech.codegarage.tidetwist.util.AllConstants;
import tech.codegarage.tidetwist.util.AppUtil;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class CuisineViewHolder extends BaseViewHolder<Cuisine> {

    TextView tvCuisineName;
    LinearLayout llCusine;
    ImageView ivCuisine;

    public CuisineViewHolder(ViewGroup parent) {
        super(parent, R.layout.row_cuisine_item);

        tvCuisineName = (TextView) $(R.id.tv_cuisine_name);
        llCusine = (LinearLayout) $(R.id.ll_cuisine);
        ivCuisine = (ImageView) $(R.id.iv_cuisine);
    }

    @Override
    public void setData(final Cuisine data) {

        tvCuisineName.setText(data.getName());
        AppUtil.loadImage(getContext(), ivCuisine, data.getImage(), false, true, true);

        llCusine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intentSearch = new Intent(getContext(), KitchenListActivity.class);
                intentSearch.putExtra(AllConstants.INTENT_KEY_KITCHEN_TYPE, KitchenType.CUISINE.name());
                intentSearch.putExtra(AllConstants.INTENT_KEY_CUISINE, Parcels.wrap(data));
                getContext().startActivity(intentSearch);
            }
        });
    }
}