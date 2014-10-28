package com.fight2.entity;

public class QuestResult {
    private int status;
    private int treasureIndex;
    private TileItem item;
    private Card card;

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public int getTreasureIndex() {
        return treasureIndex;
    }

    public void setTreasureIndex(final int treasureIndex) {
        this.treasureIndex = treasureIndex;
    }

    public TileItem getItem() {
        return item;
    }

    public void setItem(final TileItem item) {
        this.item = item;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(final Card card) {
        this.card = card;
    }

    public enum TileItem {
        Ticket,
        Stamina,
        Card
    }
}
