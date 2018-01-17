package com.ankhrom.base.common.statics;

public final class MathHelper {

    public static float interpolate(float a, float b, float t) {

        return a + (b - a) * t;
    }
}
