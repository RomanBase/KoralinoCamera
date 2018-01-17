package com.ankhrom.base.networking.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class StringRequest extends BaseVolleyRequest<String> {

    public StringRequest(int method, String url, String contentType, Map<String, String> header, Map<String, String> params, byte[] body, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, contentType, header, params, body, listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {

        try {
            String string = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(string, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
}
