package com.ankhrom.base.custom.builder;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ankhrom.base.interfaces.FragmentAnimationsHolder;

public class FragmentTransactioner {

    public static FragmentPage with(Context context) {

        return new FragmentPage(context);
    }

    public static class FragmentPage {

        final Context context;

        Fragment fragment;
        boolean intoStack;
        boolean replace;

        FragmentPage(Context context) {
            this.context = context;
        }

        public FragmentRoot addView(Fragment fragment) {

            intoStack = false;
            replace = false;

            this.fragment = fragment;
            return new FragmentRoot(this);
        }

        public FragmentRoot replaceView(Fragment fragment) {

            intoStack = false;
            replace = true;

            this.fragment = fragment;
            return new FragmentRoot(this);
        }

        public FragmentRoot addPage(Fragment fragment) {

            intoStack = true;
            replace = false;

            this.fragment = fragment;
            return new FragmentRoot(this);
        }

        public FragmentRoot replacePage(Fragment fragment) {

            intoStack = true;
            replace = true;

            this.fragment = fragment;
            return new FragmentRoot(this);
        }
    }

    public static class FragmentRoot {

        final FragmentPage page;
        int view;

        FragmentRoot(FragmentPage page) {
            this.page = page;
        }

        public FragmentCommitAnim into(int view) {

            this.view = view;
            return new FragmentCommitAnim(this);
        }
    }

    public static class FragmentCommitAnim {

        final FragmentRoot root;

        FragmentAnimationsHolder holder;
        int transition;

        FragmentCommitAnim(FragmentRoot root) {
            this.root = root;
        }

        public FragmentCommit enterAndExit(FragmentAnimationsHolder anim) {

            this.transition = FragmentTransaction.TRANSIT_UNSET;
            this.holder = anim;

            return new FragmentCommit(this);
        }

        public FragmentCommit transition(int id) {

            this.transition = id;
            return new FragmentCommit(this);
        }

        public FragmentCommit clearStack() {

            this.transition = FragmentTransaction.TRANSIT_FRAGMENT_OPEN;
            FragmentCommit commiter = new FragmentCommit(this);
            commiter.clearStack();

            return commiter;
        }

        public void commit() {

            this.transition = FragmentTransaction.TRANSIT_FRAGMENT_OPEN;
            new FragmentCommit(this).commit();
        }
    }

    public static class FragmentCommit {

        final FragmentCommitAnim anim;
        boolean clearStack;

        FragmentCommit(FragmentCommitAnim anim) {
            this.anim = anim;
        }

        public FragmentCommit clearStack() {

            clearStack = true;
            return this;
        }

        public FragmentCommit clearStack(boolean clearStack) {

            this.clearStack = clearStack;
            return this;
        }

        public void commit() {

            FragmentRoot root = anim.root;
            FragmentPage page = root.page;
            FragmentManager manager = ((FragmentActivity) page.context).getSupportFragmentManager();

            String backStackIdentifier = page.fragment.getClass().getName();

            if (clearStack) {
                manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

            FragmentTransaction trans = manager.beginTransaction();

            if (anim.transition != FragmentTransaction.TRANSIT_UNSET) {
                trans.setTransition(anim.transition);
            } else if (anim.holder != null) {
                trans.setCustomAnimations(
                        anim.holder.getEnterAnimation(),
                        anim.holder.getExitAnimation(),
                        anim.holder.getRestoreEnterAnimation(),
                        anim.holder.getRestoreExitAnimation()
                );
            }

            if (page.replace) {
                trans.replace(root.view, page.fragment, backStackIdentifier);
            } else {
                trans.add(root.view, page.fragment, backStackIdentifier);
            }

            if (page.intoStack) {
                trans.addToBackStack(backStackIdentifier);
            }

            trans.commit();
            manager.executePendingTransactions();
        }
    }
}
