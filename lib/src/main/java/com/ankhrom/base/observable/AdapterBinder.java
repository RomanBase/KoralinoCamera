package com.ankhrom.base.observable;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.ankhrom.base.model.ItemModel;

public class AdapterBinder<T extends ItemModel> extends ArrayAdapter<T> {

    private final LayoutInflater inflater;
    private final List<T> items;

    /**
     * @param context Aplication context
     */
    public AdapterBinder(Context context) {
        super(context, -1);

        inflater = LayoutInflater.from(context);
        items = new ArrayList<>();
    }

    @Override
    public void add(T item) {

        items.add(item);
        super.add(item);
    }

    @Override
    public void addAll(@NonNull Collection<? extends T> collection) {

        items.addAll(collection);
        super.addAll(collection);
    }

    @Override
    public void addAll(T[] itemArray) {

        Collections.addAll(items, itemArray);
        super.addAll(itemArray);
    }

    public T getItem(int index) {

        return items.get(index);
    }

    @Override
    public void remove(T item) {

        items.remove(item);
        super.remove(item);
    }

    @Override
    public void clear() {

        items.clear();
        super.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewDataBinding binding;
        ItemModel item = items.get(position);

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
}

