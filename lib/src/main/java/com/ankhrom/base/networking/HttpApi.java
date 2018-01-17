package com.ankhrom.base.networking;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.ankhrom.base.Base;
import com.ankhrom.base.common.statics.StringHelper;

@Deprecated
public class HttpApi {

    private final Context context;
    private final boolean cacheAllways;

    public HttpApi(Context context) {
        this(context, false);
    }

    public HttpApi(Context context, boolean cacheAllways) {
        this.context = context;
        this.cacheAllways = cacheAllways;
    }

    public void pullIntroCache(String url) {

        HttpResult<InputStream> cacheInputStreamResult = getInputStream(url, true);

        try {
            cacheInputStreamResult.getValue().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HttpResult<InputStream> getInputStream(String url, boolean isCacheEnabled, HttpApiProperty... properties) {
        HttpURLConnection connection = null;
        InputStream inputStream;
        try {
            connection = getConnection(url);
            if (properties != null) {
                for (HttpApiProperty property : properties) {
                    connection.addRequestProperty(property.getField(), property.getValue());
                }
            }

            HttpResult.State inputResult = HttpResult.State.OK;

            if (isCacheEnabled) {
                HttpCache cache = new HttpCache(context);
                HttpCache.HttpCacheItem item = cache.get(url);

                if (connection != null) {
                    long serverLastModified = connection.getLastModified();
                    long cacheLastModified = item.getLastModifiedTime();

                    if (cacheAllways || serverLastModified > cacheLastModified) {
                        InputStream serverStream = new BufferedInputStream(connection.getInputStream());
                        OutputStream cacheStream = item.createOutputStream();

                        copy(serverStream, cacheStream);

                        cacheStream.close();
                        serverStream.close();

                        item.setLastModifiedTime(serverLastModified);
                    } else {
                        inputResult = HttpResult.State.OK_CACHE;
                    }
                } else {
                    inputResult = HttpResult.State.CACHE_ONLY;
                }

                inputStream = item.createInputStream();
            } else {
                inputStream = connection.getInputStream();
            }

            if (inputStream == null) {
                return new HttpResult<>(null, HttpResult.State.ERROR);
            }

            return new HttpResult<>(inputStream, inputResult);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            /*if (connection != null) {
                connection.disconnect();
            }*/
        }

        return new HttpResult<>(null, HttpResult.State.ERROR);
    }

    public <T> HttpResult<T> getJson(Type type, String url, boolean enableCache, HttpApiProperty... properties) {

        HttpResult<InputStream> inputStreamResult = getInputStream(url, enableCache, properties);

        if (inputStreamResult.isInvalid()) {
            return HttpResult.ERROR();
        }

        T result = new Gson().fromJson(new InputStreamReader(inputStreamResult.getValue()), type);

        try {
            inputStreamResult.getValue().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new HttpResult<>(result, inputStreamResult.getResult());
    }

    public HttpResult<String> getData(String url, boolean enableCache, HttpApiProperty... properties) {

        HttpResult<InputStream> inputStreamResult = getInputStream(url, enableCache, properties);

        if (inputStreamResult.isInvalid()) {
            return HttpResult.ERROR();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStreamResult.getValue(), "UTF-8"));

            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
                line = reader.readLine();
            }
            reader.close();

            return HttpResult.OK(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStreamResult.getValue().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return HttpResult.ERROR();
    }

    public HttpResult<String> putData(String url, String data, HttpApiProperty... properties) {

        return pushData("PUT", url, data, properties);
    }

    public <T> HttpResult<T> putData(Type type, String url, String data, HttpApiProperty... properties) {

        return pushData(type, "PUT", url, data, properties);
    }

    public HttpResult<String> postData(String url, String data, HttpApiProperty... properties) {

        return pushData("POST", url, data, properties);
    }

    public <T> HttpResult<T> postData(Type type, String url, String data, HttpApiProperty... properties) {

        return pushData(type, "POST", url, data, properties);
    }

    public HttpResult<String> pushData(String request, String url, String data, HttpApiProperty... properties) {

        HttpURLConnection urlConnection = null;

        try {
            urlConnection = getConnection(url);
            if (urlConnection != null) {
                int responseCode = requestPayload(request, urlConnection, data, properties);

                if (Status.isOk(responseCode)) {
                    String line;
                    StringBuilder builder = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        builder.append(line);
                    }
                    return new HttpResult<>(builder.toString());
                } else {
                    logPostError(responseCode, urlConnection);
                    return new HttpResult<>(null, HttpResult.State.ERROR, responseCode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return new HttpResult<>(null, HttpResult.State.ERROR);
    }

    public <T> HttpResult<T> pushData(Type type, String request, String url, String data, HttpApiProperty... properties) {

        HttpURLConnection urlConnection = null;

        try {
            urlConnection = getConnection(url);
            if (urlConnection != null) {
                int responseCode = requestPayload(request, urlConnection, data, properties);

                if (Status.isOk(responseCode)) {
                    InputStream is = urlConnection.getInputStream();
                    T result = new Gson().fromJson(new InputStreamReader(is), type);
                    is.close();
                    return new HttpResult<>(result);
                } else {
                    logPostError(responseCode, urlConnection);
                    return new HttpResult<>(null, HttpResult.State.ERROR, responseCode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return new HttpResult<>(null, HttpResult.State.ERROR);
    }

    private int requestPayload(String request, HttpURLConnection urlConnection, String data, HttpApiProperty... properties) throws IOException {

        urlConnection.setRequestMethod(request);
        if (properties != null) {
            for (HttpApiProperty property : properties) {
                urlConnection.setRequestProperty(property.getField(), property.getValue());
            }
        }

        if (!StringHelper.isEmpty(data)) {
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            writer.write(data);
            writer.flush();
            writer.close();
            os.close();
        }

        return urlConnection.getResponseCode();
    }

    public HttpURLConnection getConnection(String url) throws IOException {

        if (!Base.isConnected(context)) {
            return null;
        }

        return (HttpURLConnection) new URL(url).openConnection();
    }

    static int copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[2048];
        int count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    private void logPostError(int responseCode, HttpURLConnection conn) {

        Base.logE("HTTP POST failed with response: " + responseCode);

        if (conn != null) {
            try {
                InputStream is = conn.getInputStream();
                String line;
                StringBuilder builder = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                Base.logE(builder.toString());
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }

    public static final class Status {

        public static boolean isOk(int response) {

            return response == HttpsURLConnection.HTTP_OK ||
                    response == HttpsURLConnection.HTTP_CREATED ||
                    response == HttpsURLConnection.HTTP_ACCEPTED;
        }
    }
}
