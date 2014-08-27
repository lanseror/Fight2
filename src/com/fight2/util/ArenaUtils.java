package com.fight2.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fight2.GameActivity;
import com.fight2.entity.Arena;
import com.fight2.entity.User;
import com.fight2.entity.UserArenaInfo;
import com.fight2.entity.UserArenaRecord;
import com.fight2.entity.UserArenaRecord.UserArenaRecordStatus;
import com.fight2.entity.battle.BattleRecord;
import com.fight2.entity.battle.BattleResult;
import com.fight2.entity.battle.SkillApplyParty;
import com.fight2.entity.battle.SkillOperation;
import com.fight2.entity.battle.SkillRecord;
import com.fight2.entity.battle.SkillType;

public class ArenaUtils {
    private static int selectedArenaId = 0;

    public static int getSelectedArenaId() {
        return selectedArenaId;
    }

    public static void setSelectedArenaId(final int selectedArenaId) {
        ArenaUtils.selectedArenaId = selectedArenaId;
    }

    public static UserArenaInfo enter(final GameActivity activity) {
        final String url = HttpUtils.HOST_URL + "/arena/enter?id=" + selectedArenaId;
        final UserArenaInfo userArenaInfo = new UserArenaInfo();
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);

            userArenaInfo.setLose(responseJson.getInt("lose"));
            userArenaInfo.setMight(responseJson.getInt("might"));
            userArenaInfo.setRankNumber(responseJson.getInt("rankNumber"));
            userArenaInfo.setWin(responseJson.getInt("win"));
            userArenaInfo.setRemainTime(responseJson.getString("remainTime"));
            final List<UserArenaRecord> arenaRecords = new ArrayList<UserArenaRecord>();

            final JSONArray arenaRecordsJsonArray = responseJson.getJSONArray("arenaRecords");
            for (int i = 0; i < arenaRecordsJsonArray.length(); i++) {
                final UserArenaRecord userArenaRecord = new UserArenaRecord();
                final JSONObject arenaRecordsJson = arenaRecordsJsonArray.getJSONObject(i);
                final String status = arenaRecordsJson.getString("status");
                userArenaRecord.setStatus(UserArenaRecordStatus.valueOf(status));
                final JSONObject arenaRecordsUserJson = arenaRecordsJson.getJSONObject("user");
                final User player = new User();
                player.setId(arenaRecordsUserJson.getInt("id"));
                final String avatar = arenaRecordsUserJson.optString("avatar");
                if (avatar != null && !"".equals(avatar)) {
                    final String localAvatar = ImageUtils.getLocalString(avatar, activity);
                    player.setAvatar(localAvatar);
                    TextureFactory.getInstance().addCardResource(activity, localAvatar);
                }
                player.setName(arenaRecordsUserJson.getString("name"));
                userArenaRecord.setUser(player);
                arenaRecords.add(userArenaRecord);
            }
            userArenaInfo.setArenaRecords(arenaRecords);
            return userArenaInfo;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static void exit() {
        final String url = HttpUtils.HOST_URL + "/arena/exit?id=" + selectedArenaId;
        try {
            HttpUtils.doGet(url);
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Arena> getArenas(final GameActivity activity) {
        final String url = HttpUtils.HOST_URL + "/arena/list-started";
        final List<Arena> arenas = new ArrayList<Arena>();
        try {
            final JSONArray responseJson = HttpUtils.getJSONArrayFromUrl(url);
            for (int i = 0; i < responseJson.length(); i++) {
                final JSONObject jsonObject = responseJson.getJSONObject(i);
                final Arena arena = new Arena();
                arena.setId(jsonObject.getInt("id"));
                arena.setName(jsonObject.getString("name"));
                arena.setOnlineNumber(jsonObject.getInt("onlineNumber"));
                arena.setRemainTime(jsonObject.getString("remainTime"));
                arenas.add(arena);
            }
            return arenas;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static BattleResult attack(final int index, final GameActivity activity) {
        final String url = HttpUtils.HOST_URL + "/arena/attack.action?id=" + selectedArenaId + "&index=" + index;
        final BattleResult battleResult = new BattleResult();
        final List<BattleRecord> battleRecords = new ArrayList<BattleRecord>();
        battleResult.setBattleRecord(battleRecords);
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            battleResult.setWinner(responseJson.getBoolean("isWinner"));
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
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return battleResult;
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