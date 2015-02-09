package com.fight2.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fight2.entity.battle.SkillApplyParty;
import com.fight2.entity.battle.SkillOperation;
import com.fight2.entity.battle.SkillType;

public class SkillUtils {

    private final static Map<String, String> EFFECT_MAP = new HashMap<String, String>();
    static {
        EFFECT_MAP.put("-1" + SkillType.HP, "减少%s当前生命值的%s%%");
        EFFECT_MAP.put("1" + SkillType.HP, "%s的生命值提升%s%%");
        EFFECT_MAP.put("-1" + SkillType.ATK, "降低%s当前攻击力的%s%%");
        EFFECT_MAP.put("1" + SkillType.ATK, "%s的攻击力提升%s%%");
        EFFECT_MAP.put("1" + SkillType.Defence, "%s受到的伤害降低%s%%");
        EFFECT_MAP.put("1" + SkillType.Revival, "%s死亡时有机会以%s%%的生命值复活");
    }

    public static List<SkillOperation> opsFromJson(final JSONObject skillJson) {
        final List<SkillOperation> operations = new ArrayList<SkillOperation>();
        try {
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
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
        return operations;
    }

    public static String getEffect(final List<SkillOperation> operations) {
        final StringBuilder effectStrs = new StringBuilder();
        for (final SkillOperation operation : operations) {
            final String effectStr = EFFECT_MAP.get(String.valueOf(operation.getSign()) + operation.getSkillType());
            if (effectStrs.length() > 0) {
                effectStrs.append("，");
            }
            effectStrs.append(String.format(effectStr, operation.getSkillApplyParty().getDescription(), operation.getPoint()));
        }
        effectStrs.append("。");
        return effectStrs.toString();
    }
}