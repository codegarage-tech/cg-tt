package tech.codegarage.tidetwist.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import tech.codegarage.tidetwist.base.BaseFragment;
import tech.codegarage.tidetwist.fragment.FoodItemFragment;
import tech.codegarage.tidetwist.model.FoodCategory;
import tech.codegarage.tidetwist.util.Logger;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class FoodCategoryViewPagerAdapter extends FragmentPagerAdapter {

    private List<FoodCategory> mFoodCategories = new ArrayList<>();
    private String TAG = FoodCategoryViewPagerAdapter.class.getSimpleName();

    public FoodCategoryViewPagerAdapter(FragmentManager fragmentManager, List<FoodCategory> foodCategories) {
        super(fragmentManager);

        mFoodCategories.clear();
        mFoodCategories.addAll(foodCategories);
    }

    @Override
    public Fragment getItem(int position) {
        return FoodItemFragment.newInstance(mFoodCategories.get(position));
    }

    @Override
    public int getCount() {
        return mFoodCategories.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFoodCategories.get(position).getName();
    }

    public void setFoodCategories(List<FoodCategory> foodCategories) {
        Logger.d(TAG, TAG + " fragmentUpdate>> setFoodCategories in FoodCategoryViewPagerAdapter");
        mFoodCategories = foodCategories;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        Logger.d(TAG, TAG + " fragmentUpdate>> getItemPosition in FoodCategoryViewPagerAdapter");
        if (object instanceof BaseFragment) {
            ((BaseFragment) object).onFragmentUpdate(mFoodCategories);
        }

        //don't return POSITION_NONE, avoid fragment recreation.
        return super.getItemPosition(object);
    }
}