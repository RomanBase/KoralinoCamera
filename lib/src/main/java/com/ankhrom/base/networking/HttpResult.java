package com.ankhrom.base.networking;


@Deprecated
public class HttpResult<T> {

    public enum State {
        OK,
        OK_CACHE,
        CACHE_ONLY,
        ERROR
    }

    private final State result;
    private T value = null;
    private Object data;

    public HttpResult(T value) {
        this.value = value;
        this.result = State.OK;
    }

    public HttpResult(T value, State result) {
        this.value = value;
        this.result = result;
    }

    public HttpResult(T newValue, State result, Object data) {
        this(newValue, result);
        this.data = data;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public State getResult() {
        return result;
    }

    public Object getData() {
        return data;
    }

    public boolean isValid() {
        return result == State.OK || result == State.OK_CACHE || result == State.CACHE_ONLY;
    }

    public boolean isInvalid() {
        return result == State.ERROR;
    }

    public static <T> HttpResult<T> NULL() {

        return new HttpResult<>(null);
    }

    public static <T> HttpResult<T> NULL(State result) {

        return new HttpResult<>(null, result);
    }

    public static <T> HttpResult<T> ERROR() {

        return new HttpResult<>(null, State.ERROR);
    }

    public static <T> HttpResult<T> ERROR(T value) {

        return new HttpResult<>(value, State.ERROR);
    }

    public static <T> HttpResult<T> ERROR(T value, Object data) {

        return new HttpResult<>(value, State.ERROR, data);
    }

    public static <T> HttpResult<T> OK(T value) {

        return new HttpResult<>(value, State.OK);
    }
}
