package com.fight2.util;

import java.io.IOException;
import java.util.List;

import org.andengine.util.debug.Debug;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fight2.GameActivity;
import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;

public class CardUtils {
    public static final String HOST_URL = "http://192.168.1.178:8080/Fight2Server";

    public static boolean saveParties() {
        final String url = HOST_URL + "/party/edit";
        final GameUserSession session = GameUserSession.getInstance();
        final Card[][] parties = session.getParties();
        final JSONArray partyJson = new JSONArray();
        for (final Card[] party : parties) {
            final JSONArray cardJson = new JSONArray();
            for (final Card card : party) {
                if (card != null) {
                    cardJson.put(card.getId());
                } else {
                    cardJson.put(-1);
                }
            }
            partyJson.put(cardJson);
        }

        try {
            return HttpUtils.postJSONString(url, partyJson.toString());
        } catch (final ClientProtocolException e) {
            Debug.e(e);
        } catch (final IOException e) {
            Debug.e(e);
        }
        return false;
    }

    public static Card summon(final GameActivity activity) {
        final String url = HOST_URL + "/card/summon";
        final GameUserSession session = GameUserSession.getInstance();
        final List<Card> cards = session.getCards();
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final int status = responseJson.getInt("status");
            if (status == 0) {
                final JSONObject cardJson = responseJson.getJSONObject("card");
                final String avatar = ImageUtils.getLocalString(cardJson.getString("avatar"), activity);
                final String image = ImageUtils.getLocalString(cardJson.getString("image"), activity);
                final TextureFactory textureFactory = TextureFactory.getInstance();
                textureFactory.addCardResource(activity, avatar);
                textureFactory.addCardResource(activity, image);
                final Card card = new Card();
                card.setId(cardJson.getInt("id"));
                card.setAtk(cardJson.getInt("atk"));
                card.setAvatar(avatar);
                card.setHp(cardJson.getInt("hp"));
                card.setImage(image);
                card.setName(cardJson.getString("name"));
                card.setSkill(cardJson.optString("skill"));
                cards.add(card);
                return card;
            } else {
                return null;
            }
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
    }
}