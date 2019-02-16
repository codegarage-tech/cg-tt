package tech.codegarage.tidetwist.animation.flytocart;

/**
 * Created by coderzlab on 18/8/16.
 */

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import tech.codegarage.tidetwist.R;

import java.lang.ref.WeakReference;

/**
 * Created by DK on 2015/3/19.
 */
public class CircleAnimationUtil {
    private static final int DEFAULT_DURATION = 1000;
    private static final int DEFAULT_DURATION_DISAPPEAR = 200;
    private View mTarget, mTargetCopy;
    private View mDest;

    private float originX;
    private float originY;
    private float destX;
    private float destY;

    private int mCircleDuration = DEFAULT_DURATION;
    private int mMoveDuration = DEFAULT_DURATION;
    private int mDisappearDuration = DEFAULT_DURATION_DISAPPEAR;

    private WeakReference<Activity> mContextReference;
    private int mBorderWidth = 4;
    private int mBorderColor = Color.RED;
    //    private CircleLayout mCircleLayout;
    private Bitmap mBitmap;
    private CircleImageView mImageView;
    private Animator.AnimatorListener mAnimationListener;

    public CircleAnimationUtil() {
    }

    public CircleAnimationUtil attachActivity(Activity activity) {
        mContextReference = new WeakReference<Activity>(activity);
        return this;
    }

    public CircleAnimationUtil setTargetView(View view) {
        mTarget = view;
        setOriginRect(mTarget.getWidth(), mTarget.getHeight());
        return this;
    }

    public CircleAnimationUtil setTargetViewCopy(View view) {
        mTargetCopy = view;
        return this;
    }

    private CircleAnimationUtil setOriginRect(float x, float y) {
        originX = x;
        originY = y;
        return this;
    }

    private CircleAnimationUtil setDestRect(float x, float y) {
        destX = x;
        destY = y;
        return this;
    }

    public CircleAnimationUtil setDestView(View view) {
        mDest = view;
        setDestRect(mDest.getWidth(), mDest.getWidth());
        return this;
    }

    public CircleAnimationUtil setBorderWidth(int width) {
        mBorderWidth = width;
        return this;
    }

    public CircleAnimationUtil setBorderColor(int color) {
        mBorderColor = color;
        return this;
    }

    public CircleAnimationUtil setCircleDuration(int duration) {
        mCircleDuration = duration;
        return this;
    }

    public CircleAnimationUtil setMoveDuration(int duration) {
        mMoveDuration = duration;
        return this;
    }

    private boolean prepare() {
        if (mContextReference.get() != null) {
            ViewGroup decoreView = (ViewGroup) mContextReference.get().getWindow().getDecorView();

            mBitmap = drawViewToBitmap(mTarget, mTarget.getWidth(), mTarget.getHeight());
            if (mImageView == null)
                mImageView = new CircleImageView(mContextReference.get());
            mImageView.setImageBitmap(mBitmap);
            mImageView.setBorderWidth(mBorderWidth);
            mImageView.setBorderColor(mBorderColor);

            int[] src = new int[2];
            mTarget.getLocationOnScreen(src);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mTarget.getWidth(), mTarget.getHeight());
            params.setMargins(src[0], src[1], 0, 0);
            if (mImageView.getParent() == null)
                decoreView.addView(mImageView, params);
        }
        return true;
    }

    public void startAnimation() {

        if (prepare()) {
//            mTarget.setVisibility(View.INVISIBLE);
            getAvatarRevealAnimator().start();
        }
    }

    private AnimatorSet getAvatarRevealAnimator() {
        final float endRadius = Math.max(destX, destY) / 2;
        final float startRadius = Math.max(originX, originY);

        Animator mRevealAnimator = ObjectAnimator.ofFloat(mImageView, "drawableRadius", startRadius, endRadius * 1.05f, endRadius * 0.9f, endRadius);
        mRevealAnimator.setInterpolator(new AccelerateInterpolator());

//        float scaleFactor = Math.max(2f * destY / originY, 2f * destX / originX);
        final float scaleFactor = 0.5f;
        Animator scaleAnimatorY = ObjectAnimator.ofFloat(mImageView, View.SCALE_Y, 1, 1, scaleFactor, scaleFactor);
        Animator scaleAnimatorX = ObjectAnimator.ofFloat(mImageView, View.SCALE_X, 1, 1, scaleFactor, scaleFactor);

        AnimatorSet animatorCircleSet = new AnimatorSet();
        animatorCircleSet.setDuration(mCircleDuration);
        animatorCircleSet.playTogether(scaleAnimatorX, scaleAnimatorY, mRevealAnimator);
        animatorCircleSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mAnimationListener != null) {
                    mAnimationListener.onAnimationStart(animation);
                }
                if (mTargetCopy != null) {
                    mTargetCopy.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                int[] src = new int[2];
                int[] dest = new int[2];
                mImageView.getLocationOnScreen(src);
                mDest.getLocationOnScreen(dest);

                float y = mImageView.getY();
                float x = mImageView.getX();
                Animator translatorX = ObjectAnimator.ofFloat(mImageView, View.X, x, x + dest[0] - (src[0] + (originX * scaleFactor - 2 * endRadius * scaleFactor) / 2) + (0.5f * destX - scaleFactor * endRadius));
                translatorX.setInterpolator(new TimeInterpolator() {
                    @Override
                    public float getInterpolation(float input) {
//                        return (float) (Math.sin((0.5f * input) * Math.PI));
                        //-(1-x)^2+1
                        return (float) (-Math.pow(input - 1, 2) + 1f);
                    }
                });
                Animator translatorY = ObjectAnimator.ofFloat(mImageView, View.Y, y, y + dest[1] - (src[1] + (originY * scaleFactor - 2 * endRadius * scaleFactor) / 2) + (0.5f * destY - scaleFactor * endRadius));
                translatorY.setInterpolator(new LinearInterpolator());

                AnimatorSet animatorMoveSet = new AnimatorSet();
                animatorMoveSet.playTogether(translatorX, translatorY);
                animatorMoveSet.setDuration(mMoveDuration);

                AnimatorSet animatorDisappearSet = new AnimatorSet();
                Animator disappearAnimatorY = ObjectAnimator.ofFloat(mImageView, View.SCALE_Y, scaleFactor, 0);
                Animator disappearAnimatorX = ObjectAnimator.ofFloat(mImageView, View.SCALE_X, scaleFactor, 0);
                animatorDisappearSet.setDuration(mDisappearDuration);
                animatorDisappearSet.playTogether(disappearAnimatorX, disappearAnimatorY);


                AnimatorSet total = new AnimatorSet();
                total.playSequentially(animatorMoveSet, animatorDisappearSet);
                total.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (mAnimationListener != null) {
                            mAnimationListener.onAnimationEnd(animation);
                        }
                        reset();
                        if (mTargetCopy != null) {
                            mTargetCopy.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                total.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        return animatorCircleSet;
    }

    private Bitmap drawViewToBitmap(View view, int width, int height) {
        Drawable drawable = new BitmapDrawable();
//        view.layout(0, 0, width, height);
        Bitmap dest = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(dest);
        drawable.setBounds(new Rect(0, 0, width, height));
        drawable.draw(c);
        view.draw(c);
//        view.layout(0, 0, width, height);
        return dest;
    }

    private void reset() {
        mBitmap.recycle();
        mBitmap = null;
        if (mImageView.getParent() != null)
            ((ViewGroup) mImageView.getParent()).removeView(mImageView);
        mImageView = null;
        mTarget.setVisibility(View.VISIBLE);
    }

    public CircleAnimationUtil setAnimationListener(Animator.AnimatorListener listener) {
        mAnimationListener = listener;
        return this;
    }

    public static ViewGroup createAnimLayout(Activity activity) {
        ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setId(Integer.MAX_VALUE - 1);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }

    public static View addViewToAnimLayout(Context mContext, final View view, int[] location, boolean wrap_content) {
        if (view == null) return null;
        int x = location[0];
        int y = location[1];
        LinearLayout.LayoutParams params;
        if (wrap_content) {
            Drawable drawable = null;
            if (view instanceof ImageView) {
                drawable = ((ImageView) view).getDrawable();
            }
            if (drawable == null) {
                //final int wh = mContext.getResources().getDimensionPixelSize(R.dimen.db_goods_wh);
                params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            } else {
                params = new LinearLayout.LayoutParams(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            }
        } else {
            final int wh = mContext.getResources().getDimensionPixelSize(R.dimen.db_goods_wh);
            params = new LinearLayout.LayoutParams(wh, wh);
        }
        params.leftMargin = x;
        params.topMargin = y;
        view.setLayoutParams(params);
        return view;
    }
}