package tech.codegarage.tidetwist.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.badoualy.stepperindicator.adapter.SmartFragmentStatePagerAdapter;
import tech.codegarage.tidetwist.enumeration.CheckoutScreenType;
import tech.codegarage.tidetwist.fragment.CheckoutDeliveryInfoFragment;
import tech.codegarage.tidetwist.fragment.CheckoutFoodFragment;
import tech.codegarage.tidetwist.fragment.CheckoutPaymentFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class CheckoutPagerAdapter extends SmartFragmentStatePagerAdapter {

    private List<CheckoutScreenType> mFragmentsPages = new ArrayList<CheckoutScreenType>();

    public CheckoutPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        mFragmentsPages.clear();
        mFragmentsPages.addAll(Arrays.asList(CheckoutScreenType.values()));
    }

    @Override
    public Fragment getItem(int position) {
        switch (mFragmentsPages.get(position)) {
            case FOODS:
                return CheckoutFoodFragment.newInstance();
            case DELIVERY_INFO:
                return CheckoutDeliveryInfoFragment.newInstance();
            case PAYMENT:
                return CheckoutPaymentFragment.newInstance();
            default:
                return CheckoutFoodFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return mFragmentsPages.size();
    }

    public CheckoutScreenType getScreenType(int position) {
        return mFragmentsPages.get(position);
    }
}