package com.fight2.entity;

public class PartyInfo {
    private int id;
    private int hp;
    private int atk;

    private Party[] parties;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(final int hp) {
        this.hp = hp;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(final int atk) {
        this.atk = atk;
    }

    public Party[] getParties() {
        return parties;
    }

    public void setParties(final Party[] parties) {
        this.parties = parties;
    }

}
