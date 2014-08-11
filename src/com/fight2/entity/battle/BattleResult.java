package com.fight2.entity.battle;

import java.util.List;

public class BattleResult {

    private boolean isWinner;
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

}
