package com.ankhrom.base.interfaces;

public interface ObjectConverter<T, E> {

    T convert(E object);
}
