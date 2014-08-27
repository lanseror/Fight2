package com.fight2.entity;

import java.util.List;

public class UserArenaInfo {
    private int rankNumber;
    private int might;
    private int win;
    private int lose;
    private String remainTime;

    private List<UserArenaRecord> arenaRecords;

    public int getRankNumber() {
        return rankNumber;
    }

    public void setRankNumber(final int rankNumber) {
        this.rankNumber = rankNumber;
    }

    public int getMight() {
        return might;
    }

    public void setMight(final int might) {
        this.might = might;
    }

    public int getWin() {
        return win;
    }

    public void setWin(final int win) {
        this.win = win;
    }

    public int getLose() {
        return lose;
    }

    public void setLose(final int lose) {
        this.lose = lose;
    }

    public String getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(final String remainTime) {
        this.remainTime = remainTime;
    }

    public List<UserArenaRecord> getArenaRecords() {
        return arenaRecords;
    }

    public void setArenaRecords(final List<UserArenaRecord> arenaRecords) {
        this.arenaRecords = arenaRecords;
    }

}
