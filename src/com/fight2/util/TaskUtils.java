package com.fight2.util;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.fight2.entity.QuestTask;
import com.fight2.entity.QuestTask.UserTaskStatus;

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
}