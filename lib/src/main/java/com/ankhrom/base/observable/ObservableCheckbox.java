package com.ankhrom.base.observable;


import android.databinding.BindingAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ankhrom.base.interfaces.MandatoryView;

public class ObservableCheckbox extends BaseObservableField<CheckBox, Boolean> {

    public ObservableCheckbox() {
        super(false);
    }

    public ObservableCheckbox(boolean value) {
        super(value);
    }

    public boolean isValid() {

        CheckBox checkbox = view.get();
        if (checkbox instanceof MandatoryView) {
            return ((MandatoryView) checkbox).isValid();
        }

        return value;
    }

    @Override
    protected void onBindingCreated(CheckBox view) {

        view.setChecked(value);
        view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
                set(value);
            }
        });
    }

    @Override
    protected void onValueChanged(Boolean value) {

        if (value != view.get().isChecked()) {
            view.get().setChecked(value);
        }
    }

    @BindingAdapter({"app:checked"})
    public static void bindSwitch(CheckBox view, final ObservableCheckbox observable) {

        if (observable == null) {
            return;
        }

        observable.bindToView(view);
    }
}
