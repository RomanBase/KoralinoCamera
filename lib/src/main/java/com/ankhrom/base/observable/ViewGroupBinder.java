package com.ankhrom.base.observable;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import com.ankhrom.base.animators.BaseAnim;
import com.ankhrom.base.model.ItemModel;

public class ViewGroupBinder<T extends ItemModel> {

    private final List<View> views;
    private final List<T> items;
    private final LayoutInflater inflater;
    private final ViewGroup parent;

    public ViewGroupBinder(Context context, ViewGroup view, List<T> items) {

        this.views = new ArrayList<>();
        this.items = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
        this.parent = view;

        if (items != null) {
            addViews(items);
        }
    }

    public T get(int index) {

        return items.get(index);
    }

    public List<T> getItems() {

        return items;
    }

    public View getView(int index) {

        return views.get(index);
    }

    public List<View> getViews() {

        return views;
    }

    public void add(T item) {

        addView(item);
    }

    @SafeVarargs
    public final void add(T... items) {

        if (items == null) {
            return;
        }

        addViews(items);
    }

    public void remove(int index) {

        if (index >= views.size()) {
            return;
        }

        items.remove(index);
        remove(views.get(index));
    }

    public void remove(final View item) {

        views.remove(item);
        Animation a = BaseAnim.collapse(item);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                parent.removeView(item);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void clear() {

        parent.removeAllViews();
        views.clear();
    }

    public int size() {

        return views.size();
    }

    private void addView(T item) {

        ViewDataBinding binding = DataBindingUtil.inflate(inflater, item.getLayoutResource(), parent, true);

        item.onItemBinded(binding);

        int variable = item.getVariableBindingResource();
        if (variable > 0) {
            binding.setVariable(variable, item);
        }
        View view = binding.getRoot();
        views.add(view);
        items.add(item);
        BaseAnim.show(view);
    }

    @SafeVarargs
    private final void addViews(T... items) {

        for (T item : items) {
            addView(item);
        }
    }

    private void addViews(List<T> items) {

        for (T item : items) {
            addView(item);
        }
    }

    public void scrollDown() {

        scroll(View.FOCUS_DOWN);
    }

    public void scrollUp() {

        scroll(View.FOCUS_UP);
    }

    public void scrollLeft() {

        scroll(View.FOCUS_LEFT);
    }

    public void scrollRight() {

        scroll(View.FOCUS_RIGHT);
    }

    public void scroll(int direction) {

        scroll((View) parent.getParent(), direction);
    }

    public void scroll(View view, final int direction) {

        if (view != null) {
            if (view instanceof ScrollView) {
                final ScrollView sv = ((ScrollView) view);
                sv.post(new Runnable() {
                    @Override
                    public void run() {
                        sv.fullScroll(direction);
                    }
                });
            } else if (view instanceof HorizontalScrollView) {
                final HorizontalScrollView hsv = ((HorizontalScrollView) view);
                hsv.post(new Runnable() {
                    @Override
                    public void run() {
                        hsv.fullScroll(direction);
                    }
                });
            }
        }
    }
}
