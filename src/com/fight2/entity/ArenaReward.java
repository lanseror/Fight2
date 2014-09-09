package com.fight2.entity;

import java.util.List;

public class ArenaReward {
    private int id;
    private ArenaRewardType type;
    private int min;
    private int max;
    private List<ArenaRewardItem> rewardItems;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public ArenaRewardType getType() {
        return type;
    }

    public void setType(final ArenaRewardType type) {
        this.type = type;
    }

    public int getMin() {
        return min;
    }

    public void setMin(final int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(final int max) {
        this.max = max;
    }

    public List<ArenaRewardItem> getRewardItems() {
        return rewardItems;
    }

    public void setRewardItems(final List<ArenaRewardItem> rewardItems) {
        this.rewardItems = rewardItems;
    }

    public enum ArenaRewardType {
        Might,
        Ranking
    }
}
