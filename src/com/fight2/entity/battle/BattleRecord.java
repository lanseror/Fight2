package com.fight2.entity.battle;

public class BattleRecord {

    private String actionPlayer;
    private int atkParty;
    private int defenceParty;
    private int atk;
    private SkillRecord skill;

    public String getActionPlayer() {
        return actionPlayer;
    }

    public void setActionPlayer(final String actionPlayer) {
        this.actionPlayer = actionPlayer;
    }

    public int getAtkParty() {
        return atkParty;
    }

    public void setAtkParty(final int atkParty) {
        this.atkParty = atkParty;
    }

    public int getDefenceParty() {
        return defenceParty;
    }

    public void setDefenceParty(final int defenceParty) {
        this.defenceParty = defenceParty;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(final int atk) {
        this.atk = atk;
    }

    public SkillRecord getSkill() {
        return skill;
    }

    public void setSkill(final SkillRecord skill) {
        this.skill = skill;
    }

}
