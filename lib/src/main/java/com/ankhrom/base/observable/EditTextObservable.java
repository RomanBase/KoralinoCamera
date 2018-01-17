package com.ankhrom.base.observable;

import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.ankhrom.base.common.statics.ObjectHelper;
import com.ankhrom.base.common.statics.StringHelper;
import com.ankhrom.base.interfaces.MandatoryView;

public class EditTextObservable extends BaseObservableField<EditText, String> {

    public EditTextObservable() {
    }

    public EditTextObservable(String value) {
        super(value);
    }

    public boolean isValid() {

        EditText editText = view.get();

        if (editText instanceof MandatoryView) {
            return ((MandatoryView) editText).isValid();
        }

        return !StringHelper.isEmpty(value);
    }

    @Override
    protected void onBindingCreated(EditText view) {

        view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                set(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        if (!ObjectHelper.equals(view.getText().toString(), value)) {
            view.setText(value);
        }
    }

    @Override
    protected void onValueChanged(String value) {

        if (!ObjectHelper.equals(value, view.get().getText().toString())) {
            view.get().setText(value);
        }
    }

    @BindingConversion
    public static String convertBindableToString(EditTextObservable bindableString) {
        return bindableString == null || bindableString.isEmpty() ? StringHelper.EMPTY : bindableString.get();
    }

    @BindingAdapter({"app:text"})
    public static void bindEditText(EditText editText, final EditTextObservable observable) {

        if (observable == null) {
            return;
        }

        observable.bindToView(editText);
    }

    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }
}
