package tech.codegarage.tidetwist.dialog;

import android.app.Activity;

import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.base.BaseAlertDialog;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class DiscardCartDialog extends BaseAlertDialog {

    private OnClickListener mOnClickListener;

    public DiscardCartDialog(Activity activity, OnClickListener onClickListener) {
        super(activity);

        mOnClickListener = onClickListener;
    }

    @Override
    public Builder initView() {
        Builder builder = prepareView("", getActivity().getString(R.string.dialog_your_selected_items_will_be_removed_from_cart), R.string.dialog_cancel, R.string.dialog_ok, -1, mOnClickListener);

        return builder;
    }
}