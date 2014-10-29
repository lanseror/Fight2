package com.fight2.entity;

public class QuestResult {
    private int status;
    private int treasureIndex;
    private TileItem item;
    private Card card;
    private boolean treasureUpdated;
    private QuestTreasureData questTreasureData;

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

    public boolean isTreasureUpdated() {
        return treasureUpdated;
    }

    public void setTreasureUpdated(final boolean treasureUpdated) {
        this.treasureUpdated = treasureUpdated;
    }

    public QuestTreasureData getQuestTreasureData() {
        return questTreasureData;
    }

    public void setQuestTreasureData(final QuestTreasureData questTreasureData) {
        this.questTreasureData = questTreasureData;
    }

    public enum TileItem {
        Ticket,
        Stamina,
        Card
    }
}
