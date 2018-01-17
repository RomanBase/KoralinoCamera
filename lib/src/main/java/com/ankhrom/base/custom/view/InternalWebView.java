package com.ankhrom.base.custom.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ankhrom.base.R;

public class InternalWebView extends WebView {

    private int fontSize;
    private boolean isJavaScriptEnabled;
    private boolean isCookiesEnabled;
    private boolean isCacheEnabled;

    public InternalWebView(Context context) {
        super(context);
    }

    public InternalWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public InternalWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InternalWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(context, attrs);
    }

    @Deprecated
    public InternalWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        initAttrs(context, attrs);
    }

    protected void initAttrs(Context context, AttributeSet attrs) {

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.InternalWebView, 0, 0);
        try {
            fontSize = (int) ta.getDimension(R.styleable.InternalWebView_fontSize, 0);
            isJavaScriptEnabled = ta.getBoolean(R.styleable.InternalWebView_js_enable, false);
            isCookiesEnabled = ta.getBoolean(R.styleable.InternalWebView_cookies_enable, false);
            isCacheEnabled = ta.getBoolean(R.styleable.InternalWebView_cache_enable, false);
        } finally {
            ta.recycle();
        }

        initWebSettings(getSettings());

        WebViewClient webClient = initWebClient();
        WebChromeClient chromeClient = initChromeClient();

        if (webClient != null) {
            setWebViewClient(webClient);
        }

        if (chromeClient != null) {
            setWebChromeClient(chromeClient);
        }

        setEnableCookies(isCookiesEnabled);
    }

    public void setEnableCookies(boolean enable) {

        CookieManager.getInstance().setAcceptCookie(enable);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, enable);
        }
    }

    public void setCookie(String url, String value) {

        setCookie(getContext(), url, value);
    }

    public static void setCookie(Context context, String url, String value) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setCookie(url, value);
            cookieManager.flush();
        } else {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeSessionCookie();

            cookieManager.setCookie(url, value);

            CookieSyncManager.getInstance().sync();
        }

        CookieSyncManager.getInstance().startSync();
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected WebSettings initWebSettings(final WebSettings webSettings) {

        if (fontSize > 0) {
            webSettings.setDefaultFontSize(fontSize);
        }

        webSettings.setCacheMode(isCacheEnabled ? WebSettings.LOAD_CACHE_ELSE_NETWORK : WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setGeolocationEnabled(false);
        webSettings.setNeedInitialFocus(false);
        webSettings.setSaveFormData(false);

        if (isJavaScriptEnabled) {
            webSettings.setJavaScriptEnabled(true);
        }

        return webSettings;
    }

    protected WebViewClient initWebClient() {

        WebViewClient webClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        };

        return webClient;
    }

    protected WebChromeClient initChromeClient() {

        return null;
    }
}
