package com.ankhrom.base.common.statics;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.Locale;

/**
 * @see <a href="https://developers.google.com/maps/documentation/android/intents>https://developers.google.com/maps/documentation/android/intents</a>
 */
public final class MapsHelper {

    public static void showAddress(Context context, String address) {

        if (StringHelper.isEmpty(address)) {
            return;
        }

        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Locale.US, "geo:0,0?q=%s", Uri.encode(address)))));
        } catch (ActivityNotFoundException ex) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Locale.US, "http://google.com/maps/?q=%s", Uri.encode(address)))));
        }
    }

    public static void showGeo(Context context, float N, float W, String address) {

        try {
            String geo;
            if (StringHelper.isEmpty(address)) {
                geo = String.format(Locale.US, "geo:%f,%f", N, W);
            } else {
                geo = String.format(Locale.US, "geo:%f,%f?q=%s", N, W, Uri.encode(address));
            }

            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(geo)));
        } catch (ActivityNotFoundException ex) {
            String geo;
            if (StringHelper.isEmpty(address)) {
                geo = String.format(Locale.US, "http://google.com/maps/?q=%f,%f", N, W);
            } else {
                geo = String.format(Locale.US, "http://google.com/maps/?q=%f,%f(%s)", N, W, Uri.encode(address));
            }

            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(geo)));
        }
    }
}
