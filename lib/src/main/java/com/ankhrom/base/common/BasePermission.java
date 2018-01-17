package com.ankhrom.base.common;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import com.ankhrom.base.Base;
import com.ankhrom.base.GlobalCode;

public class BasePermission {

    public static BasePermission.Builder with(Context context) {

        return new BasePermission.Builder(context);
    }

    /**
     * @param permission required permission
     * @return true if permission is granted
     */
    public static boolean isAvailable(Context context, String permission) {

        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * @param permission required permission
     * @return true if additional explaination is required
     */
    public static boolean isDialogRequired(Context context, String permission) {

        return ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission);
    }

    /**
     * request permmisions (granted or forbiden permissions are exluded from dialog)
     *
     * @param requestCode 8-bit int to match with a result reported to onRequestPermissionsResult callback.
     * @param permissions array of required permissions
     */
    public static void requestDialog(Context context, int requestCode, String... permissions) {

        if (permissions == null) {
            return;
        }

        ActivityCompat.requestPermissions((Activity) context, permissions, requestCode);
    }

    /**
     * @param permissions array of permissions to check
     * @return array of required permissions
     */
    public static String[] checkPermissions(Context context, String... permissions) {

        if (permissions == null || Build.VERSION.SDK_INT < 23) {
            return null;
        }

        List<String> required = new ArrayList<>();

        for (String permission : permissions) {
            if (!isAvailable(context, permission)) {
                required.add(permission);
                Base.logE(permission + " is not granted");
            } else {
                Base.logD(permission + " is granted");
            }
        }

        if (required.size() == 0) {
            return null;
        } else {
            String[] out = new String[required.size()];
            out = required.toArray(out);

            return out;
        }
    }

    public static class Builder {

        private final Context context;
        private int requestCode = GlobalCode.PERMMISSION_REQUEST;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder requestCode(int requestCode) {

            this.requestCode = requestCode;
            return this;
        }

        public void require(String... permissions) {

            requestDialog(context, requestCode, checkPermissions(context, permissions));
        }
    }
}
