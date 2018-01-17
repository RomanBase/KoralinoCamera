package com.ankhrom.base.observable;


import android.databinding.BindingAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;

public class ObservableSwitch extends BaseObservableField<Switch, Boolean> {

    public ObservableSwitch() {
        super(false);
    }

    public ObservableSwitch(boolean value) {
        super(value);
    }

    @Override
    protected void onBindingCreated(Switch view) {

        view.setChecked(value);
        view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                set(checked);
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
    public static void bindSwitch(Switch view, final ObservableSwitch observable) {

        if (observable == null) {
            return;
        }

        observable.bindToView(view);
    }
}
