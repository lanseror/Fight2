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
import com.fight2.entity.Party;
import com.fight2.entity.PartyInfo;

public class CardUtils {

    public static boolean saveParties() {
        final String url = HttpUtils.HOST_URL + "/party/edit";
        final GameUserSession session = GameUserSession.getInstance();
        final PartyInfo partyInfo = session.getPartyInfo();
        final JSONArray partyJson = new JSONArray();
        final Party[] parties = partyInfo.getParties();
        for (final Party party : parties) {
            final JSONArray cardJson = new JSONArray();
            for (final Card card : party.getCards()) {
                if (card != null) {
                    cardJson.put(card.getId());
                } else {
                    cardJson.put(-1);
                }
            }
            partyJson.put(cardJson);
        }

        try {

            final String responseJsonStr = HttpUtils.postJSONString(url, partyJson.toString());
            final JSONObject responseJson = new JSONObject(responseJsonStr);
            partyInfo.setAtk(responseJson.getInt("atk"));
            partyInfo.setHp(responseJson.getInt("hp"));
            final JSONArray responsePartyJsonArray = responseJson.getJSONArray("parties");
            for (int partyIndex = 0; partyIndex < responsePartyJsonArray.length(); partyIndex++) {
                final JSONObject responsePartyJson = responsePartyJsonArray.getJSONObject(partyIndex);
                final Party party = parties[partyIndex];
                party.setAtk(responsePartyJson.getInt("atk"));
                party.setHp(responsePartyJson.getInt("hp"));
            }
            return true;

        } catch (final ClientProtocolException e) {
            Debug.e(e);
        } catch (final Exception e) {
            Debug.e(e);
        }
        return false;
    }

    public static Card summon(final GameActivity activity) {
        final String url = HttpUtils.HOST_URL + "/card/summon";
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