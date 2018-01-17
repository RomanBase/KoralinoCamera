package com.ankhrom.base.model;

import android.databinding.ViewDataBinding;

public abstract class ItemModel extends Model {

    public void onItemBinded(ViewDataBinding binding) {

    }

    public abstract int getLayoutResource();
}
