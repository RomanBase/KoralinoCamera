package com.ankhrom.base.custom.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by R' on 12/30/2017.
 */

public abstract class BasePrefs {

    protected static final String DEFAULT_JSON = "{}";
    protected static final String DEFAULT_JSON_LIST = "[]";

    protected final Context context;
    protected SharedPreferences prefs;

    public BasePrefs(@NonNull Context context) {

        this.context = context;
    }

    protected abstract String getPrefsName();

    protected int getPrefsMode() {
        return Context.MODE_PRIVATE;
    }

    public SharedPreferences getPrefs() {

        if (prefs == null) {
            prefs = context.getSharedPreferences(getPrefsName(), getPrefsMode());
        }

        return prefs;
    }

    public void setPrefs(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    protected SharedPreferences.Editor edit() {

        return getPrefs().edit();
    }
}
