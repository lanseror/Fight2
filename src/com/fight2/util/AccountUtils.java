package com.fight2.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.fight2.GameActivity;
import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.Party;
import com.fight2.entity.PartyInfo;

public class AccountUtils {
    private static final String INSTALLATION = "INSTALLATION";

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
        final String webUrl = HttpUtils.HOST_URL + "/user/register.action?installUUID=" + id;

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

    public static void login(final String installUUID, final GameActivity activity) throws IOException {
        final String loginUrl = HttpUtils.HOST_URL + "/user/login.action?installUUID=" + installUUID;
        final String cardUrl = HttpUtils.HOST_URL + "/card/my-cards";
        final String partyUrl = HttpUtils.HOST_URL + "/party/my-parties";

        try {
            final JSONObject loginJson = HttpUtils.getJSONFromUrl(loginUrl);
            final GameUserSession session = GameUserSession.getInstance();
            session.setId(loginJson.getInt("id"));
            session.setName(loginJson.getString("name"));
            final Set<Integer> inPartyCards = session.getInPartyCards();

            // Get cards.
            final List<Card> cards = session.getCards();
            cards.clear();

            final JSONArray cardJsonArray = HttpUtils.getJSONArrayFromUrl(cardUrl);
            for (int cardIndex = 0; cardIndex < cardJsonArray.length(); cardIndex++) {
                final JSONObject cardJson = cardJsonArray.getJSONObject(cardIndex);
                final String avatar = ImageUtils.getLocalString(cardJson.getString("avatar"), activity);
                final String image = ImageUtils.getLocalString(cardJson.getString("image"), activity);
                final Card card = new Card();
                card.setId(cardJson.getInt("id"));
                card.setAtk(cardJson.getInt("atk"));
                card.setAvatar(avatar);
                card.setHp(cardJson.getInt("hp"));
                card.setImage(image);
                card.setName(cardJson.getString("name"));
                card.setSkill(cardJson.optString("skill"));
                final JSONObject cardTemplateJson = cardJson.getJSONObject("cardTemplate");
                card.setTemplateId(cardTemplateJson.getInt("id"));
                cards.add(card);
            }
            TextureFactory.getInstance().loadCardsResource(activity);

            final JSONObject partyInfoJson = HttpUtils.getJSONFromUrl(partyUrl);
            final PartyInfo partyInfo = new PartyInfo();
            partyInfo.setId(partyInfoJson.getInt("id"));
            partyInfo.setAtk(partyInfoJson.getInt("atk"));
            partyInfo.setHp(partyInfoJson.getInt("hp"));
            final JSONArray partyJsonArray = partyInfoJson.getJSONArray("parties");
            final Party[] parties = new Party[partyJsonArray.length()];
            partyInfo.setParties(parties);
            session.setPartyInfo(partyInfo);
            for (int partyIndex = 0; partyIndex < partyJsonArray.length(); partyIndex++) {
                final JSONObject partyJson = partyJsonArray.getJSONObject(partyIndex);
                final Party party = new Party();
                party.setId(partyJson.getInt("id"));
                party.setAtk(partyJson.getInt("atk"));
                party.setHp(partyJson.getInt("hp"));
                party.setPartyNumber(partyJson.getInt("partyNumber"));
                parties[partyIndex] = party;

                final JSONArray partyCardJsonArray = partyJson.getJSONArray("cards");
                final Card[] partyCards = new Card[partyCardJsonArray.length()];
                party.setCards(partyCards);
                for (int partyCardIndex = 0; partyCardIndex < partyCardJsonArray.length(); partyCardIndex++) {
                    final int partyCardId = partyCardJsonArray.getInt(partyCardIndex);
                    if (partyCardId == -1) {
                        partyCards[partyCardIndex] = null;
                    } else {
                        final Iterator<Card> it = cards.iterator();
                        while (it.hasNext()) {
                            final Card card = it.next();
                            if (partyCardId == card.getId()) {
                                it.remove();
                                partyCards[partyCardIndex] = card;
                                inPartyCards.add(card.getTemplateId());
                            }
                        }
                    }
                }
            }

        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean saveUserInfo() {
        final String url = HttpUtils.HOST_URL + "/user/save-user-info";
        final GameUserSession session = GameUserSession.getInstance();
        final JSONObject infoJson = new JSONObject();
        try {
            infoJson.put("name", URLEncoder.encode(session.getName(), "UTF-8"));
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
}