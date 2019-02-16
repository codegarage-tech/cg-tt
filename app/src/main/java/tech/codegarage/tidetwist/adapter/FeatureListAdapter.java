package tech.codegarage.tidetwist.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.security.InvalidParameterException;
import java.util.List;

import tech.codegarage.tidetwist.model.KitchenTime;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.viewholder.FeatureViewHolder;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class FeatureListAdapter extends RecyclerArrayAdapter<KitchenTime> {

    private static final int VIEW_TYPE_REGULAR = 1;

    public FeatureListAdapter(Context context) {
        super(context);
    }

    @Override
    public int getViewType(int position) {
        return VIEW_TYPE_REGULAR;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_REGULAR:
                return new FeatureViewHolder(parent);
            default:
                throw new InvalidParameterException();
        }
    }

    public void setCurrentKitchenTime() {
        //Unselect all kitchen
        List<KitchenTime> data = getAllData();
        if (data != null && data.size() > 0) {
            for (KitchenTime mKitchenTime : data) {
                mKitchenTime.setSelected(false);
            }
        }

        //Select and move to first position
        KitchenTime kitchenTime = AppUtil.getCurrentKitchenTime(getAllData());
        if (kitchenTime != null) {
            kitchenTime.setSelected(true);
            notifyDataSetChanged();

            //Move item at first position
            int position = getItemPosition(kitchenTime);
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getRecyclerView().getLayoutManager();
            if (linearLayoutManager != null && position != -1) {
                linearLayoutManager.scrollToPositionWithOffset(position, 0);
            }
        } else {
            //No item found with matching current time
            notifyDataSetChanged();

            //Move item at first position
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getRecyclerView().getLayoutManager();
            if (linearLayoutManager != null) {
                linearLayoutManager.scrollToPositionWithOffset(5, 0);
            }
        }
    }

    public KitchenTime getCurrentKitchenTime() {
        return AppUtil.getCurrentKitchenTime(getAllData());
    }

    public int getItemPosition(KitchenTime kitchenTime) {
        List<KitchenTime> kitchenTimeList = getAllData();
        for (int i = 0; i < kitchenTimeList.size(); i++) {
            if (kitchenTimeList.get(i).getTime_id().equalsIgnoreCase(kitchenTime.getTime_id())) {
                return i;
            }
        }
        return -1;
    }
}