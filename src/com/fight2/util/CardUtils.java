package com.fight2.util;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fight2.GameActivity;
import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.Party;
import com.fight2.entity.PartyInfo;
import com.fight2.entity.Card.Race;

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
            LogUtils.e(e);
        } catch (final Exception e) {
            LogUtils.e(e);
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
                final Card card = new Card();
                card.setId(cardJson.getInt("id"));
                card.setAtk(cardJson.getInt("atk"));
                card.setAvatar(cardJson.getString("avatar"));
                card.setHp(cardJson.getInt("hp"));
                card.setStar(cardJson.getInt("star"));
                card.setLevel(cardJson.getInt("level"));
                card.setImage(cardJson.getString("image"));
                card.setName(cardJson.getString("name"));
                card.setSkill(cardJson.optString("skill"));
                card.setRace(Race.valueOf(cardJson.getString("race")));
                final JSONObject cardTemplateJson = cardJson.getJSONObject("cardTemplate");
                card.setTemplateId(cardTemplateJson.getInt("id"));
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

    public static PartyInfo getPartyByUserId(final GameActivity activity, final int userId) {
        final String partyUrl = HttpUtils.HOST_URL + "/party/user-parties.action?id=" + userId;
        try {
            final JSONObject partyInfoJson = HttpUtils.getJSONFromUrl(partyUrl);
            final PartyInfo partyInfo = new PartyInfo();
            partyInfo.setId(partyInfoJson.getInt("id"));
            partyInfo.setAtk(partyInfoJson.getInt("atk"));
            partyInfo.setHp(partyInfoJson.getInt("hp"));
            final JSONArray partyJsonArray = partyInfoJson.getJSONArray("parties");
            final Party[] parties = new Party[partyJsonArray.length()];
            partyInfo.setParties(parties);
            for (int partyIndex = 0; partyIndex < partyJsonArray.length(); partyIndex++) {
                final JSONObject partyJson = partyJsonArray.getJSONObject(partyIndex);
                final Party party = new Party();
                party.setId(partyJson.getInt("id"));
                party.setAtk(partyJson.getInt("atk"));
                party.setHp(partyJson.getInt("hp"));
                party.setPartyNumber(partyJson.getInt("partyNumber"));
                parties[partyIndex] = party;
                final JSONArray partyGridJsonArray = partyJson.getJSONArray("partyGrids");
                final Card[] partyCards = new Card[partyGridJsonArray.length()];
                party.setCards(partyCards);
                for (int partyCardIndex = 0; partyCardIndex < partyGridJsonArray.length(); partyCardIndex++) {
                    final JSONObject partyGridJson = partyGridJsonArray.getJSONObject(partyCardIndex);
                    final JSONObject cardJson = partyGridJson.optJSONObject("card");
                    if (cardJson != null) {
                        final Card card = new Card();
                        card.setId(cardJson.getInt("id"));
                        card.setAtk(cardJson.getInt("atk"));
                        card.setHp(cardJson.getInt("hp"));
                        card.setStar(cardJson.getInt("star"));
                        card.setTier(cardJson.getInt("tier"));
                        card.setLevel(cardJson.getInt("level"));
                        card.setRace(Race.valueOf(cardJson.getString("race")));
                        final String avatar = cardJson.optString("avatar");
                        if (avatar != null && !"".equals(avatar)) {
                            final String localAvatar = ImageUtils.getLocalString(avatar, activity);
                            card.setAvatar(localAvatar);
                            card.setAvatarLoaded(true);
                            TextureFactory.getInstance().addCardResource(activity, localAvatar);
                        }
                        card.setImage(cardJson.getString("image"));
                        partyCards[partyCardIndex] = card;
                    }
                }
            }
            return partyInfo;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }
}