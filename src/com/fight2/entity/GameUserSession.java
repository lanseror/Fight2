package com.fight2.entity;

import java.util.ArrayList;
import java.util.List;

public class GameUserSession {
    private final static GameUserSession INSTANCE = new GameUserSession();
    private String name;
    private final List<List<Card>> parties;

    private GameUserSession() {
        parties = new ArrayList<List<Card>>(3);
        for (int i = 0; i < 3; i++) {
            final List<Card> party = new ArrayList<Card>(4);
            parties.add(party);
        }
    }

    public List<List<Card>> getParties() {
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

}
