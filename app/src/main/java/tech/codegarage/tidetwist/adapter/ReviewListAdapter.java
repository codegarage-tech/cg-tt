package tech.codegarage.tidetwist.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import tech.codegarage.tidetwist.model.Review;
import tech.codegarage.tidetwist.viewholder.ReviewViewHolder;

import java.security.InvalidParameterException;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class ReviewListAdapter extends RecyclerArrayAdapter<Review> {

    private static final int VIEW_TYPE_REGULAR = 1;

    public ReviewListAdapter(Context context) {
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
                return new ReviewViewHolder(parent);
            default:
                throw new InvalidParameterException();
        }
    }
}