package com.ankhrom.base.common.statics;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ankhrom.base.Base;
import com.ankhrom.base.custom.builder.ToastBuilder;

import java.io.File;

public final class AppsHelper {

    public static void startActivity(Context context, Class cls) {

        context.startActivity(new Intent(context, cls));
    }

    public static void startActivity(Activity activity, Class cls, int requestCode) {

        activity.startActivityForResult(new Intent(activity, cls), requestCode);
    }

    public static void startApp(Context context, String packageName) {

        try {
            context.startActivity(context.getPackageManager().getLaunchIntentForPackage(packageName));
        } catch (ActivityNotFoundException e) {
            ToastBuilder.with(context).text("Application " + packageName + " not found !").build();
            e.printStackTrace();
        }
    }

    public static void startInstall(Context context, Uri apk) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apk, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startInstallCommand(File file) { //requires root access
        if (file.exists()) {
            try {
                String command = "adb install -r " + file.getAbsolutePath(); //?pm install
                Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
                proc.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * starts Google play app store with specific app opened
     *
     * @param app_package package name of choosen app
     */
    public static void showInGooglePlay(Context context, String app_package) {

        String url;

        try { //Check whether Google Play store is installed or not:
            context.getPackageManager().getPackageInfo("com.android.vending", 0);
            url = "market://details?id=" + app_package;
        } catch (Exception e) {
            Base.logE("Monkeys can't find Google Play app store.");
            url = "https://play.google.com/store/apps/details?id=" + app_package;
        }

        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(intent);
    }

    public static void openMail(Context context, String to, String subject, String body) {

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri mail = Uri.parse(StringHelper.format("mailto:?to=%s&subject=%s&body=%s",
                    StringHelper.isEmpty(to) ? StringHelper.EMPTY : to,
                    StringHelper.isEmpty(subject) ? StringHelper.EMPTY : subject,
                    StringHelper.isEmpty(body) ? StringHelper.EMPTY : body
            ));
            intent.setData(mail);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openDial(Context context, String number) {

        try {
            String uri = "tel:" + Uri.encode(number.trim());
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(uri));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openWeb(Context context, String url) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    public static void openIntent(Context context, String action) {

        context.startActivity(new Intent(action));
    }

    public static void openIntent(Context context, Uri uri) {

        context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

}
