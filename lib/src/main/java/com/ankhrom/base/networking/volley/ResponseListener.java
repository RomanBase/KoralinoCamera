package com.ankhrom.base.networking.volley;


import com.android.volley.VolleyError;

public interface ResponseListener<T> {

    abstract void onResponse(T response);

    abstract void onErrorResponse(VolleyError error);
}
