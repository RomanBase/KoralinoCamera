package com.ankhrom.base.model;


import android.content.Context;

import com.ankhrom.base.observable.AdapterRecycleBinder;

import java.util.Collection;

public abstract class AdapterModel<T extends ItemModel> extends Model {

    public final AdapterRecycleBinder<T> adapter;

    public AdapterModel(Context context) {

        adapter = new AdapterRecycleBinder<T>(context);
    }

    public AdapterModel(Context context, Collection<T> collection) {
        this(context);

        adapter.addAll(collection);
    }

}
