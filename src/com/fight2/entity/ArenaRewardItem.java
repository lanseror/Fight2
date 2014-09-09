package com.fight2.entity;

public class ArenaRewardItem {
    private int id;
    private ArenaRewardItemType type;
    private int amount;
    private Card card;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public ArenaRewardItemType getType() {
        return type;
    }

    public void setType(final ArenaRewardItemType type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(final int amount) {
        this.amount = amount;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(final Card card) {
        this.card = card;
    }

    public static enum ArenaRewardItemType {
        ArenaTicket,
        Stamina,
        Card
    }

}
