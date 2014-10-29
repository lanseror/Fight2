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
import com.fight2.entity.QuestTreasureData;

public class QuestUtils {

    public static QuestResult go(final int row, final int column, final QuestTreasureData oldData) {
        final String url = HttpUtils.HOST_URL + "/quest/go?row=" + row + "&col=" + column + "&version=" + oldData.getVersion();
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
            final boolean treasureUpdated = responseJson.getBoolean("treasureUpdate");
            result.setTreasureUpdated(treasureUpdated);
            if (treasureUpdated) {
                final QuestTreasureData questTreasureData = new QuestTreasureData();
                final JSONObject questTreasureDataJson = responseJson.getJSONObject("treasure");
                questTreasureData.setVersion(questTreasureDataJson.getLong("version"));
                if (questTreasureDataJson.has("questTiles")) {
                    final List<QuestTile> questTiles = new ArrayList<QuestTile>();
                    final JSONArray questTileJsonArray = questTreasureDataJson.getJSONArray("questTiles");
                    for (int i = 0; i < questTileJsonArray.length(); i++) {
                        final JSONObject questTileJson = questTileJsonArray.getJSONObject(i);
                        final QuestTile questTile = new QuestTile();
                        questTile.setRow(questTileJson.getInt("row"));
                        questTile.setCol(questTileJson.getInt("col"));
                        questTiles.add(questTile);
                    }
                    questTreasureData.setQuestTiles(questTiles);
                }
                result.setQuestTreasureData(questTreasureData);
            }

            return result;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static QuestTreasureData getQuestTreasure(final QuestTreasureData oldData) {
        final String url = HttpUtils.HOST_URL + "/quest/treasure?version=" + oldData.getVersion();
        final QuestTreasureData questTreasureData = new QuestTreasureData();
        try {
            final JSONObject questTreasureDataJson = HttpUtils.getJSONFromUrl(url);
            questTreasureData.setVersion(questTreasureDataJson.getLong("version"));
            if (questTreasureDataJson.has("questTiles")) {
                final List<QuestTile> questTiles = new ArrayList<QuestTile>();
                final JSONArray questTileJsonArray = questTreasureDataJson.getJSONArray("questTiles");
                for (int i = 0; i < questTileJsonArray.length(); i++) {
                    final JSONObject questTileJson = questTileJsonArray.getJSONObject(i);
                    final QuestTile questTile = new QuestTile();
                    questTile.setRow(questTileJson.getInt("row"));
                    questTile.setCol(questTileJson.getInt("col"));
                    questTiles.add(questTile);
                }
                questTreasureData.setQuestTiles(questTiles);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
        return questTreasureData;
    }
}