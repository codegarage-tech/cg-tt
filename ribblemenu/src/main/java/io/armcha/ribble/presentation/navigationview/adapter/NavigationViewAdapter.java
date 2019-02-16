package io.armcha.ribble.presentation.navigationview.adapter;

import android.content.Context;
import android.view.ViewGroup;

import java.security.InvalidParameterException;
import java.util.List;

import io.armcha.ribble.presentation.navigationview.NavigationId;
import io.armcha.ribble.presentation.navigationview.NavigationItem;
import io.armcha.ribble.presentation.navigationview.viewholder.BaseViewHolder;
import io.armcha.ribble.presentation.navigationview.viewholder.NavigationViewHolder;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class NavigationViewAdapter extends RecyclerArrayAdapter<NavigationItem> {

    private static final int VIEW_TYPE_REGULAR = 1;

    public NavigationViewAdapter(Context context) {
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
                return new NavigationViewHolder(parent);
            default:
                throw new InvalidParameterException();
        }
    }

    public NavigationItem selectNavigationItem(NavigationId navigationId) {
        NavigationItem mNavigationItem = null;
        List<NavigationItem> navigationItems = getAllData();
        for (NavigationItem navigationItem : navigationItems) {
            if (navigationItem.getNavigationId() == navigationId) {
                navigationItem.setSelected(true);
                mNavigationItem = navigationItem;
            } else {
                navigationItem.setSelected(false);
            }
        }
        notifyDataSetChanged();
        return mNavigationItem;
    }
}