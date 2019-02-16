package io.armcha.ribble.presentation.widget

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet

/**
 * Created by Chatikyan on 16.02.2017.
 */

class AnimatedTextView : AppCompatTextView, AnimatedView {

    private val CANARO_EXTRA_BOLD_PATH = "fonts/canaro_extra_bold.otf"

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setAnimatedText(text: CharSequence, startDelay: Long = 0L) {
        changeText(text, startDelay)
        typeface = Typeface.createFromAsset(context.getAssets(), CANARO_EXTRA_BOLD_PATH)
    }

    private fun changeText(newText: CharSequence, startDelay: Long) {
        if (text == newText)
            return
        animate(view = this, startDelay = startDelay) {
            text = newText
        }
    }
}
