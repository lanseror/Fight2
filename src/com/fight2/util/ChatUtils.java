package com.fight2.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.andengine.util.debug.Debug;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fight2.GameActivity;
import com.fight2.entity.ChatMessage;

public class ChatUtils {
    public static int msgIndex = -1;

    public static boolean send(final String msg) {
        try {
            final String url = HttpUtils.HOST_URL + "/chat/send.action?msg=" + URLEncoder.encode(msg, "UTF-8");
            return HttpUtils.doGet(url);

        } catch (final UnsupportedEncodingException e) {
            Debug.e(e);
        } catch (final ClientProtocolException e) {
            Debug.e(e);
        } catch (final IOException e) {
            Debug.e(e);
        }
        return false;
    }

    public static List<ChatMessage> get(final GameActivity activity) {
        final String url = HttpUtils.HOST_URL + "/chat/get.action?index=" + msgIndex;
        final List<ChatMessage> messages = new ArrayList<ChatMessage>();
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final int index = responseJson.getInt("index");
            msgIndex = index;
            final JSONArray messageJsonArray = responseJson.getJSONArray("msg");
            for (int i = 0; i < messageJsonArray.length(); i++) {
                final JSONObject messageJson = messageJsonArray.getJSONObject(i);
                final ChatMessage message = new ChatMessage();
                message.setSender(messageJson.getString("sender"));
                message.setContent(messageJson.getString("content"));
                message.setDate(messageJson.getString("date"));
                messages.add(message);
            }
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

        return messages;
    }

}