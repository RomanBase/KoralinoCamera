package com.ankhrom.base.common.statics;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.util.List;

public final class FragmentHelper {

    public static void removePage(Context context, Fragment fragment) {

        removePage(context, fragment, true);
    }

    public static void removePage(Context context, Fragment fragment, boolean popBackStack) {

        FragmentManager manager = getSupportManager(context);

        manager.beginTransaction()
                .remove(fragment)
                .commit();

        manager.executePendingTransactions();

        if (popBackStack) {
            popBackStack(context);
        }
    }

    public static void clearBackStack(Context context) {

        getSupportManager(context).popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public static void popBackStack(Context context) {

        getSupportManager(context).popBackStack();
    }

    public static int getBackStackLength(Context context) {

        return getSupportManager(context).getBackStackEntryCount();
    }

    public static List<Fragment> getFragments(Context context) {

        return getSupportManager(context).getFragments();
    }

    public static Fragment getBackStackFragment(Context context, int index) {

        FragmentManager manager = getSupportManager(context);

        return manager.findFragmentByTag(manager.getBackStackEntryAt(index).getName());
    }

    public static Fragment getPreviousStackFragment(Context context) {

        return getBackStackFragment(context, getBackStackLength(context) - 2);
    }

    public static Fragment getCurrentFragment(Context context) {

        return getBackStackFragment(context, getBackStackLength(context) - 1);
    }

    public static FragmentManager getSupportManager(Context context) {

        return ((FragmentActivity) context).getSupportFragmentManager();
    }

    public static android.app.FragmentManager getManager(Context context) {

        return ((FragmentActivity) context).getFragmentManager();
    }
}
