package com.fight2.entity.quest;

import com.fight2.entity.Card;
import com.fight2.entity.Dialog;
import com.fight2.entity.User;
import com.fight2.entity.quest.QuestTile.TileItem;

public class QuestResult {
    private QuestGoStatus status;
    private int treasureIndex;
    private TileItem item;
    private Card card;
    private boolean treasureUpdated;
    private QuestTreasureData questTreasureData;
    private User enemy;
    private int stamina;
    private int mineId;
    private Dialog dialog;

    public QuestGoStatus getStatus() {
        return status;
    }

    public void setStatus(final QuestGoStatus status) {
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

    public int getMineId() {
        return mineId;
    }

    public void setMineId(final int mineId) {
        this.mineId = mineId;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(final Dialog dialog) {
        this.dialog = dialog;
    }

}
