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
import com.fight2.entity.QuestTask;
import com.fight2.entity.QuestTask.UserTaskStatus;
import com.fight2.entity.Reward;
import com.fight2.entity.RewardItem;
import com.fight2.entity.RewardItem.RewardItemType;
import com.fight2.entity.battle.BattleResult;

public class TaskUtils {
    private static QuestTask TASK;

    public static void refresh() {
        final String url = HttpUtils.HOST_URL + "/msg/get";
        try {
            final JSONObject userTaskJson = HttpUtils.getJSONFromUrl(url);
            final JSONObject taskJson = userTaskJson.getJSONObject("task");
            final QuestTask task = new QuestTask();
            task.setId(taskJson.getInt("id"));
            task.setTitle(taskJson.getString("title"));
            task.setDialog(taskJson.getString("dialog"));
            task.setTips(taskJson.getString("tips"));
            task.setX(taskJson.getInt("x"));
            task.setY(taskJson.getInt("y"));
            task.setStatus(UserTaskStatus.valueOf(userTaskJson.getString("status")));
            TASK = task;
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static QuestTask getTask() {
        if (TASK == null) {
            refresh();
        }
        return TASK;
    }

    public static boolean accept() {
        final String url = HttpUtils.HOST_URL + "/task/accept";
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

    public static BattleResult attack(final GameActivity activity) {
        final String url = HttpUtils.HOST_URL + "/task/attack";
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

    public static List<Reward> getTaskReward(final GameActivity activity, final int taskId) {
        final List<Reward> rewards = new ArrayList<Reward>();
        final String url = HttpUtils.HOST_URL + "/task-reward/list-json?taskId=" + taskId;
        try {
            final JSONArray responseJsonArray = HttpUtils.getJSONArrayFromUrl(url);
            for (int i = 0; i < responseJsonArray.length(); i++) {
                final JSONObject responseJson = responseJsonArray.getJSONObject(i);
                final Reward reward = new Reward();
                reward.setId(responseJson.getInt("id"));
                final JSONArray rewardItemJsonArray = responseJson.getJSONArray("rewardItems");
                final List<RewardItem> rewardItems = new ArrayList<RewardItem>();
                for (int j = 0; j < rewardItemJsonArray.length(); j++) {
                    final JSONObject rewardItemJson = rewardItemJsonArray.getJSONObject(j);
                    final RewardItem arenaRewardItem = new RewardItem();
                    arenaRewardItem.setId(rewardItemJson.getInt("id"));
                    arenaRewardItem.setAmount(rewardItemJson.getInt("amount"));
                    final RewardItemType rewardItemType = RewardItemType.valueOf(rewardItemJson.getString("type"));
                    arenaRewardItem.setType(rewardItemType);
                    if (rewardItemType == RewardItemType.Card) {
                        final JSONObject cardJson = rewardItemJson.getJSONObject("card");
                        final Card card = CardUtils.cardFromJson(cardJson);
                        arenaRewardItem.setCard(card);
                    }
                    rewardItems.add(arenaRewardItem);
                }
                reward.setRewardItems(rewardItems);
                rewards.add(reward);
            }
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
        return rewards;
    }
}