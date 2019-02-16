package tech.codegarage.tidetwist.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.jude.easyrecyclerview.EasyRecyclerView;

import org.parceler.Parcels;

import java.util.List;

import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.adapter.FoodItemListAdapter;
import tech.codegarage.tidetwist.base.BaseFragment;
import tech.codegarage.tidetwist.model.FoodCategory;
import tech.codegarage.tidetwist.model.FoodItem;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.Logger;

import static tech.codegarage.tidetwist.util.AllConstants.INTENT_KEY_FOOD_CATEGORY;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class FoodItemFragment extends BaseFragment {

    private EasyRecyclerView rvFoodItem;
    private FoodItemListAdapter mFoodItemListAdapter;
    private FoodCategory mFoodCategory;

    public static FoodItemFragment newInstance(FoodCategory foodCategory) {
        Bundle args = new Bundle();
        args.putParcelable(INTENT_KEY_FOOD_CATEGORY, Parcels.wrap(foodCategory));
        FoodItemFragment fragment = new FoodItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int initFragmentLayout() {
        return R.layout.fragment_food_item;
    }

    @Override
    public void initFragmentBundleData(Bundle bundle) {
        if (bundle != null) {
            mFoodCategory = Parcels.unwrap(bundle.getParcelable(INTENT_KEY_FOOD_CATEGORY));
            Logger.d(TAG, TAG + " >>> " + "mFoodCategory: " + mFoodCategory.toString());
        }
    }

    @Override
    public void initFragmentViews(View parentView) {
        rvFoodItem = (EasyRecyclerView) parentView.findViewById(R.id.rv_food_item);
    }

    @Override
    public void initFragmentViewsData() {
        mFoodItemListAdapter = new FoodItemListAdapter(getActivity());
        rvFoodItem.setLayoutManager(new GridLayoutManager(getActivity(), AppUtil.getGridSpanCount(getActivity())));
        rvFoodItem.setAdapter(mFoodItemListAdapter);
        mFoodItemListAdapter.addAll(mFoodCategory.getFood_items());
    }

    @Override
    public void initFragmentActions() {

    }

    @Override
    public void initFragmentBackPress() {

    }

    @Override
    public void initFragmentOnResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void initFragmentUpdate(Object object) {
        Logger.d(TAG, TAG + " fragmentUpdate>> initFragmentUpdate in FoodItemFragment");
        List<FoodCategory> foodCategories = (List<FoodCategory>) object;
        if (foodCategories != null) {
            Logger.d(TAG, TAG + " fragmentUpdate>> update: " + foodCategories.toString());
            for (FoodCategory foodCategory : foodCategories) {
                if (foodCategory.getCategory_id().equalsIgnoreCase(mFoodCategory.getCategory_id())) {
                    Logger.d(TAG, TAG + " fragmentUpdate>> desired category: " + foodCategory.getName());
                    mFoodItemListAdapter.removeAll();
                    mFoodCategory = foodCategory;
                    mFoodItemListAdapter.addAll(mFoodCategory.getFood_items());
                    mFoodItemListAdapter.notifyDataSetChanged();
                    Logger.d(TAG, TAG + " fragmentUpdate>> data updated successfully");
                }
            }
        }
    }

    public void updateSpecificFood(FoodItem foodItem) {
        mFoodItemListAdapter.updateItem(foodItem);
    }

    public void updateSelectedFoodItems() {
        mFoodItemListAdapter.updateSelectedFoodItems();
    }
}