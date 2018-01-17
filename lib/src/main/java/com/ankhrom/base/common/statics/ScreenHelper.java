package com.ankhrom.base.common.statics;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.ankhrom.base.Base;

public final class ScreenHelper {

    public static float getDp(Context context, float px) {

        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float getPx(Context context, float dp) {

        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static boolean hasSoftKeys() {

        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

        return !hasBackKey && !hasHomeKey;
    }

    public static int getNavigationBarHeight(Context context) {

        if (hasSoftKeys()) {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }

        return 0;
    }

    public static boolean isSoftKeyboardVisible(Context context, View view) {

        if (view == null) {
            return false;
        }

        final Rect r = new Rect();
        view.getWindowVisibleDisplayFrame(r);

        final int heightDiff = view.getRootView().getHeight() - (r.bottom - r.top);

        return heightDiff > getPx(context, 100);
    }

    public static void hideSoftKeyboard(Activity activity) {

        View view = activity.getCurrentFocus();
        if (view != null) {
            hideSoftKeyboard(activity, view.getWindowToken());
        }
    }

    public static void hideSoftKeyboard(Context context, IBinder windowToken) {

        if (windowToken != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(windowToken, 0);
        }
    }

    public static boolean isDeviceDecorated(Context context) {

        float[] deco = getDimensions(context, true);
        float[] undeco = getDimensions(context, false);

        return !(deco[0] == undeco[0] && deco[1] == undeco[1]);
    }

    /**
     * @return screen dimensions - [0]width, [1]height, [2]density
     */
    public static float[] getDimensions(Context context, boolean includeDecorations) {

        float[] dim = new float[3];
        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display dis = wm.getDefaultDisplay();
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        dis.getMetrics(displayMetrics);

        if (includeDecorations) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(dis, realSize);
                dim[0] = realSize.x;
                dim[1] = realSize.y;
            } catch (Exception ex) {
                Base.logE("Monkeys can't measure real display size.");
            }
        } else {
            Point size = new Point();
            dis.getSize(size);
            dim[0] = size.x;
            dim[1] = size.y;
        }

        dim[2] = displayMetrics.density;

        return dim;
    }
}
