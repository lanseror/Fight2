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
import com.fight2.entity.ArenaContinuousWin;
import com.fight2.entity.ArenaRanking;
import com.fight2.entity.ArenaReward;
import com.fight2.entity.Guild;
import com.fight2.entity.ArenaReward.ArenaRewardType;
import com.fight2.entity.ArenaRewardItem;
import com.fight2.entity.ArenaRewardItem.ArenaRewardItemType;
import com.fight2.entity.Card;
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

    public static boolean refresh() {
        final String url = HttpUtils.HOST_URL + "/arena/refresh?id=" + selectedArenaId;
        try {
            return HttpUtils.doGet(url);
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArenaContinuousWin getContinuousWin() {
        final ArenaContinuousWin arenaContinuousWin = new ArenaContinuousWin();
        final String url = HttpUtils.HOST_URL + "/arena/gcw";
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            arenaContinuousWin.setTime(responseJson.getInt("time"));
            arenaContinuousWin.setRate(responseJson.getInt("rate"));
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
        return arenaContinuousWin;
    }

    public static List<ArenaRanking> getArenaRanking(final GameActivity activity) {
        final List<ArenaRanking> arenaRankings = new ArrayList<ArenaRanking>();
        final String url = HttpUtils.HOST_URL + "/arena/get-ranking?id=" + selectedArenaId;
        try {
            final JSONArray responseJsonArray = HttpUtils.getJSONArrayFromUrl(url);
            for (int i = 0; i < responseJsonArray.length(); i++) {
                final JSONObject responseJson = responseJsonArray.getJSONObject(i);
                final ArenaRanking arenaRanking = new ArenaRanking();
                arenaRanking.setId(responseJson.getInt("id"));
                arenaRanking.setMight(responseJson.getInt("might"));
                arenaRanking.setRankNumber(responseJson.getInt("rankNumber"));
                final JSONObject userJson = responseJson.getJSONObject("user");
                final User user = new User();
                user.setId(userJson.getInt("id"));
                user.setName(userJson.getString("name"));
                final JSONObject guildJson = userJson.optJSONObject("guild");
                if (guildJson != null) {
                    final Guild guild = new Guild();
                    guild.setId(guildJson.getInt("id"));
                    guild.setName(guildJson.getString("name"));
                    user.setGuild(guild);
                }
                arenaRanking.setUser(user);
                arenaRankings.add(arenaRanking);
            }
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
        return arenaRankings;
    }

    public static List<ArenaReward> getArenaReward(final GameActivity activity) {
        final List<ArenaReward> arenaRewards = new ArrayList<ArenaReward>();
        final String url = HttpUtils.HOST_URL + "/arena-reward/list-json?arenaId=" + selectedArenaId;
        try {
            final JSONArray responseJsonArray = HttpUtils.getJSONArrayFromUrl(url);
            for (int i = 0; i < responseJsonArray.length(); i++) {
                final JSONObject responseJson = responseJsonArray.getJSONObject(i);
                final ArenaReward arenaReward = new ArenaReward();
                arenaReward.setId(responseJson.getInt("id"));
                arenaReward.setMax(responseJson.getInt("max"));
                arenaReward.setMin(responseJson.getInt("min"));
                arenaReward.setType(ArenaRewardType.valueOf(responseJson.getString("type")));
                final JSONArray rewardItemJsonArray = responseJson.getJSONArray("rewardItems");
                final List<ArenaRewardItem> rewardItems = new ArrayList<ArenaRewardItem>();
                for (int j = 0; j < rewardItemJsonArray.length(); j++) {
                    final JSONObject rewardItemJson = rewardItemJsonArray.getJSONObject(j);
                    final ArenaRewardItem arenaRewardItem = new ArenaRewardItem();
                    arenaRewardItem.setId(rewardItemJson.getInt("id"));
                    arenaRewardItem.setAmount(rewardItemJson.getInt("amount"));
                    final ArenaRewardItemType rewardItemType = ArenaRewardItemType.valueOf(rewardItemJson.getString("type"));
                    arenaRewardItem.setType(rewardItemType);
                    if (rewardItemType == ArenaRewardItemType.Card) {
                        final JSONObject cardJson = rewardItemJson.getJSONObject("card");
                        final Card card = new Card();
                        card.setName(cardJson.getString("name"));
                        card.setAtk(cardJson.getInt("atk"));
                        card.setHp(cardJson.getInt("hp"));
                        card.setStar(cardJson.getInt("star"));
                        final String image = cardJson.getString("image");
                        if (image != null && !"".equals(image)) {
                            final String localImage = ImageUtils.getLocalString(image, activity);
                            card.setImage(localImage);
                            TextureFactory.getInstance().addCardResource(activity, localImage);
                        }
                        arenaRewardItem.setCard(card);
                    }
                    rewardItems.add(arenaRewardItem);
                }
                arenaReward.setRewardItems(rewardItems);
                arenaRewards.add(arenaReward);
            }
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
        return arenaRewards;
    }

    public static int addContinuousWin() {
        final String url = HttpUtils.HOST_URL + "/arena/acw";
        try {
            final JSONArray responseJsonArray = HttpUtils.getJSONArrayFromUrl(url);
            return responseJsonArray.getInt(0);
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
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