package com.fight2.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;

public class AccountUtils {
    private static final String INSTALLATION = "INSTALLATION";
    private static final String HOST_URL = "http://192.168.1.178:8080";

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

    public static void install(final Context context) throws IOException {
        final File installation = getInstallationFile(context);
        final FileOutputStream out = new FileOutputStream(installation);
        final String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }

    public static void registerAndLogin(final String installUUID) throws IOException {
        final String webUrl = HOST_URL + "/user/register.action?installUUID=" + installUUID;

        try {
            final JSONObject jsonObj = HttpUtils.getJSONFromUrl(webUrl);
            GameUserSession.getInstance().setName(jsonObj.getString("username"));
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static void login(final String installUUID) throws IOException {
        final String webUrl = HOST_URL + "/user/login.action?installUUID=" + installUUID;

        try {
            final JSONObject jsonObj = HttpUtils.getJSONFromUrl(webUrl);
            final GameUserSession session = GameUserSession.getInstance();
            session.setName(jsonObj.getString("username"));
            final List<Card> cards = session.getCards();
            cards.clear();

        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }

}