package com.fight2.entity.battle;

import java.util.List;

public class BattleResult {
    private boolean isWinner;
    private int baseMight;
    private int aliveMight;
    private int cwMight;
    private int totalMight;
    private int cwRate;
    private List<BattleRecord> battleRecord;

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(final boolean isWinner) {
        this.isWinner = isWinner;
    }

    public List<BattleRecord> getBattleRecord() {
        return battleRecord;
    }

    public void setBattleRecord(final List<BattleRecord> battleRecord) {
        this.battleRecord = battleRecord;
    }

    public int getBaseMight() {
        return baseMight;
    }

    public void setBaseMight(final int baseMight) {
        this.baseMight = baseMight;
    }

    public int getAliveMight() {
        return aliveMight;
    }

    public void setAliveMight(final int aliveMight) {
        this.aliveMight = aliveMight;
    }

    public int getCwMight() {
        return cwMight;
    }

    public void setCwMight(final int cwMight) {
        this.cwMight = cwMight;
    }

    public int getTotalMight() {
        return totalMight;
    }

    public void setTotalMight(final int totalMight) {
        this.totalMight = totalMight;
    }

    public int getCwRate() {
        return cwRate;
    }

    public void setCwRate(final int cwRate) {
        this.cwRate = cwRate;
    }

}
