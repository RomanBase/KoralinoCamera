package com.ankhrom.base.custom.builder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ToastBuilder {

    private final Context context;
    private int duration = Toast.LENGTH_SHORT;
    private CharSequence text;
    private int textColor = 0;
    private int backgroundResource = 0;
    private int backgroundColor = 0;

    private ToastBuilder(Context context) {
        this.context = context;
    }

    public static ToastBuilder with(@NonNull Context context) {

        return new ToastBuilder(context);
    }

    public ToastBuilder lengthShort() {

        duration = Toast.LENGTH_SHORT;
        return this;
    }

    public ToastBuilder lengthLong() {

        duration = Toast.LENGTH_LONG;
        return this;
    }

    public ToastBuilder text(CharSequence text) {

        this.text = text;
        return this;
    }

    public ToastBuilder text(int resource) {

        this.text = context.getResources().getString(resource);
        return this;
    }

    public ToastBuilder color(int color) {

        this.textColor = color;
        return this;
    }

    public ToastBuilder colorResource(int resource) {

        this.textColor = ContextCompat.getColor(context, resource);
        return this;
    }

    public ToastBuilder backgroundColor(int color) {

        this.backgroundColor = color;
        return this;
    }

    public ToastBuilder backgroundResource(int resource) {

        this.backgroundResource = resource;
        return this;
    }

    public Toast build() {

        @SuppressLint("ShowToast")
        Toast toast = Toast.makeText(context, text, duration);

        if (backgroundResource > 0) {
            toast.getView().setBackgroundResource(backgroundResource);
        }

        if (backgroundColor < 0) {
            View view = toast.getView();
            Drawable toaster = DrawableCompat.wrap(view.getBackground().mutate().getConstantState().newDrawable());
            DrawableCompat.setTint(toaster, backgroundColor);

            if (Build.VERSION.SDK_INT > 15) {
                view.setBackground(toaster);
            } else {
                //noinspection deprecation
                view.setBackgroundDrawable(toaster);
            }
        }

        if (textColor < 0) {
            ((TextView) ((ViewGroup) toast.getView()).getChildAt(0)).setTextColor(textColor);
        }

        return toast;
    }

    public Toast buildAndShow() {

        Toast toast = build();
        toast.show();

        return toast;
    }
}
