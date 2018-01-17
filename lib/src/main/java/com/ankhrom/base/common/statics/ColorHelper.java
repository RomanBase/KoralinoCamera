package com.ankhrom.base.common.statics;

import android.graphics.Color;

public final class ColorHelper {

    public static int interpolate(int a, int b, float t) {

        int alfa = (int) MathHelper.interpolate(Color.alpha(a), Color.alpha(b), t);
        int red = (int) MathHelper.interpolate(Color.red(a), Color.red(b), t);
        int green = (int) MathHelper.interpolate(Color.green(a), Color.green(b), t);
        int blue = (int) MathHelper.interpolate(Color.blue(a), Color.blue(b), t);

        return Color.argb(alfa, red, green, blue);
    }
}
