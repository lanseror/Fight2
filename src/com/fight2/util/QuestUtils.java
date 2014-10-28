package com.fight2.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fight2.entity.Card;
import com.fight2.entity.QuestResult;
import com.fight2.entity.QuestResult.TileItem;
import com.fight2.entity.QuestTile;

public class QuestUtils {

    public static QuestResult go(final int row, final int column) {
        final String url = HttpUtils.HOST_URL + "/quest/go?row=" + row + "&col=" + column;
        try {
            final QuestResult result = new QuestResult();
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            result.setStatus(responseJson.getInt("status"));
            if (result.getStatus() == 1) {
                result.setTreasureIndex(responseJson.getInt("treasureIndex"));
                final TileItem tileItem = TileItem.valueOf(responseJson.getString("treasureItem"));
                result.setItem(tileItem);
                if (tileItem == TileItem.Card) {
                    final JSONObject cardJson = responseJson.getJSONObject("card");
                    final Card card = new Card();
                    card.setId(cardJson.getInt("id"));
                    card.setAtk(cardJson.getInt("atk"));
                    card.setAvatar(cardJson.getString("avatar"));
                    card.setHp(cardJson.getInt("hp"));
                    card.setStar(cardJson.getInt("star"));
                    card.setImage(cardJson.getString("image"));
                    card.setName(cardJson.getString("name"));
                    card.setSkill(cardJson.optString("skill"));
                    final JSONObject cardTemplateJson = cardJson.getJSONObject("cardTemplate");
                    card.setTemplateId(cardTemplateJson.getInt("id"));
                    result.setCard(card);
                }
            }

            return result;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<QuestTile> getQuestTreasure() {
        final String url = HttpUtils.HOST_URL + "/quest/treasure";
        final List<QuestTile> questTiles = new ArrayList<QuestTile>();
        try {
            final JSONArray questTileJsonArray = HttpUtils.getJSONArrayFromUrl(url);
            for (int i = 0; i < questTileJsonArray.length(); i++) {
                final JSONObject questTileJson = questTileJsonArray.getJSONObject(i);
                final QuestTile questTile = new QuestTile();
                questTile.setRow(questTileJson.getInt("row"));
                questTile.setCol(questTileJson.getInt("col"));
                questTiles.add(questTile);
            }

        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
        return questTiles;
    }
}