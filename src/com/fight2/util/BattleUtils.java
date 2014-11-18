package com.fight2.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fight2.entity.battle.BattleRecord;
import com.fight2.entity.battle.BattleResult;
import com.fight2.entity.battle.SkillApplyParty;
import com.fight2.entity.battle.SkillOperation;
import com.fight2.entity.battle.SkillRecord;
import com.fight2.entity.battle.SkillType;

public class BattleUtils {

    public static BattleResult attack(final JSONObject responseJson) {
        final BattleResult battleResult = new BattleResult();
        final List<BattleRecord> battleRecords = new ArrayList<BattleRecord>();
        battleResult.setBattleRecord(battleRecords);
        try {
            battleResult.setWinner(responseJson.getBoolean("isWinner"));
            battleResult.setBaseMight(responseJson.getInt("baseMight"));
            battleResult.setAliveMight(responseJson.getInt("aliveMight"));
            battleResult.setCwMight(responseJson.getInt("cwMight"));
            battleResult.setTotalMight(responseJson.getInt("totalMight"));
            battleResult.setCwRate(responseJson.getInt("cwRate"));
            final JSONArray battleRecordJsonArray = responseJson.getJSONArray("battleRecord");
            for (int i = 0; i < battleRecordJsonArray.length(); i++) {
                final JSONObject battleRecordJson = battleRecordJsonArray.getJSONObject(i);
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
        }
        return battleResult;
    }
}