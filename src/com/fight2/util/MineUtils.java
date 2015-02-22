package com.fight2.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fight2.GameActivity;
import com.fight2.entity.GameMine;
import com.fight2.entity.GameMine.MineType;
import com.fight2.entity.User;
import com.fight2.entity.battle.BattleResult;

public class MineUtils {

    public static List<GameMine> list() {
        final String url = HttpUtils.HOST_URL + "/mine/mines";
        try {
            final List<GameMine> mines = new ArrayList<GameMine>();
            final JSONArray responseJsonArray = HttpUtils.getJSONArrayFromUrl(url);
            for (int index = 0; index < responseJsonArray.length(); index++) {
                final JSONObject responseJson = responseJsonArray.getJSONObject(index);
                final GameMine mine = new GameMine();
                mine.setId(responseJson.getInt("id"));
                mine.setCol(responseJson.getInt("col"));
                mine.setRow(responseJson.getInt("row"));
                mine.setType(MineType.valueOf(responseJson.getString("type")));
                final JSONObject userJson = responseJson.getJSONObject("owner");
                final User owner = new User();
                owner.setId(userJson.getInt("id"));
                owner.setName(userJson.getString("name"));
                mine.setOwner(owner);
                mines.add(mine);
            }
            return mines;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static User getOwner() {
        final String url = HttpUtils.HOST_URL + "/mine/owner";
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final User owner = new User();
            owner.setId(responseJson.getInt("id"));
            owner.setName(responseJson.getString("name"));
            return owner;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static BattleResult attack(final GameActivity activity) {
        final String url = HttpUtils.HOST_URL + "/mine/attack";
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            return BattleUtils.attack(activity, responseJson);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}