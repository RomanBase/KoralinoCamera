package com.ankhrom.base.model;

import android.databinding.ViewDataBinding;

public abstract class PresentationItemModel extends ItemModel {

    public void onBindingCreated(ViewDataBinding binding){

    }

    public void start() {

    }

    public void stop() {

    }

    public void resume() {

    }

    public void pause() {

    }

    public abstract boolean isDone();

    public abstract boolean isLoaded();

    public abstract  boolean isActive();
}
