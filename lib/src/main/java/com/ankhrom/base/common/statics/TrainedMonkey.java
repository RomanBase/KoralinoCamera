package com.ankhrom.base.common.statics;

import android.os.SystemClock;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 */
public final class TrainedMonkey {

    public static <T> T[] arrayUp(T[] src, T add) {

        if (src == null) {
            throw new RuntimeException("Monkeys can't copy data from null source array");
        }

        @SuppressWarnings("unchecked")
        T[] temp = (T[]) src.getClass().cast(Array.newInstance(src.getClass().getComponentType(), src.length + 1));

        System.arraycopy(src, 0, temp, 0, src.length);
        temp[src.length] = add;

        return temp;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] arrayClear(T[] array, T remove) {

        List<T> list = Arrays.asList(array);
        list.removeAll(Collections.singleton(remove));

        return (T[]) list.toArray();
    }

    public static String readableSizeFormat(long sizeInBytes) {

        if (sizeInBytes <= 0) return "0";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(sizeInBytes) / Math.log10(1024));

        return new DecimalFormat("#,##0.###").format(sizeInBytes / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static <T> T[] shuffle(T[] array) {

        Random r = new Random(SystemClock.uptimeMillis());
        int count = array.length;

        for (int i = 0; i < count; i++) {

            int index = r.nextInt(count);

            T temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }

        return array;
    }

}
