package tech.codegarage.tidetwist.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import tech.codegarage.tidetwist.model.FoodItem;
import tech.codegarage.tidetwist.viewholder.CheckoutFoodViewHolder;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class CheckoutFoodAdapter extends RecyclerArrayAdapter<FoodItem> {

    private static final int VIEW_TYPE_REGULAR = 1;

    public CheckoutFoodAdapter(Context context) {
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
                return new CheckoutFoodViewHolder(parent);
            default:
                throw new InvalidParameterException();
        }
    }

//    public void updateItem(FoodItem foodItem) {
//        int position = getItemPosition(foodItem);
//
//        List<FoodItem> foodItems = new ArrayList<>();
//        foodItems.addAll(getAllData());
//        foodItems.remove(position);
//        foodItems.add(position, foodItem);
//
//        removeAll();
//        addAll(foodItems);
//        notifyDataSetChanged();
//    }

    public int getItemPosition(FoodItem foodItem) {
        List<FoodItem> foodItems = getAllData();
        for (int i = 0; i < foodItems.size(); i++) {
            if (foodItems.get(i).getName().equalsIgnoreCase(foodItem.getName())) {
                return i;
            }
        }
        return -1;
    }

    public void removeItem(FoodItem foodItem) {
        int position = getItemPosition(foodItem);
        if (position != -1) {
            remove(position);
            notifyDataSetChanged();
        }
    }
}