package com.ankhrom.koralino.camera;

import java.util.Calendar;

/**
 * Created by R' on 1/15/2018.
 */

public class Prefs {

    public static final String GALERY_APP = "com.google.android.apps.photos";

    public static final String FOLDER_NAME = "Koralino";

    private static final String SEPARATOR = "_";
    private static final String SEMI_COLOM = "-";

    public static final String getFilePath(int version) {

        Calendar calendar = Calendar.getInstance();

        StringBuilder builder = new StringBuilder();
        builder.append(FOLDER_NAME)
                .append("/")
                .append("krln")
                .append(SEPARATOR)
                .append(calendar.get(Calendar.YEAR))
                .append(SEMI_COLOM)
                .append(calendar.get(Calendar.MONTH) + 1)
                .append(SEMI_COLOM)
                .append(calendar.get(Calendar.DAY_OF_MONTH))
                .append(SEPARATOR)
                .append(calendar.get(Calendar.HOUR_OF_DAY))
                .append(SEMI_COLOM)
                .append(calendar.get(Calendar.MINUTE))
                .append(SEMI_COLOM)
                .append(calendar.get(Calendar.SECOND))
                .append(SEPARATOR)
                .append("v")
                .append(version)
                .append(".png");

        return builder.toString();
    }
}
