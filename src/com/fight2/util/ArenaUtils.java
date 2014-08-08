package com.fight2.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
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

    public static List<BattleRecord> attack(final int id, final GameActivity activity) {
        final String url = HttpUtils.HOST_URL + "/arena/attack.action?id=" + id;
        final List<BattleRecord> battleRecords = new ArrayList<BattleRecord>();
        try {
            final JSONArray responseJson = HttpUtils.getJSONArrayFromUrl(url);
            for (int i = 0; i < responseJson.length(); i++) {
                final JSONObject battleRecordJson = responseJson.getJSONObject(i);
                final BattleRecord battleRecord = new BattleRecord();
                battleRecord.setActionPlayer(battleRecordJson.getString("actionPlayer"));
                battleRecord.setAtk(battleRecordJson.getInt("atk"));
                battleRecord.setAtkParty(battleRecordJson.getInt("atkParty"));
                battleRecord.setDefenceParty(battleRecordJson.getInt("defenceParty"));
                final JSONObject skillJson = battleRecordJson.optJSONObject("skill");
                if (skillJson != null) {
                    final SkillRecord skill = new SkillRecord();
                    battleRecord.setSkill(skill);
                    skill.setCardIndex(skillJson.getInt("cardIndex"));
                    skill.setEffect(skillJson.getString("effect"));
                    skill.setName(skillJson.getString("name"));
                    final List<SkillOperation> operations = new ArrayList<SkillOperation>();
                    skill.setOperations(operations);
                    final JSONArray operationJsonArray = skillJson.getJSONArray("operations");
                    for (int operationIndex = 0; operationIndex < operationJsonArray.length(); operationIndex++) {
                        final JSONObject operationJson = operationJsonArray.getJSONObject(operationIndex);
                        final SkillOperation operation = new SkillOperation();
                        operation.setPoint(operationJson.getInt("point"));
                        operation.setSign(operationJson.getInt("sign"));
                        operation.setSkillApplyParty(SkillApplyParty.valueOf(operationJson.getString("skillApplyParty")));
                        operation.setSkillType(SkillType.valueOf(operationJson.getString("skillType")));
                        operations.add(operation);
                    }
                }
                battleRecords.add(battleRecord);
            }

        } catch (final JSONException e) {
            throw new RuntimeException(e);
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return battleRecords;
    }

    public static String getTestString(final GameActivity activity) {
        final StringBuilder jsonString = new StringBuilder();
        try {
            final InputStream inputStream = activity.getAssets().open("json.txt");
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            inputStream.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return jsonString.toString();
    }
}