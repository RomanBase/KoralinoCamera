package com.ankhrom.base.viewmodel;

import android.databinding.ViewDataBinding;

import com.ankhrom.base.interfaces.viewmodel.NetworkingViewModel;
import com.ankhrom.base.model.Model;

public abstract class BaseNetworkingViewModel<S extends ViewDataBinding, T extends Model, U> extends BaseViewModel<S, T> implements NetworkingViewModel<T, U> {

}
