package com.ankhrom.base.networking.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

public class GsonRequest<T> extends BaseVolleyRequest<T> {

    private final Type type;

    public GsonRequest(Type type, int method, String url, String contentType, Map<String, String> header, Map<String, String> params, byte[] body, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, contentType, header, params, body, listener, errorListener);

        this.type = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Response<T> parseNetworkResponse(NetworkResponse response) {

        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success((T) new Gson().fromJson(json, type), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
