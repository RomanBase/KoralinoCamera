package com.ankhrom.base.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ankhrom.base.common.statics.StringHelper;
import com.ankhrom.base.interfaces.viewmodel.ViewModel;
import com.ankhrom.base.model.Model;

import java.util.ArrayList;
import java.util.List;

import libs.view.PagerSlidingTabStrip;

public abstract class CompositeViewModel<S extends ViewDataBinding, T extends Model> extends BaseViewModel<S, T> implements ViewPager.OnPageChangeListener {

    private ViewPager pager;
    private PagerSlidingTabStrip tabs;

    private boolean restoreDataset = false;

    protected List<BaseViewModel> viewModels;

    public final ObservableBoolean atLastPage = new ObservableBoolean();

    public CompositeViewModel() {

    }

    protected abstract List<BaseViewModel> initViews();

    protected abstract int getViewPagerId();

    protected abstract int getViewTabsId();

    protected ViewPager.PageTransformer getPageTransformer() {

        return new DepthPageRollTransformer();
    }

    public void setCurrentPage(int index) {
        setCurrentPage(index, false);
    }

    public void setCurrentPageRoll(int index) {
        setCurrentPage(index, true);
    }

    public void setCurrentPage(int index, boolean animate) {

        if (getPageCount() == 0) {
            return;
        }

        pager.setCurrentItem(index, animate);
    }

    public int getCurrentPageIndex() {
        return pager.getCurrentItem();
    }

    public int getPageCount() {

        if (pager == null || pager.getAdapter() == null) {
            return 0;
        }

        return pager.getAdapter().getCount();
    }

    public BaseViewModel getCurrentViewModel() {
        return viewModels.get(getCurrentPageIndex());
    }

    public BaseViewModel getNextViewModel() {

        return viewModels.get(getNextPageIndex());
    }

    public BaseViewModel getPreviousViewModel() {

        return viewModels.get(getPreviousPageIndex());
    }

    public BaseViewModel getViewModel(int index) {
        return viewModels.get(index);
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        if (tabs != null) {
            tabs.setOnPageChangeListener(listener);
        } else {
            pager.setOnPageChangeListener(listener);
        }
    }

    public ViewPager getPager() {
        return pager;
    }

    public PagerSlidingTabStrip getTabStrip() {
        return tabs;
    }

    public int getNextPageIndex() {

        int index = getCurrentPageIndex() + 1;
        if (index >= getPageCount()) {
            index = 0;
        }

        return index;
    }

    public int getPreviousPageIndex() {

        int index = getCurrentPageIndex() - 1;
        if (index < 0) {
            index = getPageCount() - 1;
        }

        return index;
    }

    public void add(BaseViewModel viewModel) {

        viewModels.add(viewModel);
    }

    public void add(int index, BaseViewModel viewModel) {

        viewModels.add(index, viewModel);
    }

    public void remove(BaseViewModel viewModel) {

        viewModels.remove(viewModel);
    }

    public void notifyDatasetChanged() {

        pager.removeAllViews();
        pager.setAdapter(null);

        if (tabs != null) {
            tabs.removeAllViews();
        }

        pager.setAdapter(new ViewModelFragmentAdapter(getChildFragmentManager()));

        if (tabs != null) {
            tabs.setViewPager(pager);
        }
    }

    @Override
    @CallSuper
    protected void onModelCreated() {

        if (viewModels == null) {
            viewModels = initViews();
            if (viewModels == null) {
                viewModels = new ArrayList<>();
            } else {
                for (BaseViewModel vm : viewModels) {
                    vm.setNavigation(getNavigation());
                    if (!vm.isModelLoaded() && !vm.isLoading.get()) {
                        vm.onInit();
                        vm.loadModel();
                    }
                }
            }
        }

        pager.setPageTransformer(true, getPageTransformer());
        pager.setAdapter(new ViewModelFragmentAdapter(getChildFragmentManager()));

        if (tabs != null) {
            tabs.setViewPager(pager);
        }

        pager.clearOnPageChangeListeners();
        pager.addOnPageChangeListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (view != null) {

            pager = (ViewPager) view.findViewById(getViewPagerId());

            if (getViewTabsId() > 0) {
                tabs = (PagerSlidingTabStrip) view.findViewById(getViewTabsId());
            }

            return view;
        } else {
            return null;
        }
    }

    @Override
    public void onViewStackChanged(boolean isBackStacked, boolean isVisible) {
        super.onViewStackChanged(isBackStacked, isVisible);

        if (viewModels == null) {
            return;
        }

        for (ViewModel viewModel : viewModels) {
            viewModel.onViewStackChanged(isBackStacked, isVisible);
        }

        if (isBackStacked && !isVisible) {
            restoreDataset = true;
            pager.removeAllViews();
            pager.setAdapter(null);
        } else {
            if (restoreDataset) {
                restoreDataset = false;
                notifyDatasetChanged();
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        atLastPage.set(position == viewModels.size() - 1);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class ViewModelFragmentAdapter extends FragmentPagerAdapter {

        public ViewModelFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return viewModels != null ? viewModels.size() : 0;
        }

        @Override
        public Fragment getItem(int position) {
            return viewModels.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            String title = viewModels.get(position).getTitle();

            return StringHelper.isEmpty(title) ? StringHelper.EMPTY : title;
        }
    }

    public class FadePageTransformer implements ViewPager.PageTransformer {
        @Override
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position <= -1.0f) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);
                view.setTranslationX(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1.0f + position);
                view.setTranslationX(-(float) pageWidth * position);
            } else if (position < 1.0f) { // (0,1]
                // Fade the page out.
                view.setAlpha(1.0f - position);

                // Counteract the default slide transition
                view.setTranslationX(-(float) pageWidth * position);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
                view.setTranslationX(0);
            }
        }
    }

    public class DepthPageRollTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            //int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(0);

                // Scale the page scaleDown (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    public class RollPageTransformer implements ViewPager.PageTransformer {

        public void transformPage(View view, float position) {

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);
            } else if (position <= 1) { // (-1,1]
                view.setTranslationX(0);
                view.setAlpha(1);
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

}
