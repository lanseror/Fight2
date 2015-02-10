package com.fight2.entity;

public class QuestResult {
    private int status;
    private int treasureIndex;
    private TileItem item;
    private Card card;
    private boolean treasureUpdated;
    private QuestTreasureData questTreasureData;
    private User enemy;
    private int stamina;

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

    public User getEnemy() {
        return enemy;
    }

    public void setEnemy(final User enemy) {
        this.enemy = enemy;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(final int stamina) {
        this.stamina = stamina;
    }

    public enum TileItem {
        Ticket,
        Stamina,
        CoinBag,
        Card
    }
}
