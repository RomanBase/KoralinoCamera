package com.ankhrom.base.model;


import android.view.MenuItem;

public abstract class ToolbarItemModel {

    private String title;
    private Object variable;
    private boolean showAsAction;
    private int imageResourceId;

    public ToolbarItemModel(String title) {
        this.title = title;
    }

    public abstract void onClick(MenuItem item);

    public boolean isShowAsAction() {
        return showAsAction;
    }

    public ToolbarItemModel setShowAsAction(boolean showAsAction) {
        this.showAsAction = showAsAction;
        return this;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public ToolbarItemModel setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Object getVariable() {
        return variable;
    }

    public void setVariable(Object variable) {
        this.variable = variable;
    }
}
