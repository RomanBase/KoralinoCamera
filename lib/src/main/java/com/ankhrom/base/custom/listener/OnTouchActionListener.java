package com.ankhrom.base.custom.listener;

import android.view.MotionEvent;
import android.view.View;

public abstract class OnTouchActionListener implements View.OnTouchListener {

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onTouchActionDown(view);
                break;
            case MotionEvent.ACTION_MOVE:
                if (view.isClickable()) {
                    break;
                }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                onTouchActionUp(view);
                break;
        }

        return false;
    }

    public abstract void onTouchActionDown(View view);

    public abstract void onTouchActionUp(View view);
}
