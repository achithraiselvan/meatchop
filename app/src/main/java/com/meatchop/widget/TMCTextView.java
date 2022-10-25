/*$Id$*/
package com.meatchop.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.meatchop.R;

import java.lang.reflect.Field;


public class TMCTextView extends androidx.appcompat.widget.AppCompatTextView implements View.OnTouchListener {
    private static final String TAG = "TextView";
    private TypedArray attrArray;
    private int color;

    public TMCTextView(Context context) {
        super(context);
    }

    public TMCTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        attrArray = context.obtainStyledAttributes(attrs, R.styleable.TMCTextView);
        if (isClickable() && attrArray.getBoolean(R.styleable.TMCTextView_stateChangeable, true)) {
            color = getCurrentTextColor();
            setOnTouchListener(this);
        }
        setCustomFont(context, attrs);
    }

    public TMCTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        attrArray = context.obtainStyledAttributes(attrs, R.styleable.TMCTextView);
//        if (isClickable() && attrArray.getBoolean(R.styleable.BCRTextView_stateChangeable, true)) {
//            color = getTextColor(context, attrArray, R.color.primary_text_color);
//            setOnTouchListener(this);
//        }
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        String customFont = attrArray.getString(R.styleable.TMCTextView_customTextViewFont);
        setCustomFont(ctx, customFont);
        attrArray.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf;
        try {
            if (asset == null) {
                return false;
            }
            tf = Typeface.createFromAsset(ctx.getAssets(), "fonts/" + asset + ".ttf");
        } catch (Exception e) {
            Log.e(TAG, "Could not get typeface: " + e.getMessage());
            return false;
        }
        setTypeface(tf);
        return true;
    }

    public void setMarqueeSpeed(float speed, boolean speedIsMultiplier) {
        try {
            Field f = this.getClass().getDeclaredField("mMarquee");
            f.setAccessible(true);

            Object marquee = f.get(this);
            if (marquee != null) {

                String scrollSpeedFieldName = "mScrollUnit";
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    scrollSpeedFieldName = "mPixelsPerSecond";

                Field mf = marquee.getClass().getDeclaredField(scrollSpeedFieldName);
                mf.setAccessible(true);

                float newSpeed = speed;
                if (speedIsMultiplier)
                    newSpeed = mf.getFloat(marquee) * speed;

                mf.setFloat(marquee, newSpeed);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //setTextColor(attrArray.getColor(R.styleable.LeadsTextView_tv_selectedColor, R.color.primary_text_color));
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                setTextColor(color);
                break;

        }
        return false;
    }
}