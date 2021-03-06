package com.fight2.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AsyncHttpUtils {
    private static final AsyncHttpClient HTTP_CLIENT = new AsyncHttpClient();

    public static void get(final String url, final RequestParams params, final AsyncHttpResponseHandler responseHandler) {
        HTTP_CLIENT.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(final String url, final RequestParams params, final AsyncHttpResponseHandler responseHandler) {
        HTTP_CLIENT.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(final String relativeUrl) {
        return HttpUtils.HOST_URL + relativeUrl;
    }

}
