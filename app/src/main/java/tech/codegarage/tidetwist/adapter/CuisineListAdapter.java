package tech.codegarage.tidetwist.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import tech.codegarage.tidetwist.model.Cuisine;
import tech.codegarage.tidetwist.viewholder.CuisineViewHolder;

import java.security.InvalidParameterException;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class CuisineListAdapter extends RecyclerArrayAdapter<Cuisine> {

    private static final int VIEW_TYPE_REGULAR = 1;

    public CuisineListAdapter(Context context) {
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
                return new CuisineViewHolder(parent);
            default:
                throw new InvalidParameterException();
        }
    }
}