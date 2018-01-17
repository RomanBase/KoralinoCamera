package com.ankhrom.base.interfaces;

import android.view.View;

import com.ankhrom.base.model.ItemModel;

public interface OnItemSelectedListener<T extends ItemModel> {

    void onItemSelected(View view, T model);
}
