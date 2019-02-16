package com.nex3z.flowlayout;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FlowLayoutManager {

    public enum TEXT_TYPE {SELECTED, UNSELECTED}

    public interface onFlowViewClick {
        public void flowViewClick(TextView updatedTextView);
    }

    private Context context;
    private FlowLayout flowLayout;
    private List<String> keys;
    private List<TextView> flowViews = new ArrayList<>();
    private onFlowViewClick onFlowViewClick;
    private boolean isSingleChoice = false;

    private FlowLayoutManager(Context context, FlowLayout flowLayout, List<String> keys, onFlowViewClick onFlowViewClick, boolean isSingleChoice) {
        this.context = context;
        this.flowLayout = flowLayout;
        this.keys = keys;
        this.onFlowViewClick = onFlowViewClick;
        this.isSingleChoice = isSingleChoice;
    }

    // Helper methods
    private TextView buildLabel(String text) {

        final TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setPadding((int) dpToPx(16), (int) dpToPx(8), (int) dpToPx(16), (int) dpToPx(8));
        textView.setBackgroundResource(R.drawable.chip_unselected);
        textView.setTag(TEXT_TYPE.UNSELECTED.name());
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickFlowView(textView);
            }
        });

        return textView;
    }

    private void clickFlowView(TextView textView) {
        TextView updatedTextView;
        if (textView.getTag().toString().equalsIgnoreCase(TEXT_TYPE.SELECTED.name())) {
            updatedTextView = unSelectFlowView(textView);
        } else {
            updatedTextView = selectFlowView(textView);
        }

        //For single choice we need to clear all selection before update
        if (isSingleChoice) {
            clearAllSelectionExceptCurrent(updatedTextView.getText().toString());
        }

        //Update list for both single choice and multi choice
        if (onFlowViewClick != null) {
            onFlowViewClick.flowViewClick(updateTextView(updatedTextView));
        }
    }

    public void clickFlowView(String itemName) {
        TextView textView = getFlowView(itemName);
        TextView updatedTextView;
        if (textView.getTag().toString().equalsIgnoreCase(TEXT_TYPE.SELECTED.name())) {
            updatedTextView = unSelectFlowView(textView);
        } else {
            updatedTextView = selectFlowView(textView);
        }

        //For single choice we need to clear all selection before update
        if (isSingleChoice) {
            clearAllSelectionExceptCurrent(updatedTextView.getText().toString());
        }

        //Update list for both single choice and multi choice
        if (onFlowViewClick != null) {
            onFlowViewClick.flowViewClick(updateTextView(updatedTextView));
        }
    }

    private FlowLayoutManager buildFlowView() {
        List<TextView> mFlowContents = new ArrayList<TextView>();
        for (String text : keys) {
            TextView textView = buildLabel(text);
            flowLayout.addView(textView);
            mFlowContents.add(textView);
        }
        flowViews = mFlowContents;
        return this;
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private TextView selectFlowView(TextView textView) {
        textView.setTag(TEXT_TYPE.SELECTED.name());
        textView.setBackgroundResource(R.drawable.chip_selected);
        textView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        return textView;
    }

    private TextView unSelectFlowView(TextView textView) {
        textView.setTag(TEXT_TYPE.UNSELECTED.name());
        textView.setBackgroundResource(R.drawable.chip_unselected);
        textView.setTextColor(ContextCompat.getColor(context, R.color.filters_header));
        return textView;
    }

    private TextView updateTextView(TextView textView) {
        TextView updatedTextView = null;
        if (flowViews != null && flowViews.size() > 0) {
            int position = getFlowViewPosition(textView.getText().toString());
            flowViews.remove(position);
            flowViews.add(position, textView);
            updatedTextView = flowViews.get(position);
        }
        return updatedTextView;
    }

    private int getFlowViewPosition(String value) {
        if (flowViews != null && flowViews.size() > 0) {
            for (int i = 0; i < flowViews.size(); i++) {
                if (flowViews.get(i).getText().toString().equalsIgnoreCase(value)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private TextView getFlowView(String value) {
        if (flowViews != null && flowViews.size() > 0) {
            for (int i = 0; i < flowViews.size(); i++) {
                if (flowViews.get(i).getText().toString().equalsIgnoreCase(value)) {
                    return flowViews.get(i);
                }
            }
        }
        return null;
    }

    public List<TextView> getAllFlowViews() {
        return flowViews;
    }

    public void removeAllKeys() {
        keys.clear();
        flowViews.clear();
        onFlowViewClick = null;
        isSingleChoice = false;
        flowLayout.removeAllViews();
    }

    public List<TextView> getSelectedFlowViews() {
        final List<TextView> data = new ArrayList<>();
        if (flowViews != null && flowViews.size() > 0) {
            for (TextView textView : flowViews) {
                if (textView.getTag().toString().equalsIgnoreCase(TEXT_TYPE.SELECTED.name())) {
                    data.add(textView);
                }
            }
        }
        return data;
    }

    private List<TextView> clearAllSelectionExceptCurrent(String value) {
        final List<TextView> data = new ArrayList<>();
        if (flowViews != null && flowViews.size() > 0) {
            for (int i = 0; i < flowViews.size(); i++) {
                TextView updatedTextView;
                if (!flowViews.get(i).getText().toString().equalsIgnoreCase(value)) {
                    updatedTextView = unSelectFlowView(flowViews.get(i));
                } else {
                    updatedTextView = flowViews.get(i);
                }
                data.add(updatedTextView);
            }
        }
        return data;
    }

    public static class FlowViewBuilder {

        private Context mContext;
        private FlowLayout mFlowLayout;
        private List<String> mKeys;
        private boolean mIsSingleChoice = false;
        private onFlowViewClick mOnFlowViewClick;

        public FlowViewBuilder(Context context, FlowLayout flowLayout, List<String> keys, onFlowViewClick onFlowViewClick) {
            mContext = context;
            mFlowLayout = flowLayout;
            mKeys = keys;
            mOnFlowViewClick = onFlowViewClick;
        }

        public FlowViewBuilder setSingleChoice(boolean isSingleChoice) {
            mIsSingleChoice = isSingleChoice;
            return this;
        }

        public FlowLayoutManager build() {
            FlowLayoutManager flowLayoutManager = new FlowLayoutManager(mContext, mFlowLayout, mKeys, mOnFlowViewClick, mIsSingleChoice).buildFlowView();
            return flowLayoutManager;
        }
    }
}