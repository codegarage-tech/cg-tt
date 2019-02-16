package tech.codegarage.tidetwist.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.reversecoder.library.util.AllSettingsManager;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.model.Review;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.Logger;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class ReviewViewHolder extends BaseViewHolder<Review> {

    private static String TAG = ReviewViewHolder.class.getSimpleName();
    private TextView tvReviewerName, tvReviewerAddress, tvReviewerMessage;
    private LinearLayout llAddress;
    private ImageView ivReviewerImage;
    private MaterialRatingBar rbReviewerRating;

    public ReviewViewHolder(ViewGroup parent) {
        super(parent, R.layout.row_review_list_item);

        tvReviewerName = (TextView) $(R.id.tv_reviewer_name);
        tvReviewerAddress = (TextView) $(R.id.tv_reviewer_address);
        tvReviewerMessage = (TextView) $(R.id.tv_reviewer_message);
        ivReviewerImage = (ImageView) $(R.id.iv_reviewer_image);
        rbReviewerRating = (MaterialRatingBar) $(R.id.rb_reviewer_rating);
        llAddress = (LinearLayout) $(R.id.ll_address);
    }

    @Override
    public void setData(final Review data) {

        tvReviewerName.setText(data.getName());
        tvReviewerAddress.setText(data.getAddress());
        tvReviewerMessage.setText(data.getReview_comment());

        if (AllSettingsManager.isNullOrEmpty(data.getAddress())) {
            llAddress.setVisibility(View.GONE);
        } else {
            llAddress.setVisibility(View.VISIBLE);
        }

        float star = 0.0f;
        try {
            star = Float.parseFloat(data.getStar());
        } catch (Exception ex) {
            ex.printStackTrace();
            star = 0.0f;
        }
        Logger.d(TAG, "RatingReview: " + star);
        rbReviewerRating.setRating(star);
        rbReviewerRating.setIsIndicator(true);
        AppUtil.loadImage(getContext(), ivReviewerImage, data.getImage(), false, true, true);
    }
}