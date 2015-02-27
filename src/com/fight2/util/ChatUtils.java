package com.fight2.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.SparseArray;

import com.fight2.GameActivity;
import com.fight2.entity.ChatMessage;
import com.fight2.entity.engine.SmallChatRoom;
import com.loopj.android.http.JsonHttpResponseHandler;

public class ChatUtils {
    public static int msgIndex = -1;
    private static int containMsgSize = 0;
    public static int displayedMiniMsg = 0;
    public static int displayedFullMsg = 0;
    public static boolean isGetting = false;
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

    public static void get(final GameActivity activity) {
        if (isGetting) {
            return;
        }
        isGetting = true;
        final String url = "/chat/get.action?index=" + msgIndex;
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AsyncHttpUtils.get(url, null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(final int statusCode, final Header[] headers, final JSONObject responseJson) {
                        try {
                            final int index = responseJson.getInt("index");
                            msgIndex = index;
                            final JSONArray messageJsonArray = responseJson.getJSONArray("msg");
                            for (int i = 0; i < messageJsonArray.length(); i++) {
                                final JSONObject messageJson = messageJsonArray.getJSONObject(i);
                                final ChatMessage message = new ChatMessage();
                                message.setSender(messageJson.getString("sender"));
                                message.setContent(URLDecoder.decode(messageJson.getString("content"), "UTF-8"));
                                message.setDate(messageJson.getString("date"));
                                CHAT_MESSAGES.put(++containMsgSize, message);
                            }
                        } catch (final JSONException | UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        isGetting = false;
                    }
                });
            }

        });

    }

    public static synchronized void updateFullChatMessage() {
        displayedFullMsg = displayedMiniMsg > 10 ? displayedMiniMsg - 10 : 0;
    }

    public static ChatMessage getDisplayMessage(final DisplayChannel displayChannel) {
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

    public static void startGetMsg(final GameActivity activity) {
        final SmallChatRoom smallChatRoom = activity.getGameHub().getSmallChatRoom();
        smallChatRoom.startGetMsg();
    }

    public static void stopGetMsg(final GameActivity activity) {
        final SmallChatRoom smallChatRoom = activity.getGameHub().getSmallChatRoom();
        smallChatRoom.stopGetMsg();
    }
}