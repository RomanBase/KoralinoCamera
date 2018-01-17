package com.ankhrom.base.observable;

import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.Arrays;
import java.util.List;

import com.ankhrom.base.common.statics.ObjectHelper;

public class SpinnerObservable extends BaseObservable implements Spinner.OnItemSelectedListener {

    private Spinner view;

    private Spinner.OnItemSelectedListener listener;
    private String selected;
    private int selectedIndex;
    private boolean preventNotify = false;

    List<String> list;

    public SpinnerObservable(String... items) {

        if (items == null || items.length == 0) {
            return;
        }

        this.list = Arrays.asList(items);
    }

    public void preventNotifyOneTime() {
        preventNotify = true;
    }

    public String getSelected() {

        return selected;
    }

    public int get() {

        return selectedIndex;
    }

    public void set(int index) {

        if (selectedIndex != index) {
            selectedIndex = index;
            setSelected();
            notifyChange();
        } else {
            preventNotify = false;
        }
    }

    public void set(String value) {

        if (!ObjectHelper.equals(value, selected)) {
            SpinnerAdapter adapter = view.getAdapter();
            int count = adapter.getCount();
            for (int i = 0; i < count; i++) {
                if (ObjectHelper.equals(value, adapter.getItem(i))) {
                    set(i);
                    break;
                }
            }
        } else {
            preventNotify = false;
        }
    }

    public void set(List<String> items) {

        if (!ObjectHelper.equals(list, items)) {
            if (view == null) {
                this.list = items;
            } else {
                setData(items);
                notifyChange();
            }
        } else {
            preventNotify = false;
        }
    }

    void setData(List<String> items) {

        if (view.getAdapter() != null) {
            view.setAdapter(null);
        }

        if (items != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            view.setAdapter(adapter);
        } else {
            view.setAdapter(null);
        }
    }

    void setSelected() {

        if (view == null) {
            return;
        }

        view.setSelection(selectedIndex);
    }

    public void setListener(Spinner.OnItemSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        selectedIndex = position;
        selected = parent.getItemAtPosition(position).toString();
        if (listener != null) {
            if (preventNotify) {
                preventNotify = false;
                return;
            }
            listener.onItemSelected(parent, view, position, id);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        if (listener != null) {
            listener.onNothingSelected(parent);
        }
    }

    @BindingAdapter({"app:selection"})
    public static void bindSpinner(Spinner view, SpinnerObservable observable) {

        if (view == null || observable == null) {
            return;
        }

        if (!ObjectHelper.equals(view, observable.getView())) {
            observable.setView(view);

            if (observable.list != null) {
                observable.setData(observable.list);
                observable.notifyChange();
                observable.list = null;
            }

            if (observable.get() > 0) {
                view.setSelection(observable.get());
            }

            view.setOnItemSelectedListener(observable);
        }
    }

    void setView(Spinner view) {
        this.view = view;
    }

    Spinner getView() {

        return view;
    }
}
