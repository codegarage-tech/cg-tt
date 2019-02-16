package tech.codegarage.dropdownmenuplus.typeview.grid_holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import tech.codegarage.dropdownmenuplus.util.DpUtils;
import tech.codegarage.dropdownmenuplus.view.FilterCheckedTextView;

import tech.codegarage.dropdownmenuplus.R;

/**
 *
 */
public class MultiItemHolder extends RecyclerView.ViewHolder {

    public FilterCheckedTextView textView;

    public MultiItemHolder(Context mContext, ViewGroup parent) {
        super(DpUtils.infalte(mContext, R.layout.holder_item, parent));
        textView = itemView.findViewById(R.id.tv_item);
    }

    /**
     * tag标记的字段规则：eg:"obj_s"
     *
     * @param s
     * @param tag
     */
    public void bind(String s, Object tag) {
        textView.setText(s);
        textView.setTag(tag);
    }
}
