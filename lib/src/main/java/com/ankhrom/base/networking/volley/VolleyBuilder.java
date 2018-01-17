package com.ankhrom.base.networking.volley;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;

import java.io.File;

import com.ankhrom.base.common.statics.StringHelper;

public class VolleyBuilder {

    private final Context context;

    private int cacheSize;
    private String cacheFile;
    private HttpStack stack;

    public VolleyBuilder(Context context) {
        this.context = context;
    }

    public VolleyBuilder cacheSize(int mb) {

        cacheSize = 1024 * 1024 * mb;
        return this;
    }

    public VolleyBuilder cacheFile(String name) {

        cacheFile = name;
        return this;
    }

    public VolleyBuilder stack(HttpStack stack) {

        this.stack = stack;
        return this;
    }

    public RequestQueue build() {

        File cacheDir = new File(context.getCacheDir(), !StringHelper.isEmpty(cacheFile) ? cacheFile : "cache-" + System.nanoTime());

        if (cacheSize == 0) {
            cacheSize(30);
        }

        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir, cacheSize), new BasicNetwork(stack != null ? stack : new HurlStack()));
        queue.start();

        return queue;
    }
}
