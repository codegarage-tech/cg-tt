package tech.codegarage.tidetwist.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import tech.codegarage.tidetwist.application.TideTwistApp;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class CanaroTextView extends AppCompatTextView {

    public CanaroTextView(Context context) {
        this(context, null);
    }

    public CanaroTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanaroTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(TideTwistApp.canaroExtraBold);
    }
}
