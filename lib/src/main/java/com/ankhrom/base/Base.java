package com.ankhrom.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

public class Base {

    private static final String NULL = "NULL";
    private static final String LOG_SEPARATOR = " ";

    public static final String TAG = "Base";
    public static boolean debug = false;

    /**
     * check internet connection
     *
     * @return true if any connection is available
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean runOnUiThread(Runnable action, long millisecDelay) {

        return new Handler(Looper.getMainLooper()).postAtTime(action, SystemClock.uptimeMillis() + millisecDelay);
    }

    public static void log(Object... args) {

        if (args == null || !debug) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (Object o : args) {
            if (o != null) {
                builder.append(o.toString());
            } else {
                builder.append(NULL);
            }
            builder.append(LOG_SEPARATOR);
        }

        Log.i(TAG, builder.toString());
    }

    public static void logI(Object o) {

        if (debug) {
            if (o == null) {
                Log.i(TAG, NULL);
                return;
            }
            Log.i(TAG, o.toString());
        }
    }

    public static void logV(Object o) {

        if (debug) {
            if (o == null) {
                Log.v(TAG, NULL);
                return;
            }
            Log.v(TAG, o.toString());
        }
    }

    public static void logD(Object o) {

        if (debug) {
            if (o == null) {
                Log.d(TAG, NULL);
                return;
            }
            Log.d(TAG, o.toString());
        }
    }

    public static void logE(Object o) {

        if (debug) {
            if (o == null) {
                Log.e(TAG, NULL);
                return;
            }
            Log.e(TAG, o.toString());
        }
    }

    public static void logI(String TAG, Object o) {

        if (debug) {
            if (o == null) {
                Log.i(TAG, NULL);
                return;
            }
            Log.i(TAG, o.toString());
        }
    }

    public static void logV(String TAG, Object o) {

        if (debug) {
            if (o == null) {
                Log.v(TAG, NULL);
                return;
            }
            Log.v(TAG, o.toString());
        }
    }

    public static void logD(String TAG, Object o) {

        if (debug) {
            if (o == null) {
                Log.d(TAG, NULL);
                return;
            }
            Log.d(TAG, o.toString());
        }
    }

    public static void logE(String TAG, Object o) {

        if (debug) {
            if (o == null) {
                Log.e(TAG, NULL);
                return;
            }
            Log.e(TAG, o.toString());
        }
    }
}
