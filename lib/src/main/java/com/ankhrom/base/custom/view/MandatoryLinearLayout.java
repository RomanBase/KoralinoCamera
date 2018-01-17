package com.ankhrom.base.custom.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ankhrom.base.R;
import com.ankhrom.base.common.statics.ObjectHelper;
import com.ankhrom.base.common.statics.StringHelper;

public class MandatoryLinearLayout extends LinearLayout implements View.OnFocusChangeListener {

    public static final String MANDATORY_TAG = "mandatory";

    //public static final int OK = 0;
    public static final int NORMAL = 1;
    public static final int REQUIRED = 2;

    private int backgroundNormal;
    private int backgroundRequired;
    private String regex;

    private int currentState = NORMAL;


    private OnStateChangedListener stateListener;

    public MandatoryLinearLayout(Context context) {
        super(context);
    }

    public MandatoryLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initArgs(context, attrs);
    }

    public MandatoryLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initArgs(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MandatoryLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initArgs(context, attrs);
    }

    private void initArgs(Context context, AttributeSet attrs) {

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MandatoryLinearLayout, 0, 0);
        try {
            backgroundNormal = ta.getResourceId(R.styleable.MandatoryLinearLayout_backgroundNormal, 0);
            backgroundRequired = ta.getResourceId(R.styleable.MandatoryLinearLayout_backgroundRequired, 0);
            regex = ta.getString(R.styleable.MandatoryLinearLayout_regex);
        } finally {
            ta.recycle();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (ObjectHelper.equals(child.getTag(), MANDATORY_TAG)) {
                child.setOnFocusChangeListener(this);
            }
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);

        if (ObjectHelper.equals(child.getTag(), MANDATORY_TAG)) {
            child.setOnFocusChangeListener(this);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
            setState(NORMAL);
        } else {
            if (v instanceof TextView) {
                String text = ((TextView) v).getText().toString();
                int state = (StringHelper.isEmpty(text) && (StringHelper.isEmpty(regex) || text.matches(regex))) ? REQUIRED : NORMAL;
                setState(state);
                if (stateListener != null) {
                    stateListener.onStateChanged(MandatoryLinearLayout.this, state == NORMAL);
                }
            }
        }
    }

    public void setState(int state) {

        if (currentState == state) {
            return;
        }

        currentState = state;

        switch (state) {
            case NORMAL:
                setBackgroundResource(backgroundNormal);
                break;
            case REQUIRED:
                setBackgroundResource(backgroundRequired);
                break;
        }
    }

    public void setStateListener(OnStateChangedListener stateListener) {
        this.stateListener = stateListener;
    }

    public static interface OnStateChangedListener {

        void onStateChanged(View view, boolean isValid);
    }
}
