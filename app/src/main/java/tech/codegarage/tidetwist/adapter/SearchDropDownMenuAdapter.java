package tech.codegarage.tidetwist.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import tech.codegarage.dropdownmenuplus.adapter.MenuAdapter;
import tech.codegarage.dropdownmenuplus.adapter.SimpleTextAdapter;
import tech.codegarage.dropdownmenuplus.interfaces.OnFilterDoneListener;
import tech.codegarage.dropdownmenuplus.model.FilterBean;
import tech.codegarage.dropdownmenuplus.model.ItemBean;
import tech.codegarage.dropdownmenuplus.model.TitleBean;
import tech.codegarage.dropdownmenuplus.typeview.DoubleListView;
import tech.codegarage.dropdownmenuplus.typeview.SingleGridView;
import tech.codegarage.dropdownmenuplus.util.CommonUtil;
import tech.codegarage.dropdownmenuplus.util.DpUtils;
import tech.codegarage.dropdownmenuplus.util.FilterUtils;
import tech.codegarage.dropdownmenuplus.view.FilterCheckedTextView;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.util.Logger;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class SearchDropDownMenuAdapter implements MenuAdapter {

    private final Context mContext;
    private OnFilterDoneListener onFilterDoneListener;
    private String[] titles;
    private FilterBean filterBean;
    private List<TitleBean> cityBeanList;
    private List<ItemBean> cuisineListData;

    private OnSingleGirdViewCallbackListener onSingleGirdViewCallbackListener;
    private OnDoubleListViewCallbackListener onDoubleListViewCallbackListener;

    public SearchDropDownMenuAdapter(Context context, String[] titles, OnFilterDoneListener onFilterDoneListener) {
        this.mContext = context;
        this.titles = titles;
        this.onFilterDoneListener = onFilterDoneListener;
    }

    @Override
    public int getMenuCount() {
        return titles.length;
    }

    @Override
    public String getMenuTitle(int position) {
        return titles[position];
    }

    @Override
    public int getBottomMargin(int position) {
        if (position == 3) {
            return 0;
        }
        return DpUtils.dpToPx(mContext, 140);
    }

    @Override
    public View getView(int position, FrameLayout parentContainer) {
        View view = parentContainer.getChildAt(position);

        switch (position) {
            case 0:
                view = createDoubleListView();
                break;
            case 1:
                view = createSingleGridView();
                break;
        }

        return view;
    }

    public void setFilterBean(FilterBean filterBean) {
        this.filterBean = filterBean;
        cityBeanList = filterBean.getTitleBeans();
        cuisineListData = filterBean.getItemBeans();
    }

    private View createSingleGridView() {
        SingleGridView<ItemBean> singleGridView = new SingleGridView<ItemBean>(mContext)
                .adapter(new SimpleTextAdapter<ItemBean>(null, mContext) {
                    @Override
                    public String provideText(ItemBean s) {
                        Logger.d("createSingleGridView: ", "createSingleGridView--item点击" + s.getName());
                        return s.getName();
                    }

                    @Override
                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                        checkedTextView.setPadding(0, DpUtils.dpToPx(context, 10), 0, DpUtils.dpToPx(context, 10));
                        checkedTextView.setGravity(Gravity.CENTER);
                        checkedTextView.setBackgroundResource(R.drawable.filter_btn_reset_style);
                    }
                })
                .onSingleGridViewClick(new SingleGridView.OnSingleGridViewClickListener<ItemBean>() {
                    @Override
                    public void onSingleGridViewCallback(ItemBean item) {
                        onSingleGirdViewCallbackListener.onSingleGridViewCallback(item);

                        FilterUtils.instance().position = 1;
                        FilterUtils.instance().positionTitle = item.getName();

                        onFilterDone(1);
                    }
                });

        singleGridView.setList(cuisineListData, -1);

        return singleGridView;
    }

    private View createDoubleListView() {
        DoubleListView<TitleBean, ItemBean> comTypeDoubleListView = new DoubleListView<TitleBean, ItemBean>(mContext)
                .leftAdapter(new SimpleTextAdapter<TitleBean>(null, mContext) {
                    @Override
                    public String provideText(TitleBean filterType) {
                        return filterType.getName();
                    }

                    @Override
                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                        checkedTextView.setPadding(DpUtils.dpToPx(mContext, 44), DpUtils.dpToPx(mContext, 15), 0, DpUtils.dpToPx(mContext, 15));
                    }
                })
                .rightAdapter(new SimpleTextAdapter<ItemBean>(null, mContext) {
                    @Override
                    public String provideText(ItemBean areaBean) {
                        return areaBean.getName();
                    }

                    @Override
                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                        checkedTextView.setPadding(DpUtils.dpToPx(mContext, 30), DpUtils.dpToPx(mContext, 15), 0, DpUtils.dpToPx(mContext, 15));
                        checkedTextView.setBackgroundResource(android.R.color.white);
                    }
                })
                .onLeftItemClickListener(new DoubleListView.OnLeftItemClickListener<TitleBean, ItemBean>() {
                    @Override
                    public List<ItemBean> provideRightList(TitleBean item, int position) {
                        List<ItemBean> child = item.getSubtitle();
                        if (CommonUtil.isEmpty(child)) {
                            FilterUtils.instance().doubleListLeft = item.getName();
                            FilterUtils.instance().doubleListRight = "";

                            FilterUtils.instance().position = 0;
                            FilterUtils.instance().positionTitle = item.getName();

                            onFilterDone(0);
                        }
                        onDoubleListViewCallbackListener.onDoubleListViewCallback(item, null);

                        return child;
                    }
                })
                .onRightItemClickListener(new DoubleListView.OnRightItemClickListener<TitleBean, ItemBean>() {
                    @Override
                    public void onRightItemClick(TitleBean cityBean, ItemBean areaBean) {
                        FilterUtils.instance().doubleListLeft = cityBean.getName();
                        FilterUtils.instance().doubleListRight = areaBean.getName();

                        FilterUtils.instance().position = 0;
                        FilterUtils.instance().positionTitle = areaBean.getName();

                        onFilterDone(0);

                        onDoubleListViewCallbackListener.onDoubleListViewCallback(cityBean, areaBean);
                    }
                });

        comTypeDoubleListView.setLeftList(cityBeanList.get(0).getSubtitle(), 0);
        comTypeDoubleListView.setRightList(((ArrayList<TitleBean>) cityBeanList.get(0).getSubtitle()).get(0).getSubtitle(), -1);
        comTypeDoubleListView.getLeftListView().setBackgroundColor(ContextCompat.getColor(mContext, R.color.b_c_fafafa));

        return comTypeDoubleListView;
    }

    private void onFilterDone(int tabPosition) {
        if (onFilterDoneListener != null) {
            if (tabPosition == 0) {
                onFilterDoneListener.onFilterDone(0, FilterUtils.instance().positionTitle, "");
            } else if (tabPosition == 1) {
                onFilterDoneListener.onFilterDone(1, FilterUtils.instance().positionTitle, "");
            }
        } else {
            Logger.d("onFilterDone", "DropMenuAdapter--onFilterDoneListener==null");
        }
    }

    public interface OnDoubleListViewCallbackListener {
        void onDoubleListViewCallback(TitleBean titleBean, ItemBean subtitleBean);
    }

    public void setOnDoubleListViewCallbackListener(OnDoubleListViewCallbackListener onDoubleListViewCallbackListener) {
        this.onDoubleListViewCallbackListener = onDoubleListViewCallbackListener;
    }

    public interface OnSingleGirdViewCallbackListener {
        void onSingleGridViewCallback(ItemBean item);
    }

    public void setOnSingleGirdViewCallbackListener(OnSingleGirdViewCallbackListener onSingleGirdViewCallbackListener) {
        this.onSingleGirdViewCallbackListener = onSingleGirdViewCallbackListener;
    }
}