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
import com.fight2.entity.ArenaReward.ArenaRewardType;
import com.fight2.entity.ArenaRewardItem;
import com.fight2.entity.ArenaRewardItem.ArenaRewardItemType;
import com.fight2.entity.Card;
import com.fight2.entity.Card.Race;
import com.fight2.entity.Guild;
import com.fight2.entity.User;
import com.fight2.entity.UserArenaInfo;
import com.fight2.entity.UserArenaRecord;
import com.fight2.entity.UserArenaRecord.UserArenaRecordStatus;
import com.fight2.entity.battle.BattleResult;

public class ArenaUtils {
    private static Arena selectedArena;
    private static int selectedArenaId = 0;

    public static Arena getSelectedArena() {
        return selectedArena;
    }

    public static void setSelectedArena(final Arena selectedArena) {
        ArenaUtils.selectedArena = selectedArena;
        setSelectedArenaId(selectedArena.getId());
    }

    public static int getSelectedArenaId() {
        return selectedArenaId;
    }

    private static void setSelectedArenaId(final int selectedArenaId) {
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
            userArenaInfo.setIssuedReward(responseJson.getInt("issuedReward"));
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
                        card.setRace(Race.valueOf(cardJson.getString("race")));
                        card.setImage(cardJson.getString("image"));
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
                arena.setGuildArena(jsonObject.getBoolean("guildArena"));
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

    public static BattleResult attack(final int attackPlayerId, final GameActivity activity) {
        final String url = HttpUtils.HOST_URL + "/arena/attack.action?id=" + selectedArenaId + "&attackId=" + attackPlayerId;
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            return BattleUtils.attack(responseJson);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkAttack() {
        final String url = HttpUtils.HOST_URL + "/arena/check-attack";
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final int status = responseJson.getInt("status");
            return status == 0;
        } catch (final ClientProtocolException e) {
            LogUtils.e(e);
        } catch (final Exception e) {
            LogUtils.e(e);
        }
        return false;
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