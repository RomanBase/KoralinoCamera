package com.ankhrom.base.networking;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;


@Deprecated
public class HttpCache {

    public class HttpCacheItem {

        private final Context context;
        private final HttpCache httpCache;
        private final String fileName;

        public HttpCacheItem(Context context, HttpCache httpCache, String fileName) {
            this.context = context;
            this.httpCache = httpCache;
            this.fileName = fileName;
        }

        public long getLastModifiedTime() {
            File f = new File(httpCache.getCacheDir(), fileName);
            if (f.exists()) {
                return f.lastModified();
            }
            return 0;
        }

        public void setLastModifiedTime(long time) {
            File f = new File(httpCache.getCacheDir(), fileName);
            if (f.exists()) {
                f.setLastModified(time);
            }
        }

        public InputStream createInputStream() throws FileNotFoundException {
            File f = new File(httpCache.getCacheDir(), fileName);
            if (f.exists()) {
                return new FileInputStream(f);
            }
            return null;
        }

        public OutputStream createOutputStream() throws IOException {
            File f = new File(httpCache.getCacheDir(), fileName);
            if (!f.exists()) {
                f.createNewFile();
            }
            return new FileOutputStream(f);
        }
    }

    final Context context;

    public HttpCache(Context context) {
        this.context = context;
    }

    public HttpCacheItem get(String url) {
        return new HttpCacheItem(context, this, urlToFileName(url));
    }


    private File getCacheDir() {
        File cacheDir = new File(context.getCacheDir(), "httpcache");
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        return cacheDir;
    }

    String urlToFileName(String url) {
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = url.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();
            hash = byteArrayToHex(bytes);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hash;
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format(Locale.US, "%02x", b & 0xff));
        return sb.toString();
    }
}
