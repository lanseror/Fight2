package com.fight2.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;

public class AccountUtils {
    private static final String INSTALLATION = "INSTALLATION";
    private static final String HOST_URL = "http://192.168.1.178:8080/Fight2Server";

    private static File getInstallationFile(final Context context) {
        return new File(context.getFilesDir(), INSTALLATION);
    }

    public static boolean isInstalled(final Context context) {
        final File installation = getInstallationFile(context);
        return installation.exists();
    }

    public static String readInstallUUID(final Context context) throws IOException {
        final File installation = getInstallationFile(context);
        final RandomAccessFile f = new RandomAccessFile(installation, "r");
        final byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    public static String installAndRegister(final Context context) {
        final String id = UUID.randomUUID().toString();
        final String webUrl = HOST_URL + "/user/register.action?installUUID=" + id;

        try {
            final JSONObject jsonObj = HttpUtils.getJSONFromUrl(webUrl);
            GameUserSession.getInstance().setName(jsonObj.getString("name"));
            final File installation = getInstallationFile(context);
            final FileOutputStream out = new FileOutputStream(installation);
            out.write(id.getBytes());
            out.close();
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return id;
    }

    public static void register(final String installUUID) throws IOException {

    }

    public static void login(final String installUUID) throws IOException {
        final String loginUrl = HOST_URL + "/user/login.action?installUUID=" + installUUID;
        final String partyUrl = HOST_URL + "/party/my-parties";

        try {
            final JSONObject loginJson = HttpUtils.getJSONFromUrl(loginUrl);
            final GameUserSession session = GameUserSession.getInstance();
            session.setName(loginJson.getString("name"));
            final List<Card> cards = session.getCards();
            cards.clear();
            final Card[][] parties = session.getParties();
            final JSONArray partyJsonArray = HttpUtils.getJSONArrayFromUrl(partyUrl);
            for (int partyIndex = 0; partyIndex < partyJsonArray.length(); partyIndex++) {
                final Card[] party = parties[partyIndex];
                final JSONArray cardJsonArray = partyJsonArray.getJSONArray(partyIndex);
                for (int cardIndex = 0; cardIndex < cardJsonArray.length(); cardIndex++) {
                    final JSONObject cardJson = cardJsonArray.optJSONObject(cardIndex);
                    System.out.println(cardJson);
                }
            }

        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }
}