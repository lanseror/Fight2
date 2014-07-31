package com.fight2.entity;

public class Party {
    private int id;

    private int partyNumber;

    private int hp;

    private int atk;

    private Card[] cards;

    public int getPartyNumber() {
        return partyNumber;
    }

    public void setPartyNumber(final int partyNumber) {
        this.partyNumber = partyNumber;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(final int hp) {
        this.hp = hp;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(final int atk) {
        this.atk = atk;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public Card[] getCards() {
        return cards;
    }

    public void setCards(final Card[] cards) {
        this.cards = cards;
    }

}
