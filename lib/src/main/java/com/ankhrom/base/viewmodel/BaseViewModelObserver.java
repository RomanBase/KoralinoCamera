package com.ankhrom.base.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.ankhrom.base.BaseActivity;
import com.ankhrom.base.GlobalCode;
import com.ankhrom.base.common.statics.ArgsHelper;
import com.ankhrom.base.common.statics.FragmentHelper;
import com.ankhrom.base.custom.builder.FragmentTransactioner;
import com.ankhrom.base.interfaces.ActivityEventListener;
import com.ankhrom.base.interfaces.ObjectFactory;
import com.ankhrom.base.interfaces.OnActivityStateChangedListener;
import com.ankhrom.base.interfaces.PopupModelAdapter;
import com.ankhrom.base.interfaces.viewmodel.AnimableTransitionViewModel;
import com.ankhrom.base.interfaces.viewmodel.AnimableViewModel;
import com.ankhrom.base.interfaces.viewmodel.MenuViewModel;
import com.ankhrom.base.interfaces.viewmodel.OnBaseChangedListener;
import com.ankhrom.base.interfaces.viewmodel.ViewModel;
import com.ankhrom.base.interfaces.viewmodel.ViewModelNavigation;
import com.ankhrom.base.interfaces.viewmodel.ViewModelObserver;

public class BaseViewModelObserver implements ViewModelObserver, ViewModelNavigation, ActivityEventListener {

    private ViewModel currentViewModel;
    private MenuViewModel menuViewModel;
    private ObjectFactory objectFactory;
    private PopupModelAdapter popupAdapter;
    private RequestQueue requestQueue;

    private Context context;
    private ViewModel defaultViewModel;

    private int rootContainerId;
    private int screenRootContainerId;

    private OnBaseChangedListener baseChangeListener;

    private BaseViewModelObserver() {

    }

    public static Builder with(ObjectFactory factory, @IdRes int rootContainerId) {

        return new Builder(factory, rootContainerId);
    }

    public void onInitialized() {

        postArgsToViewModel(GlobalCode.INITIALIZED);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setDefaultViewModel(@NonNull ViewModel viewModel) {

        defaultViewModel = viewModel;
        currentViewModel = viewModel;

        currentViewModel.setNavigation(this);
        currentViewModel.onInit();
        currentViewModel.onViewStackChanged(false, true);
        currentViewModel.loadModel();

        FragmentTransactioner.with(context)
                .replaceView(currentViewModel.getFragment())
                .into(rootContainerId)
                .clearStack()
                .commit();
    }

    public void setDefaultMenuModel(MenuViewModel menuModel) {

        if (menuModel != null) {
            menuModel.setNavigation(this);
            menuModel.onInit();
            menuModel.onCreate();
        }

        menuViewModel = menuModel;
    }

    public void setFactory(ObjectFactory factory) {
        this.objectFactory = factory;
    }

    public void setPopupAdapter(PopupModelAdapter popupAdapter) {
        this.popupAdapter = popupAdapter;
    }

    public void setRootViewContainer(ViewGroup rootView) {

        if (popupAdapter != null) {
            popupAdapter.init(context, (ViewGroup) rootView.findViewById(screenRootContainerId));
        }
    }

    public void setRootContainerId(@IdRes int viewId) {
        this.rootContainerId = viewId;
    }

    public void setScreenRootContainerId(@IdRes int viewId) {
        this.screenRootContainerId = viewId;
    }

    public void setOnBaseChangedListener(OnBaseChangedListener modelChangeListener) {
        this.baseChangeListener = modelChangeListener;
    }

    public void setRequestQueue(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    private void initViewModel(ViewModel viewModel, boolean visibleStack) {

        if (currentViewModel != null) {
            currentViewModel.onViewStackChanged(true, visibleStack);
        }

        currentViewModel = viewModel;
        initViewModel(currentViewModel);

        notifyViewModelChanged();
    }

    private void initViewModel(ViewModel viewModel) {

        viewModel.setNavigation(this);
        viewModel.onInit();
        viewModel.loadModel();
    }

    private void commitViewModel(FragmentTransactioner.FragmentCommitAnim fragment, ViewModel viewModel, boolean clearBackStack) {

        if (viewModel instanceof AnimableViewModel) {
            fragment.enterAndExit((AnimableViewModel) viewModel)
                    .clearStack(clearBackStack)
                    .commit();
        } else if (viewModel instanceof AnimableTransitionViewModel) {
            fragment.transition(((AnimableTransitionViewModel) viewModel).getTransition())
                    .clearStack(clearBackStack)
                    .commit();
        } else if (clearBackStack) {
            fragment.clearStack().commit();
        } else {
            fragment.commit();
        }
    }

    @Override
    public void setViewModel(@NonNull ViewModel viewModel, boolean clearBackStack) {

        initViewModel(viewModel, false);

        FragmentTransactioner.FragmentCommitAnim fragment = FragmentTransactioner.with(context)
                .replacePage(currentViewModel.getFragment())
                .into(rootContainerId);

        commitViewModel(fragment, viewModel, clearBackStack);
    }

    @Override
    public void addViewModel(@NonNull ViewModel viewModel, boolean asRootContent) {

        initViewModel(viewModel, true);

        FragmentTransactioner.FragmentCommitAnim fragment = FragmentTransactioner.with(context)
                .addPage(currentViewModel.getFragment())
                .into(asRootContent ? rootContainerId : screenRootContainerId);

        commitViewModel(fragment, viewModel, false);
    }

    @Override
    public void replaceViewModel(@NonNull ViewModel viewModel, @IdRes int containerId) {

        initViewModel(viewModel);

        FragmentTransactioner.FragmentCommitAnim fragment = FragmentTransactioner.with(context)
                .replaceView(viewModel.getFragment())
                .into(containerId);

        commitViewModel(fragment, viewModel, false);
    }

    @Override
    public boolean onBaseBackPressed() {

        if (popupAdapter != null && popupAdapter.isActive()) {
            popupAdapter.hide(popupAdapter.getCurrentPopupModel());
            return true;
        }

        if (currentViewModel != null) {
            if (currentViewModel.onBackPressed()) {
                return true;
            }

            if (defaultViewModel.getClass().isInstance(currentViewModel)) {
                return false;
            }
        }

        if (!defaultViewModel.getClass().isInstance(currentViewModel)) {
            setPreviousViewModel();
        }

        return false;
    }

    @Override
    public void setPreviousViewModel() {

        int count = FragmentHelper.getBackStackLength(context);

        if (count <= 1) {
            currentViewModel = defaultViewModel;
            notifyMenuState(defaultViewModel);
        } else {
            Fragment fragment = FragmentHelper.getPreviousStackFragment(context);
            if (fragment != null && fragment instanceof ViewModel) {
                currentViewModel = (ViewModel) fragment;
            } else {
                return;
            }
        }

        currentViewModel.onViewStackChanged(false, true);

        if (!currentViewModel.isModelLoaded()) {
            currentViewModel.loadModel();
        }

        notifyViewModelChanged();
    }

    public void notifyMenuState(Object state) {

        if (menuViewModel != null) {
            menuViewModel.setMenuState(state);
        }
    }

    @Override
    public void notifyViewModelChanged() {

        if (baseChangeListener != null) {
            baseChangeListener.onViewModelChanged(currentViewModel);
        }
    }

    @Override
    public void navigateBack() {

        ((Activity) context).onBackPressed();
    }

    @Override
    public void onResume() {

        if (currentViewModel != null && currentViewModel instanceof OnActivityStateChangedListener) {
            ((OnActivityStateChangedListener) currentViewModel).onActivityResume();
        }
    }

    @Override
    public void onPause() {

        if (currentViewModel != null && currentViewModel instanceof OnActivityStateChangedListener) {
            ((OnActivityStateChangedListener) currentViewModel).onActivityPause();
        }
    }

    @Override
    public boolean onBaseActivityResult(int requestCode, int resultCode, Intent data) {

        return currentViewModel != null && currentViewModel.onBaseActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void postArgsToViewModel(int requestCode, Object... args) {

        if (currentViewModel != null) {
            currentViewModel.onReceiveArgs(requestCode, args);
        }
    }

    @Override
    public void postArgsToBase(int requestCode, Object... args) {

        if (baseChangeListener != null) {
            baseChangeListener.onReceiveArgs(requestCode, args);
        }
    }

    @Override
    public void notifyScreenOptions(int options) {

        if (baseChangeListener != null) {
            baseChangeListener.onScreenOptionsChanged(options);
        }
    }

    @Override
    public Context getContext() {
        return context;
    }

    public BaseActivity getBaseActivity() {

        if (context instanceof BaseActivity) {
            return (BaseActivity) context;
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends MenuViewModel> T getMenuViewModel() {
        return (T) menuViewModel;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ObjectFactory> T getFactory() {
        return (T) objectFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends PopupModelAdapter> T getPopupAdapter() {
        return (T) popupAdapter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModelObserver> T getObserver() {
        return (T) this;
    }

    @Override
    public RequestQueue getRequestQueue() {

        return requestQueue;
    }

    @Override
    public ViewModelNavigation getNavigation() {
        return this;
    }

    @Override
    public ActivityEventListener getEventListener() {
        return this;
    }

    @Override
    public ViewModel getCurrentViewModel() {
        return currentViewModel;
    }

    public ViewModel getDefaultViewModel() {
        return defaultViewModel;
    }

    public int getRootContainerId() {
        return rootContainerId;
    }

    public OnBaseChangedListener getBaseChangeListener() {
        return baseChangeListener;
    }

    public static class Builder {

        private final Context bContext;
        private final int bRootContainerId;
        private int bScreenRootContainerId;
        private ViewModel bViewModel;
        private MenuViewModel bMenuModel;
        private ObjectFactory bFactory;
        private PopupModelAdapter bPopupAdapter;
        private RequestQueue bRequestQueue;

        Builder(@NonNull ObjectFactory factory, @IdRes int rootContainerId) {

            this.bFactory = factory;
            this.bContext = factory.getContext();
            this.bRootContainerId = rootContainerId;
        }

        public Builder setScreenRootContainerId(@IdRes int screenRootContainerId) {

            this.bScreenRootContainerId = screenRootContainerId;
            return this;
        }

        public Builder setViewModel(ViewModel viewModel) {

            this.bViewModel = viewModel;
            return this;
        }

        public <T extends ViewModel> Builder setViewModel(Class<T> clazz, Object... args) {

            this.bViewModel = bFactory.getViewModel(clazz, args);
            return this;
        }

        public Builder setMenuModel(MenuViewModel menuModel) {

            this.bMenuModel = menuModel;
            return this;
        }

        public <T extends MenuViewModel> Builder setMenuModel(Class<T> clazz, Object... args) {

            if (args == null) {
                args = new Object[]{bContext};
            } else {
                args = ArgsHelper.addArg(args, bContext);
            }

            this.bMenuModel = bFactory.construct(clazz, args);
            return this;
        }

        public Builder setPopupAdapter(PopupModelAdapter popupAdapter) {

            this.bPopupAdapter = popupAdapter;
            return this;
        }

        public <T extends PopupModelAdapter> Builder setPopupAdapter(Class<T> clazz, Object... args) {

            this.bPopupAdapter = bFactory.construct(clazz, args);
            return this;
        }

        public Builder setRequestQueue(RequestQueue requestQueue) {

            this.bRequestQueue = requestQueue;
            return this;
        }

        public ViewModelObserver build() {

            if (bRequestQueue == null) {
                bRequestQueue = bFactory.getRequestQueue();
            }

            if (bFactory.get(RequestQueue.class) == null) {
                bFactory.add(bRequestQueue);
            }

            if (bPopupAdapter == null) {
                bPopupAdapter = new BasePopupAdapter();
            }

            BaseViewModelObserver mvm = new BaseViewModelObserver();
            mvm.setContext(bContext);
            mvm.setRootContainerId(bRootContainerId);
            mvm.setScreenRootContainerId(bScreenRootContainerId > 0 ? bScreenRootContainerId : bRootContainerId);
            mvm.setFactory(bFactory);
            mvm.setRequestQueue(bRequestQueue);
            mvm.setPopupAdapter(bPopupAdapter);
            mvm.setDefaultMenuModel(bMenuModel);
            mvm.setDefaultViewModel(bViewModel);

            return mvm;
        }
    }
}
