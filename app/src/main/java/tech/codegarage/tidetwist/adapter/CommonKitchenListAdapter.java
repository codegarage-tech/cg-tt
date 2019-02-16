package tech.codegarage.tidetwist.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import tech.codegarage.tidetwist.model.Kitchen;
import tech.codegarage.tidetwist.viewholder.KitchenViewHolder;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class CommonKitchenListAdapter extends RecyclerArrayAdapter<Kitchen> {

    private static final int VIEW_TYPE_REGULAR = 1;

    public CommonKitchenListAdapter(Context context) {
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
                return new KitchenViewHolder(parent);
            default:
                throw new InvalidParameterException();
        }
    }

    public void updateItem(Kitchen kitchen) {
        int position = getItemPosition(kitchen);

        List<Kitchen> kitchens = new ArrayList<>();
        kitchens.addAll(getAllData());
        kitchens.remove(position);
        kitchens.add(position, kitchen);

        removeAll();
        addAll(kitchens);
        notifyDataSetChanged();
    }

    private int getItemPosition(Kitchen kitchen) {
        List<Kitchen> kitchens = getAllData();
        for (int i = 0; i < kitchens.size(); i++) {
            if (kitchens.get(i).getManufacturer_id().equalsIgnoreCase(kitchen.getManufacturer_id())) {
                return i;
            }
        }
        return -1;
    }
}