package com.fight2.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fight2.entity.Guild;
import com.fight2.entity.User;

public class GuildUtils {

    public static boolean applyGuild(final String name) {
        final String url = HttpUtils.HOST_URL + "/guild/apply";
        final JSONObject infoJson = new JSONObject();
        try {
            infoJson.put("name", URLEncoder.encode(name, "UTF-8"));
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        try {
            final String responseJsonStr = HttpUtils.postJSONString(url, infoJson.toString());
            final JSONObject responseJson = new JSONObject(responseJsonStr);
            final int status = responseJson.getInt("status");
            return status == 0;
        } catch (final ClientProtocolException e) {
            LogUtils.e(e);
        } catch (final Exception e) {
            LogUtils.e(e);
        }
        return false;
    }

    public static Guild getUserGuild() {
        final String url = HttpUtils.HOST_URL + "/guild/get-user-guild";
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final int status = responseJson.getInt("status");
            if (status < 0) {
                return null;
            }
            final JSONObject guildJson = responseJson.getJSONObject("guild");
            final Guild guild = new Guild();
            guild.setId(guildJson.getInt("id"));
            guild.setName(guildJson.getString("name"));
            guild.setNotice(guildJson.optString("notice"));
            guild.setQq(guildJson.optString("qq"));
            final User president = new User();
            final JSONObject presidentJson = guildJson.getJSONObject("president");
            president.setId(presidentJson.getInt("id"));
            president.setName(presidentJson.getString("name"));
            guild.setPresident(president);
            return guild;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean joinGuild(final int id) {
        final String url = HttpUtils.HOST_URL + "/guild/join?id=" + id;
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final int status = responseJson.getInt("status");
            return status == 0;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean quitGuild() {
        final String url = HttpUtils.HOST_URL + "/guild/quit";
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final int status = responseJson.getInt("status");
            return status == 0;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static List<User> getMembers(final int id) {
        final String url = HttpUtils.HOST_URL + "/guild/members?id=" + id;
        try {
            final List<User> members = new ArrayList<User>();
            final JSONArray memberJsonArray = HttpUtils.getJSONArrayFromUrl(url);
            for (int i = 0; i < memberJsonArray.length(); i++) {
                final JSONObject memberJson = memberJsonArray.getJSONObject(i);
                final User member = new User();
                member.setId(memberJson.getInt("id"));
                member.setName(memberJson.getString("name"));
                members.add(member);
            }
            return members;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static List<Guild> getTopGuilds() {
        final String url = HttpUtils.HOST_URL + "/guild/list-tops";
        try {
            final List<Guild> guilds = new ArrayList<Guild>();
            final JSONArray guildJsonArray = HttpUtils.getJSONArrayFromUrl(url);
            for (int i = 0; i < guildJsonArray.length(); i++) {
                final JSONObject guildJson = guildJsonArray.getJSONObject(i);
                final Guild guild = new Guild();
                guild.setId(guildJson.getInt("id"));
                guild.setName(guildJson.getString("name"));
                final User president = new User();
                final JSONObject presidentJson = guildJson.getJSONObject("president");
                president.setId(presidentJson.getInt("id"));
                president.setName(presidentJson.getString("name"));
                guild.setPresident(president);
                guilds.add(guild);
            }
            return guilds;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean editGuild(final Guild guild) {
        final String url = HttpUtils.HOST_URL + "/guild/president-edit";
        final JSONObject infoJson = new JSONObject();
        try {
            infoJson.put("id", guild.getId());
            infoJson.put("qq", guild.getQq());
            infoJson.put("notice", URLEncoder.encode(guild.getNotice(), "UTF-8"));
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        try {
            final String responseJsonStr = HttpUtils.postJSONString(url, infoJson.toString());
            final JSONObject responseJson = new JSONObject(responseJsonStr);
            final int status = responseJson.getInt("status");
            return status == 0;
        } catch (final ClientProtocolException e) {
            LogUtils.e(e);
        } catch (final Exception e) {
            LogUtils.e(e);
        }
        return false;
    }

    public static boolean vote(final int candidateId) {
        final String url = HttpUtils.HOST_URL + "/guild/vote?id=" + candidateId;
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
}