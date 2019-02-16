package tech.codegarage.tidetwist.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class LockableViewPager extends ViewPager {

    private boolean isSwipable = true;

    public LockableViewPager(Context context) {
        super(context);
    }

    public LockableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSwipable(boolean canScroll) {
        this.isSwipable = canScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isSwipable && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isSwipable && super.onInterceptTouchEvent(ev);
    }
}