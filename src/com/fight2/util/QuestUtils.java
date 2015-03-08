package com.fight2.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fight2.GameActivity;
import com.fight2.entity.Card;
import com.fight2.entity.Dialog;
import com.fight2.entity.Dialog.Speaker;
import com.fight2.entity.Dialog.OrderType;
import com.fight2.entity.User;
import com.fight2.entity.UserProperties;
import com.fight2.entity.battle.BattleResult;
import com.fight2.entity.quest.QuestGoStatus;
import com.fight2.entity.quest.QuestResult;
import com.fight2.entity.quest.QuestTile;
import com.fight2.entity.quest.QuestTreasureData;
import com.fight2.entity.quest.QuestTile.TileItem;

public class QuestUtils {

    public static QuestResult go(final int row, final int column, final QuestTreasureData oldData, final int endTargetFlag) {
        final String url = HttpUtils.HOST_URL + "/quest/go?row=" + row + "&col=" + column + "&version=" + oldData.getVersion() + "&flag=" + endTargetFlag;
        try {
            final QuestResult result = new QuestResult();
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final QuestGoStatus goStatus = QuestGoStatus.valueOf(responseJson.getString("goStatus"));
            result.setStatus(goStatus);
            if (goStatus == QuestGoStatus.Treasure) {
                result.setTreasureIndex(responseJson.getInt("treasureIndex"));
                final TileItem tileItem = TileItem.valueOf(responseJson.getString("treasureItem"));
                result.setItem(tileItem);
                if (tileItem == TileItem.Card) {
                    final JSONObject cardJson = responseJson.getJSONObject("card");
                    final Card card = CardUtils.cardFromJson(cardJson);
                    result.setCard(card);
                }
            } else if (goStatus == QuestGoStatus.Enemy) {
                final JSONObject enymyJson = responseJson.getJSONObject("enemy");
                final User enemy = new User();
                enemy.setId(enymyJson.getInt("id"));
                enemy.setName(enymyJson.getString("name"));
                result.setEnemy(enemy);
            } else if (goStatus == QuestGoStatus.Task) {
                final JSONObject enymyJson = responseJson.getJSONObject("enemy");
                final User enemy = new User();
                enemy.setId(enymyJson.getInt("id"));
                enemy.setName(enymyJson.getString("name"));
                result.setEnemy(enemy);
            } else if (goStatus== QuestGoStatus.Mine) {
                result.setMineId(responseJson.getInt("mineId"));
            }
            if (responseJson.has("dialog")) {
                final Dialog dialog = dialogFromJson(responseJson.getJSONObject("dialog"));
                result.setDialog(dialog);
            }

            result.setStamina(responseJson.getInt("stamina"));
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
                        questTile.setItem(TileItem.valueOf(questTileJson.getString("item")));
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

    private static Dialog dialogFromJson(final JSONObject json) {
        final Dialog dialog = new Dialog();
        try {
            dialog.setId(json.getInt("id"));
            dialog.setContent(json.getString("content"));
            dialog.setOrderType(OrderType.valueOf(json.getString("orderType")));
            dialog.setSpeaker(Speaker.valueOf(json.getString("speaker")));
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
        return dialog;
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
                    questTile.setItem(TileItem.valueOf(questTileJson.getString("item")));
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

    public static BattleResult attack(final int attackPlayerId, final GameActivity activity) {
        final String url = HttpUtils.HOST_URL + "/quest/attack?id=" + attackPlayerId;
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

    public static UserProperties getUserProperties(final GameActivity activity) {
        final String url = HttpUtils.HOST_URL + "/quest/user-props";
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final UserProperties userProperties = new UserProperties();
            userProperties.setCoin(responseJson.getInt("coin"));
            userProperties.setGuildContrib(responseJson.getInt("guildContrib"));
            userProperties.setStamina(responseJson.getInt("stamina"));
            userProperties.setTicket(responseJson.getInt("ticket"));
            userProperties.setSummonCharm(responseJson.getInt("summonCharm"));
            userProperties.setSummonStone(responseJson.getInt("summonStone"));
            userProperties.setDiamon(responseJson.getInt("diamon"));
            return userProperties;
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean useStaminaBottle(final GameActivity activity) {
        final String url = HttpUtils.HOST_URL + "/quest/usb";
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final int status = responseJson.getInt("status");
            return status == 0;
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}