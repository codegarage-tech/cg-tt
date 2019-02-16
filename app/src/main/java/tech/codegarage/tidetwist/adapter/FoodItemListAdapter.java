package tech.codegarage.tidetwist.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import tech.codegarage.tidetwist.activity.KitchenDetailsActivity;
import tech.codegarage.tidetwist.model.FoodItem;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.viewholder.FoodItemViewHolder;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class FoodItemListAdapter extends RecyclerArrayAdapter<FoodItem> {

    private static final int VIEW_TYPE_REGULAR = 1;

    public FoodItemListAdapter(Context context) {
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
                return new FoodItemViewHolder(parent);
            default:
                throw new InvalidParameterException();
        }
    }

    public void setData(List<FoodItem> foodItems) {
        removeAll();
        addAll(foodItems);
        notifyDataSetChanged();
    }

    public void updateItem(FoodItem foodItem) {
        int position = getItemPosition(foodItem);

        List<FoodItem> foodItems = new ArrayList<>();
        foodItems.addAll(getAllData());
        foodItems.remove(position);
        foodItems.add(position, foodItem);

        removeAll();
        addAll(foodItems);
        notifyDataSetChanged();
    }

    private int getItemPosition(FoodItem foodItem) {
        List<FoodItem> foodItems = getAllData();
        for (int i = 0; i < foodItems.size(); i++) {
            if (foodItems.get(i).getProduct_id().equalsIgnoreCase(foodItem.getProduct_id())) {
                return i;
            }
        }
        return -1;
    }

    public void updateSelectedFoodItems() {
        List<FoodItem> dbFoodItems = AppUtil.getAllStoredFoodItems((KitchenDetailsActivity) getContext());
        List<FoodItem> adapterFoodItems = new ArrayList<>(getAllData());

        for (int i = 0; i < adapterFoodItems.size(); i++) {
            FoodItem dbFoodItem = AppUtil.getFoodItem(dbFoodItems, adapterFoodItems.get(i));

            if (dbFoodItem != null) {
                //If adapter data is exist into database, then update
                if (dbFoodItem.getItem_quantity() != adapterFoodItems.get(i).getItem_quantity()) {
                    adapterFoodItems.get(i).setItem_quantity(dbFoodItem.getItem_quantity());
                }
            } else {
                //If adapter data is not exist into database
                if (adapterFoodItems.get(i).getItem_quantity() > 0) {
                    adapterFoodItems.get(i).setItem_quantity(0);
                }
            }
        }

        notifyDataSetChanged();
    }
}