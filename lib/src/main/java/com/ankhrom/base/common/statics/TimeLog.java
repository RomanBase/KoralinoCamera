package com.ankhrom.base.common.statics;

import android.os.SystemClock;

import com.ankhrom.base.Base;

public final class TimeLog {

    private long hint;
    private int index;

    public TimeLog() {
        hint();
    }

    public void hint() {

        hint = SystemClock.uptimeMillis();
        index = 0;
    }

    public void print() {

        Base.logV("Tim_" + index++, SystemClock.uptimeMillis() - hint);
    }

    public void printHint() {

        Long time = SystemClock.uptimeMillis();
        Base.logV("Tim_" + index++, time - hint);
        hint = time;
    }
}
