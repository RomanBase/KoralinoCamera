package com.ankhrom.base.observable;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.ankhrom.base.model.ItemModel;
import libs.view.AnimatedExpandableListView;

public class ExpandableAdapterBinder<T extends ExpandableAdapterBinder.ExpandableGroup, V extends ExpandableAdapterBinder.ExpandableGroupChild> extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    public static abstract class ExpandableGroup<V extends ExpandableGroupChild> extends ItemModel {

        protected final List<V> childs;
        protected View view;
        protected boolean isExpanded = false;

        public ExpandableGroup() {
            childs = new ArrayList<>();
        }

        public ExpandableGroup(List<V> childs) {
            this.childs = childs;
        }
    }

    public static abstract class ExpandableGroupChild<T extends ExpandableGroup> extends ItemModel {

        protected T parent;
        protected View view;
        protected boolean isLast = false;

        public ExpandableGroupChild() {

        }
    }

    private final List<T> groups;
    private final LayoutInflater inflater;

    /**
     * @param context Application context
     * @param groups  List of expandable groups (group with list of childs)
     */
    public ExpandableAdapterBinder(Context context, List<T> groups) {
        this.inflater = LayoutInflater.from(context);
        if (groups == null) {
            this.groups = new ArrayList<>();
        } else {
            this.groups = groups;
        }
    }

    public void add(T group) {

        groups.add(group);
        super.notifyDataSetChanged();
    }

    public void remove(T group) {

        groups.remove(group);
        super.notifyDataSetChanged();
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return groups.get(groupPosition).childs.size();
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public T getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V getChild(int groupPosition, int childPosition) {
        return (V) groups.get(groupPosition).childs.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ViewDataBinding binding;
        ExpandableGroupChild child = getChild(groupPosition, childPosition);

        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, child.getLayoutResource(), parent, false);
            convertView = binding.getRoot();
        } else {
            binding = DataBindingUtil.bind(convertView);
        }

        child.parent = getGroup(groupPosition);
        child.view = convertView;
        child.isLast = isLastChild;

        child.onItemBinded(binding);

        int variable = child.getVariableBindingResource();
        if (variable > 0) {
            binding.setVariable(variable, child);
            binding.executePendingBindings();
        }

        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ViewDataBinding binding;
        ExpandableGroup group = getGroup(groupPosition);

        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, group.getLayoutResource(), parent, false);
            convertView = binding.getRoot();
        } else {
            binding = DataBindingUtil.bind(convertView);
        }

        group.isExpanded = isExpanded;
        group.view = convertView;

        int variable = group.getVariableBindingResource();
        if (variable > 0) {
            binding.setVariable(variable, group);
            binding.executePendingBindings();
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
