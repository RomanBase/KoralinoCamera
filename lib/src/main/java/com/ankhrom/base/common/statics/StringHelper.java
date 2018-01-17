package com.ankhrom.base.common.statics;

import android.support.annotation.Nullable;

import java.util.Locale;

public final class StringHelper {

    public static final String EMPTY_PLACEHOLDER = "-";
    public static final String EMPTY = "";

    public static String format(String format, Object... args) {

        return String.format(Locale.US, format, args);
    }

    public static String build(Object... args) {

        if (args == null) {
            return EMPTY_PLACEHOLDER;
        }

        StringBuilder builder = new StringBuilder();
        for (Object arg : args) {
            if (arg != null) {
                builder.append(arg.toString()).append(" ");
            }
        }

        return builder.toString();
    }

    public static String buildStruct(@Nullable String separator, Object... args) {

        if (args == null) {
            return EMPTY_PLACEHOLDER;
        }

        if (separator == null || args.length < 2) {
            return build(args);
        }

        int subCount = args.length - 1;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < subCount; i++) {
            if (args[i] != null) {
                builder.append(args[i]).append(separator);
            }
        }

        if (args[subCount] != null) {
            builder.append(args[subCount]);
        } else {
            if (builder.length() > separator.length()) {
                builder.delete(builder.length() - separator.length(), builder.length());
            }
        }

        return builder.toString();
    }

    public static boolean isEmpty(@Nullable String o) {

        return o == null || o.isEmpty();
    }

    public static boolean isEmpty(@Nullable Object o) {

        return o == null || isEmpty(o.toString());
    }

    public static int toInteger(@Nullable String o) {

        if (isEmpty(o)) {
            return 0;
        }

        return Integer.valueOf(o);
    }

    public static double toDouble(@Nullable String o) {

        if (isEmpty(o)) {
            return 0;
        }

        return Double.valueOf(o);
    }

    public static float toFloat(@Nullable String o) {

        if (isEmpty(o)) {
            return 0;
        }

        return Float.valueOf(o);
    }

    public static long toLong(@Nullable String o) {

        if (isEmpty(o)) {
            return 0;
        }

        return Long.valueOf(o);
    }

    public static String toString(@Nullable Object o) {

        return o == null ? EMPTY : o.toString();
    }
}
