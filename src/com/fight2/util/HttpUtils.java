package com.fight2.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.andengine.util.debug.Debug;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpUtils {
    public static final String HOST_URL = "http://112.124.37.194:8888";
    private static final HttpClient HTTP_CLIENT = new DefaultHttpClient();

    public static JSONObject getJSONFromUrl(final String url) throws ClientProtocolException, IOException, JSONException {
        final String jsonString = getJSONString(url);
        final JSONObject jsonObj = new JSONObject(jsonString);
        return jsonObj;
    }

    private static String getJSONString(final String url) throws ClientProtocolException, IOException {
        // Making HTTP request
        final StringBuilder jsonString = new StringBuilder();
        try {
            final HttpGet httpGet = new HttpGet(url);
            final HttpResponse httpResponse = HTTP_CLIENT.execute(httpGet);
            final HttpEntity httpEntity = httpResponse.getEntity();
            final InputStream inputStream = httpEntity.getContent();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            inputStream.close();
        } catch (final ConnectTimeoutException e) {
            throw new RuntimeException(e);
        }
        return jsonString.toString();
    }

    public static boolean postJSONString(final String url, final String json) throws ClientProtocolException, IOException {
        boolean isOk = false;
        try {
            final HttpPost httpPost = new HttpPost(url);
            final List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("jsonMsg", json));
            final UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params);
            httpPost.setEntity(formEntity);

            final HttpResponse httpResponse = HTTP_CLIENT.execute(httpPost);
            final int statusCode = httpResponse.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                isOk = true;
            } else {
                Debug.e("Post status ==> :" + String.valueOf(statusCode));
                isOk = false;
            }
        } catch (final ConnectTimeoutException e) {
            throw new RuntimeException(e);
        }
        return isOk;
    }

    public static JSONArray getJSONArrayFromUrl(final String url) throws ClientProtocolException, IOException, JSONException {
        final String jsonString = getJSONString(url);
        final JSONArray jsonArray = new JSONArray(jsonString);
        return jsonArray;
    }

}
