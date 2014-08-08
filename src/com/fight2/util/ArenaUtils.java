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
import com.fight2.entity.battle.BattleRecord;
import com.fight2.entity.battle.SkillApplyParty;
import com.fight2.entity.battle.SkillOperation;
import com.fight2.entity.battle.SkillPointAttribute;
import com.fight2.entity.battle.SkillRecord;
import com.fight2.entity.battle.SkillType;

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

    public static List<BattleRecord> attack(final int id) {
        final String url = HttpUtils.HOST_URL + "/arena/attack.action?id=" + id;
        final List<BattleRecord> battleRecords = new ArrayList<BattleRecord>();
        try {
            final JSONArray responseJson = HttpUtils.getJSONArrayFromUrl(url);
            for (int i = 0; i < responseJson.length(); i++) {
                final JSONObject jsonObject = responseJson.getJSONObject(i);
                final BattleRecord battleRecord = new BattleRecord();
                battleRecord.setActionPlayer(jsonObject.getString("actionPlayer"));
                battleRecord.setAtk(jsonObject.getInt("atk"));
                battleRecord.setAtkParty(jsonObject.getInt("atkParty"));
                battleRecord.setDefenceParty(jsonObject.getInt("defenceParty"));
                final JSONObject skillJson = jsonObject.optJSONObject("skill");
                if (skillJson != null) {
                    final SkillRecord skill = new SkillRecord();
                    battleRecord.setSkill(skill);
                    skill.setCardIndex(jsonObject.getInt("cardIndex"));
                    skill.setEffect(jsonObject.getString("effect"));
                    skill.setName(jsonObject.getString("name"));
                    final List<SkillOperation> operations = new ArrayList<SkillOperation>();
                    skill.setOperations(operations);
                    final JSONArray operationJsonArray = jsonObject.getJSONArray("operations");
                    for (int operationIndex = 0; operationIndex < operationJsonArray.length(); operationIndex++) {
                        final JSONObject operationJson = operationJsonArray.getJSONObject(operationIndex);
                        final SkillOperation operation = new SkillOperation();
                        operation.setPoint(operationJson.getInt("point"));
                        operation.setSign(operationJson.getInt("sign"));
                        operation.setSkillApplyParty(SkillApplyParty.valueOf(operationJson.getString("skillApplyParty")));
                        operation.setSkillPointAttribute(SkillPointAttribute.valueOf(operationJson.getString("skillPointAttribute")));
                        operation.setSkillType(SkillType.valueOf(operationJson.getString("skillType")));
                        operations.add(operation);
                    }
                }
                battleRecords.add(battleRecord);
            }

        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
        return battleRecords;
    }
}