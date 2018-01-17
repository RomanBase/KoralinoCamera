package com.ankhrom.base.interfaces.viewmodel;

import android.content.Intent;
import android.support.v4.app.Fragment;

public interface ViewModel {

    /**
     * initial set up by view model observer
     */
    void onInit();

    /**
     * loads model structure
     */
    void loadModel();

    /**
     * @return true if model is loaded
     */
    boolean isModelLoaded();

    /**
     * set to true if model is in back stack or false if model is on top of stack
     */
    void onViewStackChanged(boolean isInBackStack, boolean isVisible);

    /**
     * @return true to consume this event
     */
    boolean onBackPressed();

    /**
     * @return true to consume this event
     */
    boolean onBaseActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * receive custom args from ViewModelObserver
     */
    void onReceiveArgs(int requestCode, Object[] args);

    /**
     * @return title of this ViewModel
     */
    String getTitle();

    /**
     * sets default navigation
     */
    void setNavigation(ViewModelNavigation navigation);

    /**
     * @return android.support.v4.app.Fragment of ViewModel
     */
    Fragment getFragment();
}
