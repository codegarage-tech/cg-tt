//package tech.codegarage.tidetwist.adapter;
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import tech.codegarage.tidetwist.R;
//import tech.codegarage.tidetwist.model.Feature;
//import tech.codegarage.tidetwist.util.AppUtil;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author Md. Rashadul Alam
// * Email: rashed.droid@gmail.com
// */
//public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.ViewHolder> {
//
//    private int layoutRes;
//    private int selectedPosition;
//    private OnItemClickListener listener;
//    private List<Feature> items = new ArrayList<>();
//    private List<Integer> dimens = new ArrayList<>();
//
//    private boolean randomHeight;
//    private boolean randomWidth;
//    private Context mContext;
//    private Feature mFeature;
//
//    public FeatureAdapter(Context context) {
//        dimens.add(context.getResources().getDimensionPixelSize(R.dimen.row_detail_size1));
//        dimens.add(context.getResources().getDimensionPixelSize(R.dimen.row_detail_size2));
//        dimens.add(context.getResources().getDimensionPixelSize(R.dimen.row_detail_size3));
//        mContext = context;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View view = inflater.inflate(layoutRes, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.bind(items.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
//
//    public void setListener(OnItemClickListener listener) {
//        this.listener = listener;
//    }
//
//    public void setSelectedPosition(int selectedPosition) {
//        this.selectedPosition = selectedPosition;
//    }
//
//    public int getItemPosition(Feature feature) {
//        if (feature != null && items.size() > 0) {
//            for (int i = 0; i < items.size(); i++) {
//                if (items.get(i).getTitle().equalsIgnoreCase(feature.getTitle())) {
//                    return i;
//                }
//            }
//        }
//        return -1;
//    }
//
//    public int getSelectedPosition() {
//        return selectedPosition;
//    }
//
//    public void setRandomWidth() {
//        randomWidth = true;
//    }
//
//    public void setRandomHeight() {
//        randomHeight = true;
//    }
//
//    public void setVertical(boolean vertical) {
//        if (vertical) {
//            layoutRes = R.layout.row_item_vertical_feature;
//        } else {
//            layoutRes = R.layout.row_item_horizontal_feature;
//        }
//    }
//
//    public void setItems(List<Feature> items, Feature feature) {
//        this.items = items;
//        this.mFeature = feature;
//    }
//
//    public interface OnItemClickListener {
//        void onClickItem(ViewHolder holder, Feature data);
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//
//        public final TextView tvFeatureTitle, tvFeatureSubtitle;
//        public ImageView ivFeature;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//
//            tvFeatureTitle = (TextView) itemView.findViewById(R.id.tv_snappy_title);
//            tvFeatureSubtitle = (TextView) itemView.findViewById(R.id.tv_snappy_subtitle);
//            ivFeature = (ImageView) itemView.findViewById(R.id.iv_snappy);
//        }
//
//        public void bind(final Feature feature) {
//            tvFeatureTitle.setText(feature.getTitle().toUpperCase());
//            tvFeatureSubtitle.setText(feature.getDescription());
//            itemView.setSelected(selectedPosition == getAdapterPosition());
//
//            if (randomWidth) {
//                itemView.setMinimumWidth(getRandomSize());
//            }
//            if (randomHeight) {
//                itemView.setMinimumHeight(getRandomSize());
//            }
//
//            AppUtil.loadImage(mContext, ivFeature, feature.getImage(), false,false, true);
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    view.setSelected(true);
//                    if (listener != null) {
//                        listener.onClickItem(ViewHolder.this, feature);
//                    }
//                }
//            });
//
//        }
//
//        private int getRandomSize() {
//            int index = (int) (Math.random() * 3);
//            return dimens.get(index);
//        }
//    }
//}