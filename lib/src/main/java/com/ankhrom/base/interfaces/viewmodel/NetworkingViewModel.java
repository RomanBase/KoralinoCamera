package com.ankhrom.base.interfaces.viewmodel;

import com.ankhrom.base.model.Model;
import com.ankhrom.base.networking.volley.BaseVolleyRequest;
import com.ankhrom.base.viewmodel.BaseViewModel;

public interface NetworkingViewModel<T extends Model, U> {

    BaseVolleyRequest<U> createRequest(BaseViewModel.VolleyRequest request);

    T createModel(U response);
}
