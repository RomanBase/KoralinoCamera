package com.ankhrom.base.networking.volley;


import com.android.volley.Response;
import com.android.volley.VolleyError;

public abstract class RequestListener<T> {

    private final Response.Listener<T> listener;
    private final Response.ErrorListener errorListener;

    public RequestListener() {

        listener = new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                RequestListener.this.onResponse(response);
            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                RequestListener.this.onErrorResponse(error);
            }
        };
    }

    protected abstract void onResponse(T response);

    protected abstract void onErrorResponse(VolleyError error);

    public Response.Listener<T> getListener() {
        return listener;
    }

    public Response.ErrorListener getErrorListener() {
        return errorListener;
    }
}
