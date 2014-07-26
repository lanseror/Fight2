package com.fight2.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpUtils {
    private static final HttpClient HTTP_CLIENT = new DefaultHttpClient();

    public static JSONObject getJSONFromUrl(final String url) throws ClientProtocolException, IOException, JSONException {
        // Making HTTP request
        final HttpPost httpPost = new HttpPost(url);
        final HttpResponse httpResponse = HTTP_CLIENT.execute(httpPost);
        final HttpEntity httpEntity = httpResponse.getEntity();
        final InputStream inputStream = httpEntity.getContent();
        final StringBuilder jsonString = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String line = null;
        while ((line = reader.readLine()) != null) {
            jsonString.append(line);
        }
        inputStream.close();
        // try parse the string to a JSON object
        final JSONObject jObj = new JSONObject(jsonString.toString());

        return jObj;
    }

}
