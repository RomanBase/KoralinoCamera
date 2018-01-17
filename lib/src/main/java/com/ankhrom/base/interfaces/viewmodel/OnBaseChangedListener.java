package com.ankhrom.base.interfaces.viewmodel;


public interface OnBaseChangedListener {

    void onViewModelChanged(ViewModel viewModel);

    void onScreenOptionsChanged(int options);

    void onReceiveArgs(int requestCode, Object[] args);
}
