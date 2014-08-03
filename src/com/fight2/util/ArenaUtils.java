package com.fight2.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fight2.GameActivity;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Player;

public class ArenaUtils {

    public static List<Player> getCompetitors(final GameActivity activity) {
        final String url = HttpUtils.HOST_URL + "/arena/competitors";
        final List<Player> competitors = new ArrayList<Player>();
        try {
            final JSONArray responseJson = HttpUtils.getJSONArrayFromUrl(url);

            for (int i = 0; i < responseJson.length(); i++) {
                final JSONObject jsonObject = responseJson.getJSONObject(i);
                final Player player = new Player();
                player.setId(jsonObject.getInt("id"));
                player.setAvatar(jsonObject.optString("avatar", TextureEnum.COMMON_DEFAULT_AVATAR.name()));
                player.setName(jsonObject.getString("name"));
                competitors.add(player);
            }
            return competitors;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static void attack(final int id) {
        final String url = HttpUtils.HOST_URL + "/arena/attack.action?id=" + id;
        try {
            final JSONArray responseJson = HttpUtils.getJSONArrayFromUrl(url);

        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
    }
}