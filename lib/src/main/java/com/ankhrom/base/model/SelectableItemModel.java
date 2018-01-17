package com.ankhrom.base.model;

import android.view.View;

import com.ankhrom.base.interfaces.OnItemSelectedListener;
import com.ankhrom.base.interfaces.SelectableItem;

public abstract class SelectableItemModel extends ItemModel implements SelectableItem {

    private OnItemSelectedListener itemSelectedListener;

    public SelectableItemModel() {
    }

    public SelectableItemModel(OnItemSelectedListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onItemSelected(View view) {

        if (itemSelectedListener != null) {
            itemSelectedListener.onItemSelected(view, this);
        }
    }
}
