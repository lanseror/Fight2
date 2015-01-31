package com.fight2.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.SparseArray;

import com.fight2.GameActivity;
import com.fight2.entity.Card;
import com.fight2.entity.Card.Race;
import com.fight2.entity.CardTemplate;
import com.fight2.entity.ComboSkill;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.Party;
import com.fight2.entity.PartyInfo;

public class CardUtils {
    private final static SparseArray<CardTemplate> cardTemplates = new SparseArray<CardTemplate>();
    private final static SparseArray<Set<Card>> userCards = new SparseArray<Set<Card>>();
    private final static List<Card> evoCards = new ArrayList<Card>();

    public static void addCardTemplate(final CardTemplate cardTemplate) {
        cardTemplates.put(cardTemplate.getId(), cardTemplate);
    }

    public static void clearUserCard() {
        userCards.clear();
    }

    public static void refreshUserCards() {
        userCards.clear();
        final Collection<Card> cards = GameUserSession.getInstance().getCards();
        for (final Card card : cards) {
            addUserCard(card);
        }
        refreshEvoCards();
    }

    private static void addUserCard(final Card card) {
        final int templateId = card.getTemplateId();
        if (userCards.indexOfKey(templateId) < 0) {
            final Set<Card> cardSet = new LinkedHashSet<Card>();
            userCards.put(templateId, cardSet);
        }
        final Set<Card> cardSet = userCards.get(templateId);
        cardSet.add(card);
    }

    private static void refreshEvoCards() {
        evoCards.clear();
        for (int i = 0; i < userCards.size(); i++) {
            final Set<Card> cardSet = userCards.valueAt(i);
            if (cardSet.size() > 1) {
                final List<Card> evoCardsTemp = new ArrayList<Card>(cardSet.size());
                for (final Card card : cardSet) {
                    if (getMaxEvoTier(card) > card.getTier()) {
                        evoCardsTemp.add(card);
                    }
                }
                if (evoCardsTemp.size() > 1) {
                    evoCards.addAll(evoCardsTemp);
                }
            }
        }
    }

    public static SparseArray<Set<Card>> getUsercards() {
        return userCards;
    }

    public static Set<Card> getUsercardsByTemplateId(final int templateId) {
        return userCards.get(templateId);
    }

    public static List<Card> getEvocards() {
        return evoCards;
    }

    public static Card cardFromJson(final JSONObject cardJson) {
        final Card card = new Card();
        try {
            card.setId(cardJson.getInt("id"));
            card.setStar(cardJson.getInt("star"));
            card.setLevel(cardJson.getInt("level"));
            card.setTier(cardJson.getInt("tier"));
            card.setAtk(cardJson.getInt("atk"));
            card.setHp(cardJson.getInt("hp"));
            card.setBaseExp(cardJson.getInt("baseExp"));
            card.setExp(cardJson.getInt("exp"));
            card.setAmount(cardJson.getInt("amount"));
            if (cardJson.has("avatar")) {
                card.setAvatar(cardJson.getString("avatar"));
            }
            card.setImage(cardJson.getString("image"));
            card.setName(cardJson.getString("name"));
            card.setSkill(cardJson.optString("skill"));
            card.setSkillEffect(cardJson.optString("skillEffect"));
            if (cardJson.has("cardTemplate")) {
                final JSONObject cardTemplateJson = cardJson.getJSONObject("cardTemplate");
                card.setTemplateId(cardTemplateJson.getInt("id"));
            }
            card.setRace(Race.valueOf(cardJson.getString("race")));
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
        return card;
    }

    public static boolean saveParties(final GameActivity activity) {
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
            updateUserPartyCombo(activity);
            return true;

        } catch (final ClientProtocolException e) {
            LogUtils.e(e);
        } catch (final Exception e) {
            LogUtils.e(e);
        }
        return false;
    }

    public static void updateUserPartyCombo(final GameActivity activity) {
        final GameUserSession session = GameUserSession.getInstance();
        final Party[] parties = session.getPartyInfo().getParties();
        final String partyUrl = HttpUtils.HOST_URL + "/party/user-parties.action?id=" + session.getId();
        try {
            final JSONObject partyInfoJson = HttpUtils.getJSONFromUrl(partyUrl);
            final JSONArray partyJsonArray = partyInfoJson.getJSONArray("parties");
            for (int partyIndex = 0; partyIndex < partyJsonArray.length(); partyIndex++) {
                final JSONObject partyJson = partyJsonArray.getJSONObject(partyIndex);
                final List<ComboSkill> comboSkills = new ArrayList<ComboSkill>();
                if (partyJson.has("comboSkills")) {
                    final JSONArray comboSkillJSONArray = partyJson.getJSONArray("comboSkills");
                    for (int skillIndex = 0; skillIndex < comboSkillJSONArray.length(); skillIndex++) {
                        final JSONObject comboSkilJson = comboSkillJSONArray.getJSONObject(skillIndex);
                        final ComboSkill comboSkill = new ComboSkill();
                        comboSkill.setId(comboSkilJson.getInt("id"));
                        comboSkill.setName(comboSkilJson.getString("name"));
                        final String icon = comboSkilJson.getString("icon");
                        comboSkill.setIcon(ImageUtils.getLocalString(icon, activity));
                        comboSkills.add(comboSkill);
                    }
                }
                parties[partyIndex].setComboSkills(comboSkills);
            }
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static Card summon(final GameActivity activity, final int type) {
        final String url = HttpUtils.HOST_URL + "/card/summon?type=" + type;
        final GameUserSession session = GameUserSession.getInstance();
        final Collection<Card> cards = session.getCards();
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final int status = responseJson.getInt("status");
            if (status == 0) {
                final JSONObject cardJson = responseJson.getJSONObject("card");
                final Card card = CardUtils.cardFromJson(cardJson);
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
                parties[partyIndex] = getPartyFromJson(activity, partyJson);
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

    public static Party getPartyFromJson(final GameActivity activity, final JSONObject partyJson) {
        try {
            final Party party = new Party();
            party.setId(partyJson.getInt("id"));
            party.setAtk(partyJson.getInt("atk"));
            party.setHp(partyJson.getInt("hp"));
            party.setPartyNumber(partyJson.getInt("partyNumber"));
            final List<ComboSkill> comboSkills = new ArrayList<ComboSkill>();
            if (partyJson.has("comboSkills")) {
                final JSONArray comboSkillJSONArray = partyJson.getJSONArray("comboSkills");
                for (int skillIndex = 0; skillIndex < comboSkillJSONArray.length(); skillIndex++) {
                    final JSONObject comboSkilJson = comboSkillJSONArray.getJSONObject(skillIndex);
                    final ComboSkill comboSkill = new ComboSkill();
                    comboSkill.setId(comboSkilJson.getInt("id"));
                    comboSkill.setName(comboSkilJson.getString("name"));
                    final String icon = comboSkilJson.getString("icon");
                    comboSkill.setIcon(ImageUtils.getLocalString(icon, activity));
                    comboSkills.add(comboSkill);
                }
            }
            party.setComboSkills(comboSkills);
            if (partyJson.has("partyGrids")) {
                final JSONArray partyGridJsonArray = partyJson.getJSONArray("partyGrids");
                final Card[] partyCards = new Card[partyGridJsonArray.length()];
                party.setCards(partyCards);
                for (int partyCardIndex = 0; partyCardIndex < partyGridJsonArray.length(); partyCardIndex++) {
                    final JSONObject partyGridJson = partyGridJsonArray.getJSONObject(partyCardIndex);
                    final JSONObject cardJson = partyGridJson.optJSONObject("card");
                    if (cardJson != null) {
                        final Card card = CardUtils.cardFromJson(cardJson);
                        partyCards[partyCardIndex] = card;
                    }
                }
            }
            return party;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static int getBaseExp(final Card card) {
        if (card.getBaseExp() == 0) {
            return card.getStar() * 100;
        } else {
            return card.getBaseExp();
        }
    }

    public static int getMaxLevel(final Card card) {
        return card.getStar() * 10 + (card.getTier() - 1) * 10;
    }

    public static int getMaxEvoTier(final Card card) {
        return (card.getStar() + 1) / 2 + 1;
    }

    public static void mockUpgrade(final Card card) {
        final int exp = card.getExp();
        final int currentLevel = card.getLevel();
        final int maxLevel = getMaxLevel(card);

        int level = 1;
        int levelExp = 0;
        for (int i = 1; i < maxLevel; i++) {
            levelExp += (int) (100 * Math.pow(1.065, i));
            if (exp >= levelExp) {
                level = i + 1;
            } else {
                break;
            }
        }
        card.setLevel(level);
        if (level == maxLevel) {
            card.setExp(levelExp);
        }

        if (currentLevel != card.getLevel()) {
            final int addedLevel = card.getLevel() - currentLevel;
            final int currentHp = card.getHp();
            final int currentAtk = card.getAtk();
            final int upgradeHp = (int) (card.getHp() * Math.pow(1.015, addedLevel));
            final int upgradeAtk = upgradeHp * currentAtk / currentHp;
            card.setHp(upgradeHp);
            card.setAtk(upgradeAtk);
        }

    }

    public static int getEvoPercent(final Card card) {
        final int cardMaxLevel = getMaxLevel(card);
        final int cardPercent = card.getLevel() == cardMaxLevel ? 10 : 5;
        return cardPercent;
    }

    public static Card mockEvolution(final Card card1, final Card card2) {
        final int templateId = card1.getTemplateId();
        final CardTemplate cardTemplate = cardTemplates.get(templateId);
        final Card higherTierCard = card1.getTier() > card2.getTier() ? card1 : card2;
        final int baseHp = (int) (cardTemplate.getHp() * Math.pow(1.2, higherTierCard.getTier()));
        final int card1MaxLevel = getMaxLevel(card1);
        final double card1Rate = card1.getLevel() == card1MaxLevel ? 0.1 : 0.05;
        final int card1AddHp = (int) (card1.getHp() * card1Rate);
        final int card2MaxLevel = getMaxLevel(card2);
        final double card2Rate = card2.getLevel() == card2MaxLevel ? 0.1 : 0.05;
        final int card2AddHp = (int) (card2.getHp() * card2Rate);

        final int evoHp = baseHp + card1AddHp + card2AddHp;

        final BigDecimal templateAtk = BigDecimal.valueOf(cardTemplate.getAtk());
        final BigDecimal templateHp = BigDecimal.valueOf(cardTemplate.getHp());
        final BigDecimal evoHpDecimal = BigDecimal.valueOf(evoHp);
        final int evoAtk = templateAtk.divide(templateHp, 6, RoundingMode.HALF_UP).multiply(evoHpDecimal).intValue();

        final Card evoCard = new Card();
        evoCard.setHp(evoHp);
        evoCard.setAtk(evoAtk);
        return evoCard;
    }

    public static boolean upgrade(final JSONArray cardIdsJson, final Card mainCard) {
        final String url = HttpUtils.HOST_URL + "/card/upgrade";

        try {
            final String responseJsonStr = HttpUtils.postJSONString(url, cardIdsJson.toString());
            final JSONObject responseJson = new JSONObject(responseJsonStr);
            final int status = responseJson.getInt("status");
            if (status == 0) {
                final JSONObject cardJson = responseJson.getJSONObject("card");
                mainCard.setAtk(cardJson.getInt("atk"));
                mainCard.setHp(cardJson.getInt("hp"));
                mainCard.setLevel(cardJson.getInt("level"));
                mainCard.setBaseExp(cardJson.getInt("baseExp"));
                mainCard.setExp(cardJson.getInt("exp"));
            }

            return status == 0;
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ComboSkill> getCardComboSkills(final Card card, final GameActivity activity) {
        final String url = HttpUtils.HOST_URL + "/card/combos?id=" + card.getTemplateId();
        final List<ComboSkill> skills = new ArrayList<ComboSkill>();
        try {
            final JSONArray comboSkilJSONArray = HttpUtils.getJSONArrayFromUrl(url);
            for (int skillIndex = 0; skillIndex < comboSkilJSONArray.length(); skillIndex++) {
                final JSONObject comboSkilJson = comboSkilJSONArray.getJSONObject(skillIndex);
                final ComboSkill comboSkill = new ComboSkill();
                comboSkill.setId(comboSkilJson.getInt("id"));
                comboSkill.setName(comboSkilJson.getString("name"));
                final String icon = comboSkilJson.getString("icon");
                comboSkill.setIcon(ImageUtils.getLocalString(icon, activity));
                final JSONArray comboCardJSONArray = comboSkilJson.getJSONArray("cards");
                final List<Card> comboCards = new ArrayList<Card>(4);
                for (int cardIndex = 0; cardIndex < comboCardJSONArray.length(); cardIndex++) {
                    final JSONObject comboCardJson = comboCardJSONArray.getJSONObject(cardIndex);
                    final Card comboCard = new Card();
                    comboCard.setId(comboCardJson.getInt("id"));
                    comboCard.setAvatar(comboCardJson.getString("avatar"));
                    comboCards.add(comboCard);
                }
                comboSkill.setCards(comboCards);

                skills.add(comboSkill);
            }
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
        return skills;

    }

    public static int evolution(final JSONArray cardIdsJson, final Card[] evoCards) {
        final String url = HttpUtils.HOST_URL + "/card/evo";
        try {
            int result = -1;
            final String responseJsonStr = HttpUtils.postJSONString(url, cardIdsJson.toString());
            final JSONObject responseJson = new JSONObject(responseJsonStr);
            final int status = responseJson.getInt("status");
            if (status == 0) {
                final JSONObject cardJson = responseJson.getJSONObject("card");
                final int id = cardJson.getInt("id");
                for (int i = 0; i < evoCards.length; i++) {
                    final Card card = evoCards[i];
                    if (card.getId() == id) {
                        card.setAtk(cardJson.getInt("atk"));
                        card.setHp(cardJson.getInt("hp"));
                        card.setLevel(cardJson.getInt("level"));
                        card.setTier(cardJson.getInt("tier"));
                        card.setAvatar(cardJson.getString("avatar"));
                        card.setAvatarLoaded(false);
                        card.setImage(cardJson.getString("image"));
                        card.setImageLoaded(false);
                        card.setBaseExp(cardJson.getInt("baseExp"));
                        card.setExp(cardJson.getInt("exp"));
                        result = i;
                        break;
                    }
                }
            }
            return result;
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

    }
}