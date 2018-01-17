package com.ankhrom.base.custom.prefs;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ankhrom.base.common.statics.StringHelper;
import com.google.gson.Gson;

/**
 * Created by R' on 12/30/2017.
 */

public abstract class BaseGsonPrefs<T> extends BasePrefs {

    private static final String DATA = "data";

    private final Class<T> clazz;

    public BaseGsonPrefs(@NonNull Context context, Class<T> clazz) {
        super(context);

        this.clazz = clazz;
    }

    public void set(T data) {

        edit().putString(DATA, data == null ? null : new Gson().toJson(data)).apply();
    }

    public T get() {

        String json = getPrefs().getString(DATA, null);

        if (StringHelper.isEmpty(json)) {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return new Gson().fromJson(json, clazz);
    }

    public void clear() {

        set(null);
    }
}
