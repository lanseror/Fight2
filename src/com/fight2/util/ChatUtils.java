package com.fight2.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.SparseArray;

import com.fight2.GameActivity;
import com.fight2.entity.ChatMessage;

public class ChatUtils {
    public static int msgIndex = -1;
    private static int containMsgSize = 0;
    public static int displayedMiniMsg = 0;
    public static int displayedFullMsg = 0;
    private static SparseArray<ChatMessage> CHAT_MESSAGES = new SparseArray<ChatMessage>();

    public static boolean send(final String msg) {
        try {
            final String url = HttpUtils.HOST_URL + "/chat/send.action?msg=" + URLEncoder.encode(URLEncoder.encode(msg, "UTF-8"), "UTF-8");
            return HttpUtils.doGet(url);

        } catch (final UnsupportedEncodingException e) {
            LogUtils.e(e);
        } catch (final ClientProtocolException e) {
            LogUtils.e(e);
        } catch (final IOException e) {
            LogUtils.e(e);
        }
        return false;
    }

    public static synchronized List<ChatMessage> get(final GameActivity activity) {
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
                message.setContent(URLDecoder.decode(messageJson.getString("content"), "UTF-8"));
                message.setDate(messageJson.getString("date"));
                messages.add(message);
                CHAT_MESSAGES.put(++containMsgSize, message);
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

    public static synchronized ChatMessage getDisplayMessage(final DisplayChannel displayChannel) {
        final int displayedMsg = (displayChannel == DisplayChannel.MiniChatRoom ? displayedMiniMsg : displayedFullMsg);
        final int tempDisplayedMsg = displayedMsg + 1;
        final ChatMessage message = CHAT_MESSAGES.get(tempDisplayedMsg);
        if (message != null) {
            switch (displayChannel) {
                case MiniChatRoom:
                    displayedMiniMsg = tempDisplayedMsg;
                    if (displayedMiniMsg - displayedFullMsg > 25) {
                        displayedFullMsg = displayedMiniMsg - 25;
                    }
                    break;
                case FullChatRoom:
                    displayedFullMsg = tempDisplayedMsg;
                    if (displayedFullMsg > 1 && displayedFullMsg - displayedMiniMsg > 2) {
                        displayedMiniMsg = displayedFullMsg - 2;
                    }
                    break;
            }
        }
        return message;
    }

    public enum DisplayChannel {
        MiniChatRoom,
        FullChatRoom;
    }
}