package com.ankhrom.base.observable;

import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ankhrom.base.animators.BaseAnim;
import com.ankhrom.base.common.statics.ObjectHelper;
import com.ankhrom.base.model.ItemModel;

public class ViewGroupObservable<T extends ItemModel> extends BaseObservable {

    private final List<ItemViewPair> list;
    private ViewGroup viewGroup;
    private LayoutInflater inflater;
    private View footer;

    private int separatorResource;
    private boolean separatorAsBounds;

    public ViewGroupObservable() {
        this.list = new ArrayList<>();
    }

    public void add(T item) {

        ItemViewPair pair = new ItemViewPair();
        pair.model = item;

        list.add(pair);

        if (viewGroup != null) {
            bindItem(pair);
        }
    }

    public void setSeparatorResource(int separatorResource) {
        setSeparatorResource(separatorResource, false);
    }

    public void setSeparatorResource(int separatorResource, boolean asBounds) {
        this.separatorResource = separatorResource;
        this.separatorAsBounds = asBounds;
    }

    @SafeVarargs
    public final void addAll(T... items) {

        if (items == null) {
            return;
        }

        for (T item : items) {
            ItemViewPair pair = new ItemViewPair();
            pair.model = item;
            list.add(pair);
            if (viewGroup != null) {
                bindItem(pair);
            }
        }
    }

    public void addAll(Collection<T> items) {

        for (T item : items) {
            ItemViewPair pair = new ItemViewPair();
            pair.model = item;
            list.add(pair);
            if (viewGroup != null) {
                bindItem(pair);
            }
        }
    }

    public void remove(T item) {

        ItemViewPair pair = findItemPair(item);

        if (pair != null) {
            if (viewGroup != null) {
                animateRemove(pair.view);
            }

            list.remove(pair);
        }
    }

    public ItemModel remove(int index) {

        if (viewGroup != null) {
            ItemViewPair pair = list.get(index);
            animateRemove(pair.view);
            if (pair.separator != null) {
                viewGroup.removeView(pair.separator);
            }
        }

        return list.remove(index).model;
    }

    @SuppressWarnings("unchecked")
    public T get(int i) {
        return (T) list.get(i).model;
    }

    public int size() {
        return list.size();
    }

    public ItemViewPair findItemPair(T item) {

        for (ItemViewPair pair : list) {
            if (item.equals(pair.model)) {
                return pair;
            }
        }

        return null;
    }

    public List<ItemViewPair> getList() {
        return list;
    }

    @BindingAdapter({"app:items"})
    public static void bindList(ViewGroup view, final ViewGroupObservable observable) {


        if (view == null || observable == null) {
            return;
        }

        if (ObjectHelper.equals(view, observable.viewGroup)) {
            return;
        }

        observable.setView(view);

        @SuppressWarnings("unchecked")
        List<ItemViewPair> items = observable.getList();

        if (items.isEmpty()) {
            return;
        }

        for (ItemViewPair pair : items) {
            observable.bindItem(pair);
        }
    }

    void setView(ViewGroup view) {
        this.viewGroup = view;
        this.inflater = LayoutInflater.from(view.getContext());
    }

    void bindItem(ItemViewPair pair) {

        if (separatorResource > 0) {
            if (viewGroup.getChildCount() > 0 || separatorAsBounds) {
                pair.separator = footer == null ? inflater.inflate(separatorResource, viewGroup, true) : footer;
            }

            bindItemPair(pair);

            if (separatorAsBounds) {
                footer = inflater.inflate(separatorResource, viewGroup, true);
            }
        } else {
            bindItemPair(pair);
        }
    }

    void bindItemPair(ItemViewPair pair) {

        ViewDataBinding binding = DataBindingUtil.inflate(inflater, pair.model.getLayoutResource(), viewGroup, true);

        pair.model.onItemBinded(binding);

        int br = pair.model.getVariableBindingResource();
        if (br > 0) {
            binding.setVariable(br, pair.model);
        }
        pair.view = binding.getRoot();
        BaseAnim.show(pair.view);
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

        if (viewGroup == null) {
            return;
        }

        scroll((View) viewGroup.getParent(), direction);
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

    private void animateRemove(final View view) {

        if (view == null || viewGroup == null) {
            return;
        }

        Animation a = BaseAnim.collapse(view);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewGroup.removeView(view);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public static class ItemViewPair {

        ItemModel model;
        View view;
        View separator;

        public ItemModel getModel() {
            return model;
        }

        public void setModel(ItemModel model) {
            this.model = model;
        }

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
        }
    }
}
