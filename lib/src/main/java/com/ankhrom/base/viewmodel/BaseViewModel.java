package com.ankhrom.base.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ankhrom.base.Base;
import com.ankhrom.base.BaseActivity;
import com.ankhrom.base.custom.args.InitArgs;
import com.ankhrom.base.interfaces.Initializable;
import com.ankhrom.base.interfaces.ObjectFactory;
import com.ankhrom.base.interfaces.viewmodel.NetworkingViewModel;
import com.ankhrom.base.interfaces.viewmodel.ViewModel;
import com.ankhrom.base.interfaces.viewmodel.ViewModelNavigation;
import com.ankhrom.base.interfaces.viewmodel.ViewModelObserver;
import com.ankhrom.base.model.Model;
import com.ankhrom.base.networking.volley.BaseVolleyRequest;
import com.ankhrom.base.networking.volley.RequestBuilder;

public abstract class BaseViewModel<S extends ViewDataBinding, T extends Model> extends Fragment implements Initializable, ViewModel {

    public final ObservableBoolean isLoading = new ObservableBoolean(false);
    public final ObservableBoolean cacheOnly = new ObservableBoolean(false);

    protected T model;
    protected S binding;

    private String title;
    private ViewModelNavigation navigation;

    private boolean isModelLoaded = false;
    private boolean isModelCreated = false;
    private boolean isModelBinded = true;

    public BaseViewModel() {

    }

    public abstract int getLayoutResource();

    public abstract int getBindingResource();

    @Override
    public void init(InitArgs args) {

    }

    @Override
    public void onInit() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (binding != null) {
            return binding.getRoot();
        }

        binding = DataBindingUtil.inflate(inflater, getLayoutResource(), container, false);

        int variable = getBindingResource();
        if (variable > 0) {
            binding.setVariable(variable, this);
        }

        if (model != null) {
            bindModel();
        }

        onCreateViewBinding(binding);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        onModelCreatedStep();
    }

    @Override
    public void loadModel() {

        isModelLoaded = false;
        isModelCreated = false;

        try {
            if (this instanceof NetworkingViewModel) {
                isLoading.set(true);
                //noinspection unchecked
                createModelViaVolley((NetworkingViewModel) this, getRequestQueue());
            }
        } catch (Exception e) {
            Base.logE(e.fillInStackTrace());
            e.printStackTrace();
            isLoading.set(false);
            onModelError();
        }
    }

    protected void onModelLoaded(T model) {

        isModelLoaded = true;

        this.model = model;
        bindModel();
        onModelCreatedStep();
    }

    protected void createModelViaVolley(@NonNull final NetworkingViewModel<T, Object> networking, @NonNull RequestQueue queue) throws Exception {

        VolleyNetworkingListener listener = new VolleyNetworkingListener() {

            @Override
            T createModelFromResponse(Object response) {
                return networking.createModel(response);
            }
        };

        BaseVolleyRequest<?> request = networking.createRequest(new VolleyRequest(listener));

        if (request != null) {
            queue.add(request);
        } else {
            isLoading.set(false);
        }
    }

    protected RequestQueue getRequestQueue() {

        return getObserver().getRequestQueue();
    }

    private void onModelCreatedStep() {

        if (!isModelCreated && isModelLoaded && getView() != null) {
            isModelCreated = true;

            if (!isModelBinded && model != null) {
                bindModel();
            }
            onModelCreated();
        }
    }

    @Override
    public boolean isModelLoaded() {
        return isModelLoaded;
    }

    public void setModelLoaded(boolean modelLoaded) {
        this.isModelLoaded = modelLoaded;
    }

    public T getModel() {
        return model;
    }

    public S getBinding() {
        return binding;
    }

    public void setModel(T model) {

        this.model = model;
        isModelLoaded = true;
        isModelBinded = false;
        bindModel();
        onModelCreatedStep();
    }

    public void bindModel(Model model) {

        if (model != null && binding != null) {
            int variable = model.getVariableBindingResource();
            if (variable > 0) {
                binding.setVariable(variable, model);
            }
        }
    }

    private void bindModel() {

        if (model != null && binding != null) {
            isModelBinded = true;
            bindModel(model);
        }
    }

    /**
     * View and Context are not created yet !
     */
    protected void onCreateViewBinding(S binding) {

    }

    /**
     * Called when model succesfully loaded and view with binding created
     */
    protected void onModelCreated() {

    }

    /**
     * Called when model loading fails
     */
    protected void onModelError() {

    }

    public void setTitle(int titleResource) {
        this.title = getContext().getString(titleResource);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isModelCreated() {
        return isModelCreated;
    }

    @Override
    public Context getContext() {

        ViewModelObserver observer = getObserver();
        if (observer != null) {
            return observer.getContext();
        }

        View view = getView();
        if (view != null) {
            return view.getContext();
        }

        return super.getContext();
    }

    public BaseActivity getBaseActivity() {

        ViewModelObserver observer = getObserver();
        if (observer instanceof BaseViewModelObserver) {
            return ((BaseViewModelObserver) observer).getBaseActivity();
        }

        Context context = getContext();
        if (context instanceof BaseActivity) {
            return (BaseActivity) context;
        }

        return null;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean onBaseActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }

    @Override
    public void onReceiveArgs(int requestCode, Object[] args) {

    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void onViewStackChanged(boolean isBackStacked, boolean isVisible) {

    }

    @Override
    public void setNavigation(ViewModelNavigation navigation) {
        this.navigation = navigation;
    }

    public ViewModelNavigation getNavigation() {
        return navigation;
    }

    public <V extends ViewModelObserver> V getObserver() {

        if (navigation == null) {
            return null;
        }

        return navigation.getObserver();
    }

    public <V extends ObjectFactory> V getFactory() {

        if (navigation == null) {
            return null;
        }

        return getObserver().getFactory();
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    protected void setLoading(boolean loading) {

        ViewModel currentViewModel = getObserver().getCurrentViewModel();

        if (currentViewModel.equals(this)) {
            isLoading.set(loading);
        } else {
            if (currentViewModel instanceof BaseViewModel) {
                ((BaseViewModel) currentViewModel).isLoading.set(loading);
            }
        }
    }

    protected <V extends ViewModel> V openViewModel(Class<V> clazz, Object... args) {

        V vm = getFactory().getViewModel(clazz, args);

        getNavigation().setViewModel(vm, false);

        return vm;
    }

    protected <V extends ViewModel> V openViewModelRoot(Class<V> clazz, Object... args) {

        V vm = getFactory().getViewModel(clazz, args);

        getNavigation().setViewModel(vm, true);

        return vm;
    }

    protected <V extends ViewModel> V addViewModel(Class<V> clazz, Object... args) {

        V vm = getFactory().getViewModel(clazz, args);

        getNavigation().addViewModel(vm, false);

        return vm;
    }

    protected <V extends ViewModel> V addViewModelRoot(Class<V> clazz, Object... args) {

        V vm = getFactory().getViewModel(clazz, args);

        getNavigation().addViewModel(vm, true);

        return vm;
    }

    protected <V extends ViewModel> V replaceViewModel(Class<V> clazz, @IdRes int containerId, Object... args) {

        V vm = getFactory().getViewModel(clazz, args);

        getNavigation().replaceViewModel(vm, containerId);

        return vm;
    }

    /**
     * ##################### VOLLEY LISTENER #####################
     */
    public abstract class VolleyNetworkingListener implements Response.Listener, Response.ErrorListener {

        VolleyNetworkingListener() {

        }

        abstract T createModelFromResponse(Object response);

        @Override
        public void onResponse(Object response) {

            onModelLoaded(createModelFromResponse(response));

            isLoading.set(false);
        }

        @Override
        public void onErrorResponse(VolleyError error) {

            isLoading.set(false);
            onModelError();
        }
    }

    /**
     * ##################### VOLLEY REQUEST #####################
     */
    public class VolleyRequest {

        private final VolleyNetworkingListener listener;

        VolleyRequest(VolleyNetworkingListener listener) {
            this.listener = listener;
        }

        public RequestBuilder get(String url) {

            return RequestBuilder.get(url)
                    .listener(listener)
                    .errorListener(listener);
        }

        public RequestBuilder post(String url) {

            return RequestBuilder.post(url)
                    .listener(listener)
                    .errorListener(listener);
        }

        public RequestBuilder put(String url) {

            return RequestBuilder.put(url)
                    .listener(listener)
                    .errorListener(listener);
        }

        public RequestBuilder request(int method, String url) {

            return RequestBuilder.request(method, url)
                    .listener(listener)
                    .errorListener(listener);
        }

    }

}
