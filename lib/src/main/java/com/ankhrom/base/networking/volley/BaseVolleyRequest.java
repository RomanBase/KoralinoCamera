package com.ankhrom.base.networking.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.ankhrom.base.common.statics.StringHelper;
import com.ankhrom.base.interfaces.ObjectFactory;

import java.util.Map;

public abstract class BaseVolleyRequest<T> extends Request<T> {

    private final Response.Listener<T> listener;
    private final String contentType;
    private final Map<String, String> header;
    private final Map<String, String> params;
    private final byte[] body;

    public BaseVolleyRequest(int method, String url, String contentType, Map<String, String> header, Map<String, String> params, byte[] body, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);

        this.contentType = contentType;
        this.header = header;
        this.params = params;
        this.body = body;
        this.listener = listener;
    }

    @Override
    public String getPostBodyContentType() {
        return getBodyContentType();
    }

    @Override
    public byte[] getPostBody() throws AuthFailureError {
        return getBody();
    }

    @Override
    protected Map<String, String> getPostParams() throws AuthFailureError {
        return getParams();
    }

    @Override
    public String getBodyContentType() {
        return !StringHelper.isEmpty(contentType) ? contentType : super.getBodyContentType();
    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return header != null ? header : super.getHeaders();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return body != null ? body : super.getBody();
    }

    @Override
    protected void deliverResponse(T response) {

        if (listener != null) {
            listener.onResponse(response);
        }
    }

    public void queue(ObjectFactory factory) {

        queue(factory.getRequestQueue());
    }

    public void queue(RequestQueue queue) {

        queue.add(this);
    }
}
