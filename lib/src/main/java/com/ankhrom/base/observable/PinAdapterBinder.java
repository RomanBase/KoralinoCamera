package com.ankhrom.base.observable;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import com.ankhrom.base.model.ItemModel;
import libs.view.PinnedSectionListView;

public class PinAdapterBinder extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {

    private static final int TYPE_PIN = 1;
    private static final int TYPE_ITEM = 0;

    public static abstract class PinAdapterItem extends ItemModel {

        protected boolean isPin = false;

        public PinAdapterItem() {

        }

        public PinAdapterItem(boolean isPin) {
            this.isPin = isPin;
        }

        public int getViewType() {
            return isPin ? TYPE_PIN : TYPE_ITEM;
        }
    }

    private final List<PinAdapterItem> list;
    private final LayoutInflater inflater;

    /**
     * @param context Application context
     * @param list    List collection of Pins and Items
     */
    public PinAdapterBinder(Context context, List<PinAdapterItem> list) {

        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == TYPE_PIN;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public PinAdapterItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PinAdapterItem item = getItem(position);

        ViewDataBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, item.getLayoutResource(), parent, false);
            convertView = binding.getRoot();
        } else {
            binding = DataBindingUtil.bind(convertView);
        }

        item.onItemBinded(binding);

        int variable = item.getVariableBindingResource();
        if (variable > 0) {
            binding.setVariable(variable, item);
            binding.executePendingBindings();
        }

        return convertView;
    }

    public void bindPinnedView(View view) {

        DataBindingUtil.bind(view).executePendingBindings();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getViewType();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
