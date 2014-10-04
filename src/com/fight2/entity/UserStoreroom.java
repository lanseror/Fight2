package com.fight2.entity;

import java.util.List;

public class UserStoreroom {
    private int id;
    private int stamina;
    private int ticket;
    private int coin;
    private List<Card> cards;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(final int stamina) {
        this.stamina = stamina;
    }

    public int getTicket() {
        return ticket;
    }

    public void setTicket(final int ticket) {
        this.ticket = ticket;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(final int coin) {
        this.coin = coin;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(final List<Card> cards) {
        this.cards = cards;
    }

}
