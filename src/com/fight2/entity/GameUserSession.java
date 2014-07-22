package com.fight2.entity;

import java.util.ArrayList;
import java.util.List;

public class GameUserSession {
    private final static GameUserSession INSTANCE = new GameUserSession();
    private String name;
    private final Card[][] parties;
    private final List<Card> cards = new ArrayList<Card>();

    private GameUserSession() {
        parties = new Card[3][4];
    }

    public Card[][] getParties() {
        return parties;
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

}
