package com.ankhrom.base.common.statics;

import com.ankhrom.base.interfaces.ObjectConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class ObjectHelper {

    public static boolean equals(Object o, Object t) {

        return (o == null) ? (t == null) : o.equals(t);
    }

    public static <T, E> List<T> convert(List<E> items, ObjectConverter<T, E> convertor) {

        List<T> list = new ArrayList<>(items.size());

        for (E item : items) {
            list.add(convertor.convert(item));
        }

        return list;
    }

    public static <T, E> List<T> sortThenConvert(Comparator<E> comparator, List<E> items, ObjectConverter<T, E> convertor) {

        Collections.sort(items, comparator);

        List<T> list = new ArrayList<>(items.size());

        for (E item : items) {
            list.add(convertor.convert(item));
        }

        return list;
    }
}
