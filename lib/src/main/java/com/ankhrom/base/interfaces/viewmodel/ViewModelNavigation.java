package com.ankhrom.base.interfaces.viewmodel;


import android.support.annotation.IdRes;

public interface ViewModelNavigation {

    void setViewModel(ViewModel viewModel, boolean clearBackStack);

    void addViewModel(ViewModel viewModel, boolean asRootContent);

    void replaceViewModel(ViewModel viewModel, @IdRes int containerId);

    void navigateBack();

    void setPreviousViewModel();

    <T extends ViewModelObserver> T getObserver();

}
