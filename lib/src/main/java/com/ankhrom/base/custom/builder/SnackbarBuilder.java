package com.ankhrom.base.custom.builder;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SnackbarBuilder {

    private int length = Snackbar.LENGTH_LONG;
    private final View root;
    private CharSequence text;
    private CharSequence actionText;
    private int color = 0;
    private int backgroundColor = 0;
    private int backgroundResource = 0;
    private int actionColor = 0;
    private boolean actionCaps = true;
    private ColorStateList actionColors;
    private View.OnClickListener listener;

    private SnackbarBuilder(View root) {
        this.root = root;
    }

    public static SnackbarBuilder with(@NonNull View root) {

        return new SnackbarBuilder(root);
    }

    public SnackbarBuilder lengthShort() {

        length = Snackbar.LENGTH_SHORT;
        return this;
    }

    public SnackbarBuilder lengthLong() {

        length = Snackbar.LENGTH_LONG;
        return this;
    }

    public SnackbarBuilder lengthInfinite() {

        length = Snackbar.LENGTH_INDEFINITE;
        return this;
    }

    public SnackbarBuilder text(CharSequence text) {

        this.text = text;
        return this;
    }

    public SnackbarBuilder text(int resource) {

        this.text = root.getResources().getString(resource);
        return this;
    }

    public SnackbarBuilder color(int color) {

        this.color = color;
        return this;
    }

    public SnackbarBuilder colorResource(@ColorRes int resource) {

        this.color = ContextCompat.getColor(root.getContext(), resource);
        return this;
    }

    public SnackbarBuilder backgroundColor(int color) {

        this.backgroundColor = color;
        return this;
    }

    public SnackbarBuilder backgroundColorResource(@ColorRes int resource) {

        this.backgroundColor = ContextCompat.getColor(root.getContext(), resource);
        return this;
    }

    public SnackbarBuilder backgroundResource(int resource) {

        this.backgroundResource = resource;
        return this;
    }

    public SnackbarBuilder actionColor(int color) {

        this.actionColor = color;
        return this;
    }

    public SnackbarBuilder actionColorResource(int resource) {

        this.actionColor = ContextCompat.getColor(root.getContext(), resource);
        return this;
    }

    public SnackbarBuilder actionColor(ColorStateList colors) {

        this.actionColors = colors;
        return this;
    }

    public SnackbarBuilder actionText(CharSequence text) {

        this.actionText = text;
        return this;
    }

    public SnackbarBuilder actionText(int resource) {

        this.actionText = root.getResources().getString(resource);
        return this;
    }

    public SnackbarBuilder actionListener(View.OnClickListener listener) {

        this.listener = listener;
        return this;
    }

    public SnackbarBuilder actionAllCaps(boolean caps) {

        this.actionCaps = caps;
        return this;
    }

    public Snackbar build() {

        Snackbar bar = Snackbar.make(root, text, length);

        if (backgroundResource > 0) {
            bar.getView().setBackgroundResource(backgroundResource);
        }

        if (backgroundColor < 0) {
            View view = bar.getView();
            Drawable toaster = DrawableCompat.wrap(view.getBackground().mutate().getConstantState().newDrawable());
            DrawableCompat.setTint(toaster, backgroundColor);

            if (Build.VERSION.SDK_INT > 15) {
                view.setBackground(toaster);
            } else {
                //noinspection deprecation
                view.setBackgroundDrawable(toaster);
            }
        }

        if (color < 0) {
            Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) bar.getView();
            TextView text = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
            text.setTextColor(color);
        }

        if (actionColors != null) {
            bar.setActionTextColor(actionColors);
        } else if (actionColor < 0) {
            bar.setActionTextColor(actionColor);
        }

        if (actionText != null) {
            bar.setAction(actionText, listener != null ? listener : new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            if (!actionCaps) {
                ((AppCompatButton) ((ViewGroup) bar.getView()).getChildAt(1)).setTransformationMethod(null);
            }
        }

        return bar;
    }

    public Snackbar buildAndShow() {

        Snackbar bar = build();
        bar.show();

        return bar;
    }

}
