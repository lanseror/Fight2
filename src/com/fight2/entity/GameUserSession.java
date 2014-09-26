package com.fight2.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameUserSession {
    private final static GameUserSession INSTANCE = new GameUserSession();
    private String name;
    private final List<Card> cards;
    private PartyInfo partyInfo;
    private final Set<Integer> inPartyCards = new HashSet<Integer>();

    private GameUserSession() {
        cards = new ArrayList<Card>();
    }

    public static GameUserSession getInstance() {
        return INSTANCE;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<Card> getCards() {
        return cards;
    }

    public PartyInfo getPartyInfo() {
        return partyInfo;
    }

    public void setPartyInfo(final PartyInfo partyInfo) {
        this.partyInfo = partyInfo;
    }

    public Set<Integer> getInPartyCards() {
        return inPartyCards;
    }

}
