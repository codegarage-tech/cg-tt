package com.reversecoder.timelineview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class TimeLineView extends LinearLayout {

    public static final int POSITION_TYPE_FIRST = 1;
    public static final int POSITION_TYPE_MIDDLE = 2;
    public static final int POSITION_TYPE_LAST = 3;
    public static final int POSITION_TYPE_PLAIN = 4;

    public static final int ZONE_TYPE_LINE = 1;
    public static final int ZONE_TYPE_ITEM = 2;
    public static final String ZONE_TYPE = "zoneType";
    public static final float NONE_VALUE_F = -1f;
    public static final int NONE_VALUE_I = -1;

    int ZoneType = ZONE_TYPE_ITEM;
    float radioSmall = -1;
    float radioBig = -1;
    float radioLeft = -1;
    int radioBackgroundColor = Color.BLACK;
    int radioforColor = Color.WHITE;
    int positionType = POSITION_TYPE_MIDDLE;
    Paint paint;

    public TimeLineView(Context context) {
        super(context);
        init();
    }

    public TimeLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.TimeLineView);

        ZoneType = a.getColor(R.styleable.TimeLineView_zoneType, ZONE_TYPE_ITEM);
        radioSmall = a.getDimension(R.styleable.TimeLineView_radioSmall, NONE_VALUE_F);
        radioBig = a.getDimension(R.styleable.TimeLineView_radioBig, NONE_VALUE_F);
        radioLeft = a.getDimension(R.styleable.TimeLineView_radioLeft, NONE_VALUE_F);
        positionType = a.getInt(R.styleable.TimeLineView_positionType, POSITION_TYPE_MIDDLE);
        a.recycle();
        init();
    }

    private void init() {
        if (null == paint) {
            paint = new Paint();
        }
        if (NONE_VALUE_F == radioSmall) {
            radioSmall = 12;
        }
        if (NONE_VALUE_F == radioBig) {
            radioBig = 20;
        }
        if (NONE_VALUE_I == positionType) {
            positionType = POSITION_TYPE_MIDDLE;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        paint.setAntiAlias(true);
        if (radioLeft == NONE_VALUE_F) {
            radioLeft = getWidth() / 8;
        }
        float x = radioLeft;
        float y = 0;

        if (ZoneType == ZONE_TYPE_ITEM) {

            switch (positionType) {
                case POSITION_TYPE_LAST:
                    paint.setColor(radioBackgroundColor);
                    canvas.drawCircle(x, y, radioBig, paint);
                    paint.setColor(radioforColor);
                    canvas.drawCircle(x, y, radioSmall, paint);
                    break;
                case POSITION_TYPE_FIRST:
                    y = getHeight();
                    paint.setColor(radioBackgroundColor);
                    canvas.drawCircle(x, y, radioBig, paint);
                    paint.setColor(radioforColor);
                    break;
                case POSITION_TYPE_MIDDLE:
                    paint.setColor(radioBackgroundColor);
                    canvas.drawCircle(x, y, radioBig, paint);
                    paint.setColor(radioforColor);
                    canvas.drawCircle(x, y, radioSmall, paint);

                    y = getHeight();
                    paint.setColor(radioBackgroundColor);
                    canvas.drawCircle(x, y, radioBig, paint);
                    paint.setColor(radioforColor);
                    break;
                case POSITION_TYPE_PLAIN:
                    y = getHeight();
                    paint.setColor(radioBackgroundColor);
                    canvas.drawCircle(x, y, 0, paint);
                    paint.setColor(radioforColor);
                    break;
            }

            canvas.drawCircle(x, y, radioSmall, paint);
        } else if (ZoneType == ZONE_TYPE_LINE) {

            switch (positionType) {
                case POSITION_TYPE_FIRST:
                    paint.setColor(radioforColor);
                    canvas.drawCircle(x, radioSmall, radioSmall, paint);
                    y = getHeight();
                    paint.setColor(radioforColor);
                    canvas.drawCircle(x, y, radioSmall, paint);
                    canvas.drawRect(x - radioSmall / 2, radioSmall, x + radioSmall / 2, y, paint);
                    break;
                case POSITION_TYPE_LAST:
                    paint.setColor(radioforColor);
                    canvas.drawCircle(x, 0, radioSmall, paint);
                    y = getHeight();
                    paint.setColor(radioforColor);
                    canvas.drawCircle(x, y - radioSmall, radioSmall, paint);
                    canvas.drawRect(x - radioSmall / 2, 0, x + radioSmall / 2, y - radioSmall, paint);
                    break;
                case POSITION_TYPE_MIDDLE:
                    paint.setColor(radioforColor);
                    canvas.drawCircle(x, 0, radioSmall, paint);
                    y = getHeight();
                    paint.setColor(radioforColor);
                    canvas.drawCircle(x, y, radioSmall, paint);
                    canvas.drawRect(x - radioSmall / 2, 0, x + radioSmall / 2, y, paint);
                    break;

            }
        }
    }

    public int getZoneType() {
        return ZoneType;
    }

    public void setZoneType(int zoneType) {
        ZoneType = zoneType;
    }

    public float getRadioSmall() {
        return radioSmall;
    }

    public void setRadioSmall(float radioSmall) {
        this.radioSmall = radioSmall;
    }

    public float getRadioBig() {
        return radioBig;
    }

    public void setRadioBig(float radioBig) {
        this.radioBig = radioBig;
    }

    public float getRadioLeft() {
        return radioLeft;
    }

    public void setRadioLeft(float radioLeft) {
        this.radioLeft = radioLeft;
    }

    public int getPositionType() {
        return positionType;
    }

    public void setPositionType(int positionType) {
        this.positionType = positionType;
    }

    public int getRadioBackgroundColor() {
        return radioBackgroundColor;
    }

    public void setRadioBackgroundColor(int radioBackgroundColor) {
        this.radioBackgroundColor = radioBackgroundColor;
    }

    public int getRadioforColor() {
        return radioforColor;
    }

    public void setRadioforColor(int radioforColor) {
        this.radioforColor = radioforColor;
    }
}