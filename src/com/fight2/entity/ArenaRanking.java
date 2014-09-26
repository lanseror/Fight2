package com.fight2.entity;

public class ArenaRanking {
    private int id;
    private User user;
    private int rankNumber;
    private int might;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

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

}
