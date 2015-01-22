package com.fight2.entity;

public class RewardItem {
    private int id;
    private RewardItemType type;
    private int amount;
    private Card card;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public RewardItemType getType() {
        return type;
    }

    public void setType(final RewardItemType type) {
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

    public static enum RewardItemType {
        ArenaTicket,
        Stamina,
        Card,
        GuildContribution;
    }

}
